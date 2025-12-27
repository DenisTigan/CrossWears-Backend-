package com.example.ecom.project.repo;

import com.example.ecom.project.model.Favorite;
import com.example.ecom.project.model.Product;
import com.example.ecom.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser(User user);
    Optional<Favorite> findByUserAndProduct(User user, Product product);
    boolean existsByUserAndProduct(User user, Product product);
    void deleteByUserId(Long userId);


}
