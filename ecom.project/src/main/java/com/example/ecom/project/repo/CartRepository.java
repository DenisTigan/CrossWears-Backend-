package com.example.ecom.project.repo;

import com.example.ecom.project.model.Cart;
import com.example.ecom.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);
}
