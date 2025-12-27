package com.example.ecom.project.controller;

import com.example.ecom.project.dto.CheckoutRequest;
import com.example.ecom.project.dto.OrderResponse;
import com.example.ecom.project.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin // Pentru a permite apeluri din Frontend
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/place")
    public ResponseEntity<OrderResponse> checkout(@RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(checkoutService.placeOrder(request));
    }
}
