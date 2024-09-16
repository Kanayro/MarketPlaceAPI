package org.example.marketplaceservice.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.marketplaceservice.dto.AuthenticationDTO;
import org.example.marketplaceservice.dto.JWTDTO;
import org.example.marketplaceservice.dto.PersonDTO;
import org.example.marketplaceservice.exceptions.ErrorResponse;
import org.example.marketplaceservice.exceptions.PersonNotCreatedException;
import org.example.marketplaceservice.exceptions.PersonNotFoundException;
import org.example.marketplaceservice.mappers.PersonMapper;
import org.example.marketplaceservice.models.Cart;
import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.security.JWTUtil;
import org.example.marketplaceservice.services.PersonService;
import org.example.marketplaceservice.services.RegistrationService;
import org.example.marketplaceservice.util.PersonValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class PersonController {

    private final PersonMapper personMapper;
    private final RegistrationService registrationService;
    private final AuthenticationManager authenticationManager;
    private final PersonService personService;
    private final JWTUtil jwtUtil;
    private final PersonValidator validator;
    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    public PersonController(PersonMapper personMapper, RegistrationService registrationService, AuthenticationManager authenticationManager,
                            PersonService personService, JWTUtil jwtUtil, PersonValidator validator) {
        this.personMapper = personMapper;
        this.registrationService = registrationService;
        this.authenticationManager = authenticationManager;
        this.personService = personService;
        this.jwtUtil = jwtUtil;
        this.validator = validator;
    }

    @PostMapping("/registration")
    public Map<String,String> register(@RequestBody @Valid PersonDTO personDTO, BindingResult result, HttpSession session) {
        // Преобразуем DTO в сущность Person
        Person person = personMapper.convertToPerson(personDTO);

        // Валидируем данные пользователя
        validator.validate(person, result);
        if (result.hasErrors()) {
            // Если есть ошибки валидации, собираем их в строку
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
            }
            logger.warn("Person is not created");
            // Выбрасываем исключение с сообщением об ошибке
            throw new PersonNotCreatedException(errorMsg.toString());
        }
        session.setAttribute("user", new Cart());
        // Регистрируем пользователя
        registrationService.register(person);
        // Генерируем JWT токен для зарегистрированного пользователя
        String token = jwtUtil.generateToken(personMapper.convertToJWTDTO(person));

        logger.info("Person is register");
        // Возвращаем JWT токен в ответе
        return Map.of("jwt-token", token);
    }

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody AuthenticationDTO authenticationDTO, HttpSession session) {
        // Создаем токен для аутентификации
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authenticationDTO.getLogin(),
                authenticationDTO.getPassword());
        try {
            // Пытаемся аутентифицировать пользователя
            authenticationManager.authenticate(token);
        } catch (BadCredentialsException e) {
            logger.warn("Bad credentials");
            // Если аутентификация не удалась, выбрасываем исключение
            throw new PersonNotFoundException("Invalid login or password");
        }

        // Находим пользователя и преобразуем в JWTDTO
        JWTDTO jwtdto = personMapper.convertToJWTDTO(personService.findByLogin(authenticationDTO.getLogin()));
        // Сохраняем объект Cart в сессии для пользователя
        session.setAttribute("user", new Cart());
        // Генерируем JWT токен для аутентифицированного пользователя
        String jToken = jwtUtil.generateToken(jwtdto);
        logger.info("Person is login");
        // Возвращаем JWT токен в ответе
        return Map.of("jwt-token", jToken);
    }

    // Обработчики исключений

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(PersonNotFoundException e){
        // Создаем объект ErrorResponse на основе исключения и текущего времени
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        // Возвращаем ответ с статусом BAD_REQUEST
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(PersonNotCreatedException e){
        // Обработка исключений, связанных с созданием пользователя
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(UsernameNotFoundException e){
        // Обработка исключений, возникающих при отсутствии пользователя
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
