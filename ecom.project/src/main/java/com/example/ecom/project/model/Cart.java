package com.example.ecom.project.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Un User are un singur Coș
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Un Coș are mai multe Produse (CartItems)
    // "cascade = CascadeType.ALL" înseamnă că dacă șterg coșul, se șterg și item-urile din el
    // "orphanRemoval = true" înseamnă că dacă scot un item din listă, dispare și din baza de date
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    private Double totalPrice = 0.0;

    // --- CONSTRUCTORI ---
    public Cart() {}

    public Cart(User user) {
        this.user = user;
    }

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    // Metodă ajutătoare pentru recalcularea prețului
    public void recalculateTotal() {
        this.totalPrice = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
