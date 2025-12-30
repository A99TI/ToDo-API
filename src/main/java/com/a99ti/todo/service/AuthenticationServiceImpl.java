package com.a99ti.todo.service;

import com.a99ti.todo.entity.Authority;
import com.a99ti.todo.entity.User;
import com.a99ti.todo.repository.UserRepository;
import com.a99ti.todo.request.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void register(RegisterRequest input) throws Exception {
        if (isEmailTaken(input.getEmail())){
            throw new Exception("Email already taken");
        }
        User user = buildNewUser(input);
        userRepository.save(user);

    }

    private boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }


    private User buildNewUser(RegisterRequest input){
        User user = new User();

        user.setId(0);
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setEmail(input.getEmail());
        user.setEmail(passwordEncoder.encode(input.getPassword()));

        user.setAuthorities(initialAuthority());
        return user;
    }

    private List<Authority> initialAuthority(){
        boolean isFirstUser = userRepository.count() == 0;
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority("ROLE_EMPLOYEE"));
        if(isFirstUser){
            authorities.add(new Authority("ROLE_ADMIN"));
        }
        return authorities;
    }
}
