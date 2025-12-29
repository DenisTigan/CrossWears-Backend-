package com.example.ecom.project.controller;


import com.example.ecom.project.dto.ProductDTO;
import com.example.ecom.project.model.Product;
import com.example.ecom.project.model.ProductImage;
import com.example.ecom.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductService service;

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        Product product = service.getProductById(id);
        return (product != null) ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @GetMapping("/product/{id}/suggestions")
    public ResponseEntity<List<Product>> getSuggestions(@PathVariable int id) {
        return ResponseEntity.ok(service.getSuggestions(id));
    }

    @PostMapping(value = "/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @RequestPart("product") ProductDTO productDto,
            @RequestPart("imageFiles") List<MultipartFile> imageFiles) {
        try {
            Product saved = service.addProduct(productDto, imageFiles);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Endpoint actualizat pentru a prelua o imagine specifică din galeria produsului
    @GetMapping("/product/image/{imageId}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable int imageId) {
        ProductImage image = service.getImageById(imageId);

        if (image != null && image.getImageData() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(image.getImageType()))
                    .body(image.getImageData());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int minPrice,
            @RequestParam(defaultValue = "1000000") int maxPrice) {
        return ResponseEntity.ok(service.searchProducts(keyword, minPrice, maxPrice));
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        if (service.getProductById(id) != null) {
            service.deleteProduct(id);
            return ResponseEntity.ok("Produsul și toate variantele au fost șterse.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produsul nu există.");
    }

    @PutMapping(value = "/product/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable int id,
            @RequestPart("product") ProductDTO productDto,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        try {
            // Presupunem că ai deja metoda updateProduct definită în ProductService
            Product updated = service.updateProduct(id, productDto, imageFiles);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Eroare la update: " + e.getMessage());
        }
    }

    @DeleteMapping("/product/image/{imageId}")
    public ResponseEntity<?> deleteProductImage(@PathVariable int imageId) {
        try {
            service.deleteImage(imageId);
            return ResponseEntity.ok("Imagine ștearsă cu succes.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Eroare la ștergere: " + e.getMessage());
        }
    }
}

