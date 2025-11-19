package com.example.techprep.controller;

import com.example.techprep.dto.AuthRequest;
import com.example.techprep.dto.AuthResponse;
import com.example.techprep.dto.SignupRequest;
import com.example.techprep.service.UserService;
import com.example.techprep.service.GoogleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final GoogleService googleService;

    public AuthController(UserService userService, GoogleService googleService) {
        this.userService = userService;
        this.googleService = googleService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        try {
            AuthResponse resp = userService.register(req);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody AuthRequest req) {
        try {
            AuthResponse resp = userService.authenticate(req);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // Admin Google OAuth helpers
    @GetMapping("/oauth2/google/admin/url")
    public ResponseEntity<?> getAdminGoogleAuthUrl(@RequestParam String adminEmail) {
        String url = googleService.buildAuthorizationUrl(adminEmail);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/oauth2/google/callback")
    public void googleOAuthCallback(@RequestParam(name = "code", required = false) String code,
                                    @RequestParam(name = "state", required = false) String state,
                                    jakarta.servlet.http.HttpServletResponse response) throws IOException {
        if (code == null) {
            response.sendError(400, "Missing code");
            return;
        }
        String adminEmail = state;
        try {
            googleService.handleCallbackAndStoreTokens(code, adminEmail);
            // Redirect to frontend home page after successful connection
            response.sendRedirect("http://localhost:3000/");
        } catch (IOException e) {
            response.sendError(500, "Google token exchange failed: " + e.getMessage());
        } catch (Exception e) {
            response.sendError(500, "Failed to connect Google admin account: " + e.getMessage());
        }
    }
}