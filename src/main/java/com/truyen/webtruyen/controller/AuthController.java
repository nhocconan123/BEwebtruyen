package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.dto.auth.AuthResponse;
import com.truyen.webtruyen.dto.auth.LoginRequest;
import com.truyen.webtruyen.dto.auth.RegisterRequest;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.enums.UserRole;
import com.truyen.webtruyen.entity.enums.UserStatus;
import com.truyen.webtruyen.repository.UserRepository;
import com.truyen.webtruyen.security.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(BAD_REQUEST, "Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAvatar(request.getAvatar());
        user.setBio(request.getBio());
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        return new AuthResponse(token, savedUser.getId(), savedUser.getUsername(), savedUser.getRole().name());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid credentials"));
        if (user.getStatus() != null && user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(UNAUTHORIZED, "User is locked");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));
        } catch (BadCredentialsException ex) {
            // Allow legacy plain-text rows created before JWT/password encoder was added.
            if (!request.getPassword().equals(user.getPassword())) {
                throw new ResponseStatusException(UNAUTHORIZED, "Invalid credentials");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user = userRepository.save(user);
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }
}
