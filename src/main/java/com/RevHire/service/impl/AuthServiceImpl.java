package com.RevHire.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RevHire.entity.User;
import com.RevHire.repository.UserRepository;
import com.RevHire.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    public void AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {

        Optional<User> user = userRepository.findByEmail(email);

        if(user.isPresent() && user.get().getPasswordHash().equals(password)) {
            return user.get();
        }

        throw new RuntimeException("Invalid credentials");
    }

    @Override
    public String resetPassword(String email, String securityAnswer, String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getSecurityAnswerHash().equals(securityAnswer)) {
            user.setPasswordHash(newPassword);
            userRepository.save(user);
            return "Password updated successfully";
        }

        throw new RuntimeException("Security answer incorrect");
    }
}