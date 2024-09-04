package org.example.marketplaceservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.marketplaceservice.dto.AuthenticationDTO;
import org.example.marketplaceservice.dto.JWTDTO;
import org.example.marketplaceservice.dto.PersonDTO;
import org.example.marketplaceservice.exceptions.PersonNotCreatedException;
import org.example.marketplaceservice.mappers.PersonMapper;
import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.security.JWTUtil;
import org.example.marketplaceservice.services.PersonDetailsService;
import org.example.marketplaceservice.services.PersonService;
import org.example.marketplaceservice.services.RegistrationService;
import org.example.marketplaceservice.util.PersonValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @Mock
    private PersonDetailsService service;

    @Mock
    private PersonMapper personMapper;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PersonService personService;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private PersonValidator validator;

    @InjectMocks
    private PersonController controller;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper = new ObjectMapper();
    }

    @Test
    public void shouldRegisterPersonAndGenerateToken() throws Exception {
        PersonDTO personDTO = new PersonDTO("name","email","login","password");
        String requestBody = mapper.writeValueAsString(personDTO);
        Person person = new Person();
        when(personMapper.convertToPerson(any(PersonDTO.class))).thenReturn(person);
        doNothing().when(validator).validate(any(Person.class),any(BindingResult.class));
        JWTDTO jwtdto = new JWTDTO();
        String token = "token";
        when(personMapper.convertToJWTDTO(any())).thenReturn(jwtdto);
        when(jwtUtil.generateToken(any(JWTDTO.class))).thenReturn(token);

        mockMvc.perform(post("/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt-token").value(token));

        verify(registrationService,times(1)).register(person);
        verify(jwtUtil,times(1)).generateToken(jwtdto);
    }

    @Test
    public void shouldLoginPersonAndGenerateTokenDoExist() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("login","password");
        String requestBody = mapper.writeValueAsString(authenticationDTO);

        Person person = new Person();
        when(personService.findByLogin(authenticationDTO.getLogin())).thenReturn(person);
        JWTDTO jwtdto = new JWTDTO();
        String token = "token";
        when(personMapper.convertToJWTDTO(any())).thenReturn(jwtdto);
        when(jwtUtil.generateToken(any(JWTDTO.class))).thenReturn(token);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt-token").value(token));
        verify(personService, times(1)).findByLogin(authenticationDTO.getLogin());
        verify(authenticationManager,times(1)).authenticate(any());
        verify(jwtUtil,times(1)).generateToken(jwtdto);
    }

    @Test
    public void shouldLoginPersonAndGenerateTokenDoesntExist() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("login","password");
        String requestBody = mapper.writeValueAsString(authenticationDTO);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid login or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest());
        verify(authenticationManager,times(1)).authenticate(any());

    }
}
