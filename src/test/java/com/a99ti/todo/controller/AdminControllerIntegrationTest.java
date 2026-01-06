package com.a99ti.todo.controller;

import com.a99ti.todo.entity.Authority;
import com.a99ti.todo.entity.User;
import com.a99ti.todo.repository.UserRepository;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        User user = userTestUtil.createAndSaveDefaultAdminUser();

        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(user);

        ArrayList<String> authorities = new ArrayList<>();
        authorities.add("ROLE_EMPLOYEE");

        userTestUtil.createAndSaveCustomUser(
                "john1",
                "doe1",
                "johndoe1@email.com",
                "password",
                authorities
        );
        userTestUtil.createAndSaveCustomUser(
                "john2",
                "doe2",
                "johndoe2@email.com",
                "password",
                authorities
        );

        mockMvc.perform(get("/api/todo")
                .with(request -> {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect()



    }






}
