package org.example.marketplaceservice.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.marketplaceservice.dto.JWTDTO;
import org.example.marketplaceservice.security.JWTUtil;
import org.example.marketplaceservice.services.PersonDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter { // Наследуется от OncePerRequestFilter, что гарантирует выполнение фильтра только один раз на запрос.

    private final JWTUtil jwtUtil;
    private final PersonDetailsService personDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    @Autowired
    public JWTFilter(JWTUtil jwtUtil, PersonDetailsService personDetailsService) {
        this.jwtUtil = jwtUtil;
        this.personDetailsService = personDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization"); // Получаем заголовок Authorization из запроса
        logger.info("Received Authorization header: {}", authHeader);
        // Проверяем, что заголовок присутствует, не пуст и начинается с "Bearer "
        if(authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")){
            String jwt = authHeader.substring(7); // Извлекаем токен JWT, отсекая "Bearer "

            if(jwt.isBlank()){ // Если токен пустой
                logger.warn("Invalid JWT-token or JWT-token is empty");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,"invalid jwt token in Bearer header"); // Отправляем ошибку 400
            }else{
                try {
                    JWTDTO jwtdto = jwtUtil.validateTokenAndRetrieveClaim(jwt); // Проверяем токен и получаем его полезную нагрузку
                    UserDetails userDetails = personDetailsService.loadUserByUsername(jwtdto.getLogin()); // Загружаем детали пользователя по логину

                    // Создаем объект аутентификации с пользовательскими данными
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                                    userDetails.getAuthorities());

                    // Проверяем, установлен ли уже объект аутентификации в контексте безопасности
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authToken); // Устанавливаем аутентификацию в контекс
                    }
                }catch (JWTVerificationException e){ // Обработка исключений, связанных с ошибкой валидации JWT
                    logger.warn("Verification exception");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,"invalid jwt token"); // Отправляем ошибку 400
                }
            }
        }
        logger.info("Verification is success");
        filterChain.doFilter(request,response); // Передаем управление следующему фильтру в цепочке
    }
}