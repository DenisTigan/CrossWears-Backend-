package com.example.ecom.project.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;

    private String imageName;
    private String imageType;
    private String color; // "Alb" sau "Negru" - pentru a știi ce poze afișăm

    @Lob
    @Column(length = 1000000)
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    public ProductImage() {}

    // Getters și Setters
    public int getImageId() { return imageId; }
    public void setImageId(int imageId) { this.imageId = imageId; }
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
