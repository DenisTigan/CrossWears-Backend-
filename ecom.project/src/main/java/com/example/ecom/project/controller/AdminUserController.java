package com.example.ecom.project.controller;


import com.example.ecom.project.model.User;
import com.example.ecom.project.repo.FavoriteRepository;
import com.example.ecom.project.repo.MessageRepository;
import com.example.ecom.project.repo.ReviewRepository;
import com.example.ecom.project.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin") // Ruta de bază securizată deja
@CrossOrigin(origins = "*") // Permite accesul din frontend
public class AdminUserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository; // <--- ADAUGĂ ASTA

     @Autowired
     private ReviewRepository reviewRepository; // <--- OPTIONAL: Pentru recenzii

    @Autowired
    private FavoriteRepository favoriteRepository;

    @GetMapping("/users")
    public List<com.example.ecom.project.model.User> getAllUsers() {
        return userRepository.findAll();
    }

    // UPDATE: Metoda de ștergere cu curățare prealabilă
    @DeleteMapping("/users/{id}")
    @Transactional // Critic: Totul sau nimic
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User userToDelete = userRepository.findById(id).orElse(null);

        if (userToDelete == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // 1. Ștergem Mesajele (Trimise și Primite)
            messageRepository.deleteBySenderId(id);
            messageRepository.deleteByReceiverId(id);

            // 2. Ștergem Recenziile (după nume)
            if (userToDelete.getFullName() != null) {
                reviewRepository.deleteByReviewerName(userToDelete.getFullName());
            }

            // 3. Ștergem FAVORITELE (Asta rezolvă eroarea ta curentă)
            favoriteRepository.deleteByUserId(id);

            // 4. Ștergem Userul
            userRepository.deleteById(id);

            return ResponseEntity.ok("Utilizator și toate datele asociate au fost șterse!");

        } catch (Exception e) {
            e.printStackTrace();
            // Dacă tot crapă, înseamnă că a mai rămas ceva (probabil Comenzi/Orders)
            return ResponseEntity.status(409).body("Nu pot șterge utilizatorul! Eroare: " + e.getMessage());
        }
    }
}

