package com.example.ecom.project.repo;


import com.example.ecom.project.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductsRepository extends JpaRepository<Product, Integer> {

    // QUERY NATIV MYSQL:
    // Selectează tot din tabel, exclude ID-ul curent, amestecă random, ia doar 4
    @Query(value = "SELECT * FROM product WHERE product_id != :excludedId ORDER BY RAND() LIMIT 4", nativeQuery = true)
    List<Product> findRandomSuggestions(@Param("excludedId") int excludedId);

    List<Product> findByProductNameContainingIgnoreCaseAndProductPriceBetween(
            String name, int minPrice, int maxPrice);
}
