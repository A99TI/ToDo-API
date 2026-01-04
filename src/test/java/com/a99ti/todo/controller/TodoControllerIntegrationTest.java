package com.a99ti.todo.controller;

import com.a99ti.todo.entity.Todo;
import com.a99ti.todo.entity.User;
import com.a99ti.todo.repository.TodoRepository;
import com.a99ti.todo.request.TodoRequest;
import com.a99ti.todo.response.TodoResponse;
import com.a99ti.todo.util.UserTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TodoControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TodoRepository todoRepository;

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
    void createToDo_ShouldReturnNewTodo() throws Exception{

        User user = userTestUtil.createAndSaveDefaultAdminUser();

        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(user);

        TodoRequest todoRequest = new TodoRequest(
                "TestRequest",
                "A test basic request",
                5
        );

        mockMvc.perform(post("/api/todos")
                        .with(request -> {
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            return request;
                        })
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("TestRequest"))
                .andExpect(jsonPath("$.description").value("A test basic request"))
                .andExpect(jsonPath("$.priority").value(5))
                .andExpect(jsonPath("$.completed").value(false));


    }

    @Test
    void getToDo_shouldReturnTodo() throws Exception{
        User user = userTestUtil.createAndSaveDefaultUser();

        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(user);

        Todo todo = new Todo(
                "TestRequest",
                "A test basic request",
                5,
                false,
                user
        );

        Todo savedTodo = todoRepository.save(todo);

        mockMvc.perform(get("/api/todos")
                .with(request -> {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(savedTodo.getId()))
                .andExpect(jsonPath("$[0].title").value(savedTodo.getTitle()))
                .andExpect(jsonPath("$[0].description").value(savedTodo.getDescription()))
                .andExpect(jsonPath("$[0].priority").value(savedTodo.getPriority()))
                .andExpect(jsonPath("$[0].completed").value(savedTodo.isComplete()));

    }

    @Test
    void deleteTodo_shouldDeleteToDo() throws Exception{
        User user = userTestUtil.createAndSaveDefaultUser();

        UsernamePasswordAuthenticationToken auth = userTestUtil.createAuthenticationToken(user);

        Todo todo = new Todo(
                "TestRequest",
                "A test basic request",
                5,
                false,
                user
        );

        Todo savedTodo = todoRepository.save(todo);

        mockMvc.perform(delete("/api/todos/" + savedTodo.getId())
                .with(request -> {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    return request;
                }))
                .andExpect(status().isNoContent());

        assertFalse(todoRepository.findById(savedTodo.getId()).isPresent(), "To do should not exist after deletion");


    }
    

}
