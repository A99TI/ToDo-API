package com.a99ti.todo.service;

import com.a99ti.todo.entity.User;
import com.a99ti.todo.request.PasswordUpdateRequest;
import com.a99ti.todo.response.UserResponse;

public interface UserService {
    UserResponse getUserInfo();
    void deleteUser();
    void updatePassword(PasswordUpdateRequest passwordUpdateRequest);
}
