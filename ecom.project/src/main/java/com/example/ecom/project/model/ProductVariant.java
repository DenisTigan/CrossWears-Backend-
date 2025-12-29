package com.example.ecom.project.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int variantId;

    private String size;  // S, M, L, XL
    private String color; // Alb, Negru
    private int quantity; // Stocul specific acestei variante

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference // Previne buclele infinite la serializarea JSON
    private Product product;

    public ProductVariant() {}

    // Getters și Setters
    public int getVariantId() { return variantId; }
    public void setVariantId(int variantId) { this.variantId = variantId; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
