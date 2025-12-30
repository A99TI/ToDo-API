package com.a99ti.todo.service;

import com.a99ti.todo.request.AuthenticationRequest;
import com.a99ti.todo.request.RegisterRequest;
import com.a99ti.todo.response.AuthenticationResponse;

public interface AuthenticationService {
    void register(RegisterRequest input) throws Exception;
    AuthenticationResponse login(AuthenticationRequest request);
}
