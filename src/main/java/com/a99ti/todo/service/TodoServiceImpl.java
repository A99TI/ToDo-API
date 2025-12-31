package com.a99ti.todo.service;

import com.a99ti.todo.entity.Todo;
import com.a99ti.todo.entity.User;
import com.a99ti.todo.repository.TodoRepository;
import com.a99ti.todo.request.TodoRequest;
import com.a99ti.todo.response.TodoResponse;
import com.a99ti.todo.util.FindAuthenticatedUser;
import org.springframework.stereotype.Service;

@Service
public class TodoServiceImpl implements TodoService{

    private final TodoRepository todoRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;

    public TodoServiceImpl(TodoRepository todoRepository, FindAuthenticatedUser findAuthenticatedUser) {
        this.todoRepository = todoRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
    }

    @Override
    public TodoResponse createTodo(TodoRequest todoRequest) {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();

        Todo todo = new Todo(
                todoRequest.getTitle(),
                todoRequest.getDescription(),
                todoRequest.getPriority(),
                false,
                currentUser
        );

        Todo savedTodo = todoRepository.save(todo);

        TodoResponse todoResponse = new TodoResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getDescription(),
                savedTodo.getPriority(),
                savedTodo.isComplete()
        );

        return todoResponse;

    }
}
