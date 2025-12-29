package com.example.ecom.project.dto;

import java.util.List;

public class ProductDTO {
    private String productName;
    private String productDescription;
    private int productPrice;
    private boolean available;
    private List<VariantDTO> variants; // Lista de mărimi și stocuri
    private List<String> imageColors;   // Lista de culori asociate fiecărui fișier trimis

    // Getters și Setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }
    public int getProductPrice() { return productPrice; }
    public void setProductPrice(int productPrice) { this.productPrice = productPrice; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public List<VariantDTO> getVariants() { return variants; }
    public void setVariants(List<VariantDTO> variants) { this.variants = variants; }
    public List<String> getImageColors() { return imageColors; }
    public void setImageColors(List<String> imageColors) { this.imageColors = imageColors; }
}
