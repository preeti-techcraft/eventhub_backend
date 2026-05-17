package com.educountry.eventhub.controller;

import com.educountry.eventhub.dto.ApiResponse;
import com.educountry.eventhub.dto.LoginRequest;
import com.educountry.eventhub.dto.RegisterRequest;
import com.educountry.eventhub.model.User;
import com.educountry.eventhub.repository.UserRepository;
import com.educountry.eventhub.security.CustomUserDetailsService;
import com.educountry.eventhub.security.JwtUtil;
import com.educountry.eventhub.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Invalid email or password", null));
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", jwt);
        responseData.put("user", userOpt.get());

        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", responseData));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest request) {
        User savedUser = authService.registerUser(request);
        
        // Generate token immediately upon registration
        final UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", jwt);
        responseData.put("user", savedUser);

        return ResponseEntity.ok(new ApiResponse<>(true, "Registration successful", responseData));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        if (email == null || email.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Email and new password are required.", null));
        }

        try {
            authService.resetPassword(email, newPassword);
            return ResponseEntity.ok(new ApiResponse<>(true, "Password reset successfully. You can now log in.", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
