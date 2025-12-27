package com.example.ecom.project.controller;

import com.example.ecom.project.dto.JwtResponse;
import com.example.ecom.project.dto.LoginRequest;
import com.example.ecom.project.dto.SignupRequest;

import com.example.ecom.project.model.User;
import com.example.ecom.project.repo.UserRepository;
import com.example.ecom.project.security.jwt.JwtUtils;
import com.example.ecom.project.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    // --- 1. LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // Aici Spring Security verifică parola pentru noi!
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generăm token-ul
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Luăm detaliile userului ca să le trimitem înapoi
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream().findFirst().get().getAuthority();

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getFullName(),
                role));
    }

    // --- 2. REGISTER ---
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {

        // Verificăm dacă există deja emailul
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Eroare: Acest email este deja folosit!");
        }

        // Creăm contul nou
        // ATENȚIE: Setăm rolul default "ROLE_USER"
        User user = new User();
        user.setFullName(signUpRequest.getFullName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword())); // Criptăm parola!
        user.setRole("ROLE_USER");

        userRepository.save(user);

        return ResponseEntity.ok("Utilizator înregistrat cu succes!");
    }
}
