package org.example.marketplaceservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.marketplaceservice.dto.AuthenticationDTO;
import org.example.marketplaceservice.dto.PersonDTO;
import org.example.marketplaceservice.exceptions.PersonNotCreatedException;
import org.example.marketplaceservice.mappers.PersonMapper;
import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.security.JWTUtil;
import org.example.marketplaceservice.services.PersonService;
import org.example.marketplaceservice.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @Autowired
    public PersonController(PersonMapper personMapper, RegistrationService registrationService,
                            AuthenticationManager authenticationManager, PersonService personService, JWTUtil jwtUtil) {
        this.personMapper = personMapper;
        this.registrationService = registrationService;
        this.authenticationManager = authenticationManager;
        this.personService = personService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/registration")
    public Map<String,String> register(@RequestBody @Valid PersonDTO personDTO, BindingResult result) {
        Person person = personMapper.convertToPerson(personDTO);

        if(result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = result.getFieldErrors();

            for(FieldError error : errors){
                errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
            }
            throw new PersonNotCreatedException(errorMsg.toString());
        }
        registrationService.register(person);

        String token = jwtUtil.generateToken(person);

        return Map.of("jwt-token",token);

    }

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody @Valid AuthenticationDTO authenticationDTO, BindingResult result) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authenticationDTO.getLogin(),
                authenticationDTO.getPassword());

        try{
            authenticationManager.authenticate(token);
        }catch (BadCredentialsException e){
            return Map.of("error","Bad credentials");
        }
        Person person = personService.findByLogin(authenticationDTO.getLogin());
        String jToken = jwtUtil.generateToken(person);

        return Map.of("jwt-token",jToken);

    }

    @GetMapping("/per")
    public String getPerson(HttpServletRequest request){

        return "sdsdsdsdsd";

    }
}
