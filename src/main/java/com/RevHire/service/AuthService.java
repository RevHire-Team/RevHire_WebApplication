package com.RevHire.service;

import com.RevHire.entity.User;

public interface AuthService {

    User registerUser(User user);

    User login(String email, String password);

    String resetPassword(String email, String securityAnswer, String newPassword);
}
