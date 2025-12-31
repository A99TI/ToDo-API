package com.a99ti.todo.service;

import com.a99ti.todo.entity.Todo;
import com.a99ti.todo.entity.User;
import com.a99ti.todo.repository.TodoRepository;
import com.a99ti.todo.request.TodoRequest;
import com.a99ti.todo.response.TodoResponse;
import com.a99ti.todo.util.FindAuthenticatedUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService{

    private final TodoRepository todoRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;

    public TodoServiceImpl(TodoRepository todoRepository, FindAuthenticatedUser findAuthenticatedUser) {
        this.todoRepository = todoRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
    }

    @Override
    public List<TodoResponse> getAllTodos() {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();

        return todoRepository.findByOwner(currentUser)
                .stream()
                .map(this::covertToTodoResponse)
                .toList();
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

        return covertToTodoResponse(savedTodo);

    }


    private TodoResponse covertToTodoResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getPriority(),
                todo.isComplete()
        );
    }
}
