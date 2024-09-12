package org.example.marketplaceservice.integrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.marketplaceservice.dto.AuthenticationDTO;
import org.example.marketplaceservice.dto.PersonDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
//@Testcontainers
public class PersonControllerIntegrationTest {

//    @Container
//    @ServiceConnection
//    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;


    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    @WithMockUser
    @Transactional
    @Rollback
    public void shouldRegisterPersonAndGenerateToken() throws Exception {
        PersonDTO personDTO = new PersonDTO("name4","email4","login4","password4");
        String requestBody = mapper.writeValueAsString(personDTO);
        String token = "token";

        mockMvc.perform(post("/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                )
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser
    public void shouldLoginPersonAndGenerateTokenDoExist() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("login3","password2");
        String requestBody = mapper.writeValueAsString(authenticationDTO);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser
    public void shouldLoginPersonAndGenerateTokenDoesntExist() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("aaa","bbb");
        String requestBody = mapper.writeValueAsString(authenticationDTO);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest());

    }
}
