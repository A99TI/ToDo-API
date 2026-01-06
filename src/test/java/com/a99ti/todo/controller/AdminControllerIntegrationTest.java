package com.a99ti.todo.controller;

import com.a99ti.todo.entity.Authority;
import com.a99ti.todo.entity.User;
import com.a99ti.todo.repository.UserRepository;
import com.a99ti.todo.response.UserResponse;
import com.a99ti.todo.util.UserTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AdminControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTestUtil userTestUtil;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws Exception{
        User adminUser = userTestUtil.createAndSaveDefaultAdminUser();
        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser);

        ArrayList<String> authorities = new ArrayList<>();
        authorities.add("ROLE_EMPLOYEE");

        User user1 = userTestUtil.createAndSaveCustomUser(
                "john1",
                "doe1",
                "johndoe1@email.com",
                "password",
                authorities
        );
        User user2 = userTestUtil.createAndSaveCustomUser(
                "john2",
                "doe2",
                "johndoe2@email.com",
                "password",
                authorities
        );

        mockMvc.perform(get("/api/admin")
                        .with(request -> {
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                // Check all emails exist
                .andExpect(jsonPath("$[?(@.email == '" + adminUser.getEmail() + "')]").exists())
                .andExpect(jsonPath("$[?(@.email == 'johndoe1@email.com')]").exists())
                .andExpect(jsonPath("$[?(@.email == 'johndoe2@email.com')]").exists());
    }

    @Test
    void deleteUser_ShouldDeleteStoredUser() throws Exception{
        User adminUser = userTestUtil.createAndSaveDefaultAdminUser();

        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser);

        User user1 = userTestUtil.createAndSaveDefaultUser();

        mockMvc.perform(delete("/api/admin/" + user1.getId())
                .with(request -> {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    return request;
                }))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.findById(user1.getId()).isPresent(), "Created user should not exist after deletion");

    }

    @Test
    void promoteUser_ShouldPromoteUserToAdmin() throws Exception{
        User adminUser = userTestUtil.createAndSaveDefaultAdminUser();

        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(adminUser);

        User user1 = userTestUtil.createAndSaveDefaultUser();

        mockMvc.perform(put("/api/admin/" + user1.getId() + "/role")
                .with(request -> {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    return request;
                }))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.fullName").value("john doe"))
                .andExpect(jsonPath("$.email").value("johndoe@email.com"))
                .andExpect(jsonPath("$.authorities").isArray())
                .andExpect(jsonPath("$.authorities[*].authority").value(org.hamcrest.Matchers.containsInAnyOrder("ROLE_EMPLOYEE", "ROLE_ADMIN")));
    }








}
