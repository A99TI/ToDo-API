package com.a99ti.todo.service;

import com.a99ti.todo.request.RegisterRequest;

public interface AuthenticationService {
    void register(RegisterRequest input) throws Exception;
}
