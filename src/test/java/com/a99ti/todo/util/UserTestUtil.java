package com.a99ti.todo.util;

import com.a99ti.todo.entity.Authority;
import com.a99ti.todo.entity.User;
import com.a99ti.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserTestUtil {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createAndSaveDefaultAdminUser(){
        User user = new User();
        user.setFirstName("john");
        user.setLastName("doe");
        user.setEmail("johndoe@email.com");
        user.setPassword(passwordEncoder.encode("password123"));

        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority("ROLE_EMPLOYEE"));
        authorities.add(new Authority("ROLE_ADMIN"));
        user.setAuthorities(authorities);

        return userRepository.save(user);
    }

    public User createAndSaveDefaultUser(){
        User user = new User();
        user.setFirstName("john");
        user.setLastName("doe");
        user.setEmail("johndoe@email.com");
        user.setPassword(passwordEncoder.encode("password123"));

        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority("ROLE_EMPLOYEE"));
        user.setAuthorities(authorities);

        return userRepository.save(user);
    }

    public User createAndSaveCustomUser(String firstName, String lastName, String email, String password, ArrayList<String> roles){
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        List<Authority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new Authority(role));
        }
        user.setAuthorities(authorities);

        return userRepository.save(user);
    }

    public UsernamePasswordAuthenticationToken createAuthenticationToken(User user){
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
    }

}
