package com.doctors.backend.services.impl;

import com.doctors.backend.entity.User;
import com.doctors.backend.models.AuthResponse;
import com.doctors.backend.models.AuthenticationRequest;
import com.doctors.backend.repositories.UserRepository;
import com.doctors.backend.security.JwtService;
import com.doctors.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Override
    public AuthResponse login(AuthenticationRequest request) {
        AuthResponse token = new AuthResponse();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail());

        String jwt = jwtService.generateToken(user);
        token.setToken(jwt);

        return token;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
