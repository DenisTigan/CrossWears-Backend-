package com.example.ecom.project.controller;


import com.example.ecom.project.model.Order;
import com.example.ecom.project.model.User;
import com.example.ecom.project.repo.OrderRepository;
import com.example.ecom.project.repo.UserRepository;
import com.example.ecom.project.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // FĂRĂ @PreAuthorize - Securitatea e gestionată în WebSecurityConfig
    @GetMapping("/admin/all")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // FĂRĂ @PreAuthorize
    @PutMapping("/admin/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return orderRepository.findById(id).map(order -> {
            String oldStatus = order.getStatus();
            String newStatus = body.get("status");

            order.setStatus(newStatus);
            orderRepository.save(order);

            // LOG PENTRU DEBUG
            System.out.println("Status vechi: " + oldStatus + " | Status nou: " + newStatus);

            if (!"PAID".equals(oldStatus) && "PAID".equals(newStatus)) {
                User user = userRepository.findByEmail(order.getUserEmail()).orElse(null);
                if (user != null) {
                    user.setOrderCount(user.getOrderCount() + 1);
                    userRepository.save(user);
                    System.out.println("Contor incrementat pentru: " + user.getEmail() + ". Nou count: " + user.getOrderCount());
                }
            }
            return ResponseEntity.ok("Status actualizat!");
        }).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(Authentication authentication) {
        // Preluăm email-ul din principalul autentificat (extras din Token)
        String userEmail = authentication.getName();

        List<Order> myOrders = orderRepository.findByUserEmail(userEmail);
        return ResponseEntity.ok(myOrders);
    }


}

