package com.a99ti.todo.service;

import com.a99ti.todo.request.TodoRequest;
import com.a99ti.todo.response.TodoResponse;

public interface TodoService {
    TodoResponse createTodo(TodoRequest todoRequest);
}
