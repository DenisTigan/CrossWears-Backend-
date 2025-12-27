package com.example.ecom.project.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {
    public String processSimulatedPayment(String method, String cardNo, String cvv) {
        if ("CARD".equalsIgnoreCase(method)) {
            if (cardNo == null || cardNo.length() < 12) {
                throw new RuntimeException("Plata eșuată: Date card invalide");
            }
            return "SIM-" + UUID.randomUUID().toString().substring(0, 8);
        }
        return null; // Pentru Ramburs
    }
}
