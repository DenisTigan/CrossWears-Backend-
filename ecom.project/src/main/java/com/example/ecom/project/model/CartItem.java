package com.example.ecom.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    private Cart cart;

    private String color; // Nou: reține culoarea aleasă
    private String size;  // Nou: reține mărimea aleasă
    private int quantity;
    private Double price;

    public CartItem() {}

    public CartItem(Cart cart, Product product, String color, String size, int quantity) {
        this.cart = cart;
        this.product = product;
        this.color = color;
        this.size = size;
        this.quantity = quantity;
        this.price = (double) product.getProductPrice();
    }

    // Getters și Setters (include noile câmpuri color și size)
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
}
