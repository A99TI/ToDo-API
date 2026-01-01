package com.a99ti.todo.service;

import com.a99ti.todo.entity.Authority;
import com.a99ti.todo.entity.User;
import com.a99ti.todo.repository.UserRepository;
import com.a99ti.todo.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(this::convertToUserResponse).toList();
    }

    @Override
    @Transactional
    public UserResponse promoteToAdmin(long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
        if (user.get().getAuthorities().stream().anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already an admin");
        }

        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority("ROLE_EMPLOYEE"));
        authorities.add(new Authority("ROLE_ADMIN"));
        user.get().setAuthorities(authorities);

        User savedUser = userRepository.save(user.get());

        return convertToUserResponse(savedUser);

    }

    private UserResponse convertToUserResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getAuthorities().stream().map(auth -> (Authority) auth).toList()
        );
    }
}
