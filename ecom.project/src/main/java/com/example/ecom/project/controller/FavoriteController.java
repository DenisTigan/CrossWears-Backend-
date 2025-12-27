package com.example.ecom.project.controller;


import com.example.ecom.project.model.Product;
import com.example.ecom.project.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // 1. TOGGLE
    @PostMapping("/toggle/{productId}")
    public ResponseEntity<?> toggleFavorite(@PathVariable int productId) {
        try {
            String email = getCurrentUserEmail();
            String result = favoriteService.toggleFavorite(email, productId);
            return ResponseEntity.ok(result); // Returnează "Added" sau "Removed"
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. CHECK
    @GetMapping("/check/{productId}")
    public ResponseEntity<Boolean> isFavorite(@PathVariable int productId) {
        String email = getCurrentUserEmail();
        boolean exists = favoriteService.isFavorite(email, productId);
        return ResponseEntity.ok(exists);
    }

    // 3. MY LIST
    @GetMapping("/my-list")
    public ResponseEntity<List<Product>> getMyFavorites() {
        String email = getCurrentUserEmail();
        List<Product> products = favoriteService.getFavoritesForUser(email);
        return ResponseEntity.ok(products);
    }

    // Metodă privată simplă pentru a extrage email-ul din Token
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


}
