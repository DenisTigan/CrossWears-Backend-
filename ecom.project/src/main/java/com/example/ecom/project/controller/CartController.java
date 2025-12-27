package com.example.ecom.project.controller;

import com.example.ecom.project.dto.CartRequest;
import com.example.ecom.project.model.Cart;
import com.example.ecom.project.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(Authentication auth) {
        return ResponseEntity.ok(cartService.getCartByUser(auth.getName()));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody CartRequest request, Authentication auth) {
        return ResponseEntity.ok(cartService.addToCart(auth.getName(), request.getProductId(), request.getQuantity()));
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Cart> removeFromCart(@PathVariable Long itemId, Authentication auth) {
        return ResponseEntity.ok(cartService.removeFromCart(auth.getName(), itemId));
    }
}


