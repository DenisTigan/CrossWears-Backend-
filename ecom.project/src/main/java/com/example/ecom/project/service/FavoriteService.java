package com.example.ecom.project.service;


import com.example.ecom.project.model.Favorite;
import com.example.ecom.project.model.Product;
import com.example.ecom.project.model.User;
import com.example.ecom.project.repo.FavoriteRepository;
import com.example.ecom.project.repo.ProductsRepository;
import com.example.ecom.project.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductsRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // --- 1. TOGGLE (Adaugă / Șterge) ---
    @Transactional // Important pentru operațiile de Delete/Save
    public String toggleFavorite(String userEmail, int productId) {
        // Găsim userul
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Găsim produsul
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Verificăm dacă există deja relația
        Optional<Favorite> existing = favoriteRepository.findByUserAndProduct(user, product);

        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return "Removed";
        } else {
            Favorite favorite = new Favorite(user, product);
            favoriteRepository.save(favorite);
            return "Added";
        }
    }

    // --- 2. GET LIST (Doar produsele) ---
    public List<Product> getFavoritesForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Favorite> favorites = favoriteRepository.findByUser(user);

        // Transformăm lista de Favorite (relații) în lista de Products (obiecte)
        return favorites.stream()
                .map(Favorite::getProduct)
                .collect(Collectors.toList());
    }

    // --- 3. CHECK (Verifică existența) ---
    public boolean isFavorite(String userEmail, int productId) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return false;

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return false;

        return favoriteRepository.existsByUserAndProduct(user, product);
    }




}
