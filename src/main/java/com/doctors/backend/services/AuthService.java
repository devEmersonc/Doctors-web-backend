package com.doctors.backend.services;

import com.doctors.backend.entity.User;
import com.doctors.backend.models.AuthResponse;
import com.doctors.backend.models.AuthenticationRequest;

public interface AuthService {

    public AuthResponse login(AuthenticationRequest request);

    User findByEmail(String email);
}
