package com.example.ecom.project.service;


import com.example.ecom.project.model.Order;
import com.example.ecom.project.repo.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Returnează toate comenzile pentru Admin Panel
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Actualizează statusul (ex: din PENDING în PAID)
    public Order updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comanda nu a fost găsită"));
        order.setStatus(status);
        return orderRepository.save(order);
    }}
