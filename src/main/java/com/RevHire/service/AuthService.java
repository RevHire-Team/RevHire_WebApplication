package com.RevHire.service;

import com.RevHire.entity.User;

public interface AuthService {

    User registerUser(User user);

    User login(String email, String password);

    String resetPassword(String email, String securityAnswer, String newPassword);

    void updatePassword(Long userId, String currentPassword, String newPassword);

    void deleteUser(Long userId);
}
