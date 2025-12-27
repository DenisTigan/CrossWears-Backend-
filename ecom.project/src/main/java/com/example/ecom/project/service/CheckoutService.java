package com.example.ecom.project.service;

import com.example.ecom.project.dto.CartRequest;
import com.example.ecom.project.dto.CheckoutRequest;
import com.example.ecom.project.dto.OrderResponse;
import com.example.ecom.project.model.Cart;
import com.example.ecom.project.model.CartItem;
import com.example.ecom.project.model.OrderItem;
import com.example.ecom.project.model.Order;
import com.example.ecom.project.repo.CartRepository;
import com.example.ecom.project.repo.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class CheckoutService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final CartRepository cartRepository;

    public CheckoutService(OrderRepository orderRepository, CartService cartService,
                           PaymentService paymentService, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public OrderResponse placeOrder(CheckoutRequest request) {
        // 1. Preluăm coșul folosind metoda ta existentă
        Cart cart = cartService.getCartByUser(request.getEmail());

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Coșul este gol");
        }

        // 2. Simulăm plata
        String txId = paymentService.processSimulatedPayment(
                request.getPaymentMethod(), request.getCardNumber(), request.getCvv());

        // 3. Creăm comanda (Order)
        Order order = new Order();
        order.setUserEmail(request.getEmail());
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(request.getShippingAddress());
        order.setTotalAmount(cart.getTotalPrice());
        order.setTransactionId(txId);
        order.setStatus(txId != null ? "PAID" : "PENDING");

        // 4. Transformăm CartItems în OrderItems
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProduct().getProductId());
            orderItem.setProductName(cartItem.getProduct().getProductName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getPrice());
            orderItem.setOrder(order);
            order.getItems().add(orderItem);
        }

        // 5. Salvăm comanda
        Order savedOrder = orderRepository.save(order);

        // 6. GOLIM COȘUL (Folosim structura ta cu orphanRemoval)
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);

        return new OrderResponse("Comandă plasată cu succes!",
                savedOrder.getId(), savedOrder.getTotalAmount(), savedOrder.getOrderDate());
    }
}
