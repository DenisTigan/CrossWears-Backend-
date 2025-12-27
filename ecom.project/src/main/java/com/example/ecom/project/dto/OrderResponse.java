package com.example.ecom.project.dto;

import java.time.LocalDateTime;

public class OrderResponse {
    private String message;
    private Long orderId;
    private Double totalAmount;
    private LocalDateTime date;

    public OrderResponse(String message, Long orderId, Double totalAmount, LocalDateTime date) {
        this.message = message;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.date = date;
    }

    public String getMessage() { return message; }
    public Long getOrderId() { return orderId; }
    public Double getTotalAmount() { return totalAmount; }
    public LocalDateTime getDate() { return date; }
}
