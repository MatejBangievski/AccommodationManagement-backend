package com.example.emtlab.integration.security;

import com.example.emtlab.integration.controller.AbstractControllerTest;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.Role;
import com.example.emtlab.service.domain.UserService;
import com.example.emtlab.helpers.JwtHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
public class JwtIntegrationTest extends AbstractControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper jwtHelper;

    private User testUser;
    private String validToken;
    private String expiredToken;
    private String tamperedToken;

    @BeforeEach
    void setup() {
        userService.deleteAll();

        testUser = userService.register(
                "testuser",
                "pass",
                "pass",
                "Test",
                "User",
                Role.ROLE_USER
        );

        validToken = "Bearer " + jwtHelper.generateToken(testUser);
        expiredToken = "Bearer " + jwtHelper.generateExpiredToken(testUser.getUsername());
        tamperedToken = validToken + "tampered";
    }

    @Test
    void accessProtectedEndpoint_withValidToken() throws Exception {
        mockMvc.perform(get("/api/user/{username}", testUser.getUsername())
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUser.getUsername()));
    }

    @Test
    void accessProtectedEndpoint_withExpiredToken() throws Exception {
        mockMvc.perform(get("/api/user/{username}", testUser.getUsername())
                        .header("Authorization", expiredToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("The token has expired."));
    }

    @Test
    void accessProtectedEndpoint_withTamperedToken() throws Exception {
        mockMvc.perform(get("/api/user/{username}", testUser.getUsername())
                        .header("Authorization", tamperedToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("The token's signature is invalid."));
    }

    @Test
    void accessProtectedEndpoint_withInvalidToken() throws Exception {
        mockMvc.perform(get("/api/user/{username}", testUser.getUsername())
                        .header("Authorization", "Bearer invalidtoken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("The token is invalid."));
    }

    @Test
    void accessProtectedEndpoint_withoutToken() throws Exception {
        mockMvc.perform(get("/api/user/{username}", testUser.getUsername())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Currently no restrictions in my security config.
                .andExpect(jsonPath("$.username").value(testUser.getUsername()));
    }
}
