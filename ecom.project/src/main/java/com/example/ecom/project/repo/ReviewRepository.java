package com.example.ecom.project.repo;

import com.example.ecom.project.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_ProductId(int productId);
    void deleteByReviewerName(String reviewerName);
}
