package com.example.ecom.project.controller;


import com.example.ecom.project.model.Product;
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
       if (product != null)
           return new ResponseEntity<>(product, HttpStatus.OK);
       else
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
   }

    @GetMapping("/product/{id}/suggestions")
    public ResponseEntity<List<Product>> getSuggestions(@PathVariable int id) {
        List<Product> suggestions = service.getSuggestions(id);
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping(value = "/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @RequestPart("product") Product product,
            @RequestPart("imageFile") MultipartFile imageFile) {

        try {
            Product saved = service.addProduct(product, imageFile);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   @GetMapping("product/{productId}/image")
   public ResponseEntity<byte[]> getImageByProductId(@PathVariable int productId) {


       Product product = service.getProductById(productId);
       byte[] imageFile = product.getImageDate();

       return   ResponseEntity.ok()
               .contentType(MediaType.valueOf(product.getImageType()))
               .body(imageFile);
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable int id,
                                                @RequestPart("product") Product product,
                                                @RequestPart("imageFile") MultipartFile imageFile) {
       Product product1;
       try {
           product1 = service.updateProduct(id, product, imageFile);
       } catch (IOException e) {
           return new ResponseEntity<>("Failed to update", HttpStatus.BAD_REQUEST);
       }

       if(product1 != null)
           return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
       else
           return new ResponseEntity<>("Failed to update", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        Product product = service.getProductById(id);
        if (product != null) {
            service.deleteProduct(id);
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }



   @DeleteMapping("products")
   public String deleteAllProducts() {
       service.deleteAllPrducts();
       return "All products deleted";
   }

    // Adaugă în ProductController.java
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int minPrice,
            @RequestParam(defaultValue = "1000000") int maxPrice) {

        List<Product> filteredProducts = service.searchProducts(keyword, minPrice, maxPrice);
        return new ResponseEntity<>(filteredProducts, HttpStatus.OK);
    }
}

