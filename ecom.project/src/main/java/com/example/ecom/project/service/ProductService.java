package com.example.ecom.project.service;


import com.example.ecom.project.dto.ProductDTO;
import com.example.ecom.project.dto.VariantDTO;
import com.example.ecom.project.model.Product;
import com.example.ecom.project.model.ProductImage;
import com.example.ecom.project.model.ProductVariant;
import com.example.ecom.project.repo.ProductImageRepository;
import com.example.ecom.project.repo.ProductVariantRepository;
import com.example.ecom.project.repo.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

   @Autowired
    private ProductsRepository repo;
    @Autowired
    private ProductVariantRepository variantRepo;
    @Autowired
    private ProductImageRepository imageRepo;

    public List<Product> getAllProducts() {
        return repo.findAll().stream()
                .filter(product -> product.getProductName() != null)
                .toList();
    }

    public Product getProductById(int id) {
        return repo.findById(id).orElse(null);
    }

    public List<Product> getSuggestions(int excludedId) {
        return repo.findRandomSuggestions(excludedId);
    }

    @Transactional
    public Product addProduct(ProductDTO dto, List<MultipartFile> imageFiles) throws IOException {
        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setProductDescription(dto.getProductDescription());
        product.setProductPrice(dto.getProductPrice());
        product.setAvailable(dto.isAvailable());

        Product savedProduct = repo.save(product);

        // Salvare variante (S, M, L, XL)
        if (dto.getVariants() != null) {
            for (VariantDTO vDto : dto.getVariants()) {
                ProductVariant variant = new ProductVariant();
                variant.setSize(vDto.getSize());
                variant.setColor(vDto.getColor());
                variant.setQuantity(vDto.getQuantity());
                variant.setProduct(savedProduct);
                variantRepo.save(variant);
            }
        }

        // Salvare imagini multiple per culoare
        if (imageFiles != null) {
            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);
                ProductImage img = new ProductImage();
                img.setImageName(file.getOriginalFilename());
                img.setImageType(file.getContentType());
                img.setImageData(file.getBytes());
                img.setProduct(savedProduct);

                if (dto.getImageColors() != null && i < dto.getImageColors().size()) {
                    img.setColor(dto.getImageColors().get(i));
                }
                imageRepo.save(img);
            }
        }
        return savedProduct;
    }

    @Transactional
    public Product updateProduct(int id, ProductDTO dto, List<MultipartFile> imageFiles) throws IOException {
        Product existing = repo.findById(id).orElse(null);
        if (existing == null) return null;

        // Actualizare date de bază
        existing.setProductName(dto.getProductName());
        existing.setProductDescription(dto.getProductDescription());
        existing.setProductPrice(dto.getProductPrice());
        existing.setAvailable(dto.isAvailable());

        existing.getVariants().clear();

        for (VariantDTO vDto : dto.getVariants()) {
            ProductVariant v = new ProductVariant();
            v.setSize(vDto.getSize());
            v.setColor(vDto.getColor());
            v.setQuantity(vDto.getQuantity());
            v.setProduct(existing);
            existing.getVariants().add(v);
        }

        // Dacă se trimit imagini noi, le adăugăm la galerie
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);
                ProductImage img = new ProductImage();
                img.setImageName(file.getOriginalFilename());
                img.setImageType(file.getContentType());
                img.setImageData(file.getBytes());
                img.setProduct(existing);
                if (dto.getImageColors() != null && i < dto.getImageColors().size()) {
                    img.setColor(dto.getImageColors().get(i));
                }
                imageRepo.save(img);
            }
        }

        return repo.save(existing);
    }

    public void deleteProduct(int id) {
        repo.deleteById(id); // Datorită CascadeType.ALL, se șterg și variantele/imaginile automat
    }

    public void deleteAllPrducts() {
        repo.deleteAll();
    }

    public List<Product> searchProducts(String keyword, int minPrice, int maxPrice) {
        return repo.findByProductNameContainingIgnoreCaseAndProductPriceBetween(keyword, minPrice, maxPrice);
    }

    public ProductImage getImageById(int imageId) {
        return imageRepo.findById(imageId).orElse(null);
    }

    public void deleteImage(int imageId) {
        imageRepo.deleteById(imageId);
    }
}
