package com.example.techprep.service;

import com.example.techprep.dto.AuthResponse;
import com.example.techprep.dto.AuthRequest;
import com.example.techprep.dto.SignupRequest;
import com.example.techprep.entity.User;
import com.example.techprep.repository.UserRepository;
import com.example.techprep.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(SignupRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        User saved = userRepository.save(u);

        String token = jwtUtil.generateToken(saved.getEmail());
        return new AuthResponse(token, saved.getId(), saved.getName(), saved.getEmail());
    }

    public AuthResponse authenticate(AuthRequest req) {
        Optional<User> found = userRepository.findByEmail(req.getEmail());
        if (found.isEmpty()) throw new IllegalArgumentException("Invalid credentials");
        User u = found.get();
        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(u.getEmail());
        return new AuthResponse(token, u.getId(), u.getName(), u.getEmail());
    }
}
