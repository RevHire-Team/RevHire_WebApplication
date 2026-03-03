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

    @Override
    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User session expired or user not found"));

        // Check if the provided current password matches what's in the DB
        if (!user.getPasswordHash().equals(currentPassword)) {
            throw new RuntimeException("The current password you entered is incorrect");
        }

        user.setPasswordHash(newPassword);
        userRepository.save(user);
    }

    // Inside AuthService.java
    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}