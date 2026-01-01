package com.a99ti.todo.service;

import com.a99ti.todo.response.UserResponse;

import java.util.List;

public interface AdminService {
    List<UserResponse> getAllUsers();
}
