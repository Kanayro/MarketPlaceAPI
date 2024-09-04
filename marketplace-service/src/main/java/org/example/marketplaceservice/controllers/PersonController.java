package org.example.marketplaceservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.marketplaceservice.dto.AuthenticationDTO;
import org.example.marketplaceservice.dto.JWTDTO;
import org.example.marketplaceservice.dto.PersonDTO;
import org.example.marketplaceservice.exceptions.ErrorResponse;
import org.example.marketplaceservice.exceptions.PersonNotCreatedException;
import org.example.marketplaceservice.exceptions.PersonNotFoundException;
import org.example.marketplaceservice.exceptions.ProductNotFoundException;
import org.example.marketplaceservice.mappers.PersonMapper;
import org.example.marketplaceservice.models.Cart;
import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.security.JWTUtil;
import org.example.marketplaceservice.services.PersonService;
import org.example.marketplaceservice.services.RegistrationService;
import org.example.marketplaceservice.util.PersonValidator;
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
    public Map<String,String> register(@RequestBody @Valid PersonDTO personDTO, BindingResult result) {
        Person person = personMapper.convertToPerson(personDTO);
        validator.validate(person,result);
        if(result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = result.getFieldErrors();

            for(FieldError error : errors){
                errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
            }
            throw new PersonNotCreatedException(errorMsg.toString());
        }
        registrationService.register(person);
        String token = jwtUtil.generateToken(personMapper.convertToJWTDTO(person));

        return Map.of("jwt-token",token);

    }

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody AuthenticationDTO authenticationDTO, HttpSession session) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authenticationDTO.getLogin(),
                authenticationDTO.getPassword());
        try{
            authenticationManager.authenticate(token);
        }catch (BadCredentialsException e){
            throw new PersonNotFoundException("Invalid login or password");
        }
        JWTDTO jwtdto = personMapper.convertToJWTDTO(personService.findByLogin(authenticationDTO.getLogin()));
        session.setAttribute("user", new Cart());
        String jToken = jwtUtil.generateToken(jwtdto);

        return Map.of("jwt-token",jToken);

    }

    //Exception handlers

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(PersonNotFoundException e){
        ErrorResponse response = new ErrorResponse(e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(PersonNotCreatedException e){
        ErrorResponse response = new ErrorResponse(e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(UsernameNotFoundException e){
        ErrorResponse response = new ErrorResponse(e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
