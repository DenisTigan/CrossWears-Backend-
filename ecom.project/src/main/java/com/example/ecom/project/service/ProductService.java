package com.example.ecom.project.service;


import com.example.ecom.project.model.Product;
import com.example.ecom.project.repo.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {


    @Autowired
    private ProductsRepository repo;

    public List<Product> getAllProducts(){
        return repo.findAll()
                .stream()
                .filter(product -> product.getProductName() != null)
                .toList();
    }

    public Product getProductById(int id) {
        return repo.findById(id).orElse(null);
    }

    // Metoda nouă pentru sugestii
    public List<Product> getSuggestions(int excludedId) {
        return repo.findRandomSuggestions(excludedId);
    }

    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
       product.setImageName(imageFile.getOriginalFilename());
       product.setImageType(imageFile.getContentType());
       product.setImageDate(imageFile.getBytes());
        return repo.save(product);
    }

    public Product updateProduct(int id, Product updated, MultipartFile imageFile) throws IOException {

        Product existing = repo.findById(id).orElse(null);
        if (existing == null) return null;

        // 1. Actualizăm câmpurile NON-IMAGE
        existing.setProductName(updated.getProductName());
        existing.setProductDescription(updated.getProductDescription());
        existing.setProductPrice(updated.getProductPrice());
        existing.setQuantity(updated.getQuantity());
        existing.setAvailable(updated.isAvailable());

        // 2. Dacă se trimite imagine
        if (imageFile != null && !imageFile.isEmpty()) {
            existing.setImageName(imageFile.getOriginalFilename());
            existing.setImageType(imageFile.getContentType());
            existing.setImageDate(imageFile.getBytes());
        }

        // 3. Salvăm produsul EXISTENT (cu ID-ul lui, deci UPDATE)
        return repo.save(existing);
    }




    public void deleteAllPrducts() {
        repo.deleteAll();
    }

    public void deleteProduct(int id) {
        repo.deleteById(id);
    }

    public List<Product> searchProducts(String keyword, int minPrice, int maxPrice) {
        // Apelăm metoda din repository care caută după nume (ignoring case)
        // și filtrează în intervalul de preț
        return repo.findByProductNameContainingIgnoreCaseAndProductPriceBetween(
                keyword,
                minPrice,
                maxPrice
        );
    }
}
