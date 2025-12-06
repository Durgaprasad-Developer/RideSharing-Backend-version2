package com.Durgaprasad.demo.services;

import com.Durgaprasad.demo.dto.AuthResponse;
import com.Durgaprasad.demo.dto.LoginRequest;
import com.Durgaprasad.demo.dto.RegisterRequest;
import com.Durgaprasad.demo.models.User;
import com.Durgaprasad.demo.repository.UserRepository;
import com.Durgaprasad.demo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req){
        if(userRepository.existsByUsername(req.getUsername())){
            throw new RuntimeException("Username already exists");
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(req.getRole());
        userRepository.save(u);

        AuthResponse res = new AuthResponse();
        res.setUsername(u.getUsername());
        res.setRole(u.getRole());
        return res;
    }

    public AuthResponse login(LoginRequest req){
        var user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if(!passwordEncoder.matches(req.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        AuthResponse r = new AuthResponse();
        r.setToken(token);
        r.setRole(user.getRole());
        r.setUsername(user.getUsername());
        return r;
    }
}
