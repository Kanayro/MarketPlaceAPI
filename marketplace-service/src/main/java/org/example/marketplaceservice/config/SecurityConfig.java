package org.example.marketplaceservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Аннотация для активации конфигурации безопасности Spring Security в приложении.
@EnableMethodSecurity // Аннотация, позволяющая применять безопасность на уровне методов, используя аннотации, такие как @PreAuthorize.
public class SecurityConfig { // Класс, отвечающий за настройку аспектов безопасности приложения.

    private final JWTFilter jwtFilter;

    @Autowired
    public SecurityConfig( JWTFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    // Определение SecurityFilterChain для настройки параметров безопасности HTTP.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Отключение защиты от CSRF (Cross-Site Request Forgery).
                .authorizeHttpRequests((requests) -> requests // Настройка авторизации для HTTP-запросов.
                        .requestMatchers("/auth/login", "/auth/registration", "/error").permitAll() // Эти URL доступны всем без аутентификации.
                        .anyRequest().authenticated()) // Все остальные запросы требуют аутентификации.
                .sessionManagement((session) -> session // Настройка управления сессиями.
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Политика создания сессий: создается только по мере необходимости.
                        .maximumSessions(3)) // Установка ограничения на максимальное количество сессий для одного пользователя (3).
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Добавление JWT фильтра перед UsernamePasswordAuthenticationFilter.
        return http.build(); // Строим и возвращаем объект SecurityFilterChain.
    }

    // Определение PasswordEncoder для хеширования паролей.
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(); // Возвращаем экземпляр BCryptPasswordEncoder для безопасного хеширования паролей.
    }

    // Определение AuthenticationManager для управления аутентификацией.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // Возвращаем AuthenticationManager из конфигурации аутентификации.
    }
}