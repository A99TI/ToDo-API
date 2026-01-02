package com.a99ti.todo.controller;

import com.a99ti.todo.entity.Authority;
import com.a99ti.todo.entity.User;
import com.a99ti.todo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    private User createAndSaveUser(String firstName, String lastName, String email, String password, ArrayList<String> roles){
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        List<Authority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new Authority(role));
        }
        user.setAuthorities(authorities);

        return userRepository.save(user);
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(User user){
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
    }

    @Test
    void getUserInfo_WithValidJwtToken_ShouldReturnUserDetails() throws Exception {
        ArrayList<String> roles = new ArrayList<>(List.of("ROLE_EMPLOYEE", "ROLE_ADMIN"));

        User user = createAndSaveUser("john", "doe", "johndoe@email.com", "password123",
                roles);

        UsernamePasswordAuthenticationToken auth = createAuthenticationToken(user);

        // Get user info
        mockMvc.perform(get("/api/users/info")
                        .with(request -> {
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.fullName").value("john doe"))
                .andExpect(jsonPath("$.email").value("johndoe@email.com"))
                .andExpect(jsonPath("$.authorities").isArray())
                .andExpect(jsonPath("$.authorities[*].authority").value(org.hamcrest.Matchers.containsInAnyOrder("ROLE_EMPLOYEE", "ROLE_ADMIN")));
    }

    @Test
    void deleteNonAdminUser_WithValidJwtToken_ShouldDeleteUser() throws Exception {
        ArrayList<String> roles = new ArrayList<>(List.of("ROLE_EMPLOYEE"));

        User user = createAndSaveUser("john", "doe", "johndoe@email.com", "password123",
                roles);

        UsernamePasswordAuthenticationToken auth = createAuthenticationToken(user);

        // Delete User
        mockMvc.perform(delete("/api/users")
        .with(request -> {
            SecurityContextHolder.getContext().setAuthentication(auth);
            return request;
        }))
        .andExpect(status().isOk());

        assertFalse(userRepository.findById(user.getId()).isPresent(), "User should not exist after deletion");;

    }
}