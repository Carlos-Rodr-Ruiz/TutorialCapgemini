package com.ccsw.tutorial.auth;

import com.ccsw.tutorial.auth.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void initDatabase() {
        if (userRepository.findByUsername("admin") == null) {
            User user = new User();
            user.setUsername("admin");
            String encodedPassword = passwordEncoder.encode("admin");  // Cifra la contraseña
            user.setPassword(encodedPassword);
            user.setRole("ROLE_ADMIN");

            userRepository.save(user);
            System.out.println("Usuario admin creado");
        }
    }
}
