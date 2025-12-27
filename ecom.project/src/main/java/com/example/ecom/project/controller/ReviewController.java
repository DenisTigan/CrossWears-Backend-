package com.example.ecom.project.controller;

import com.example.ecom.project.dto.ReviewRequest;
import com.example.ecom.project.model.Review;
import com.example.ecom.project.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/reviews/")
@CrossOrigin(origins = "*", maxAge = 3600)
//@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
public class ReviewController {
    @Autowired
    private ReviewService reviewService; 

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody ReviewRequest request) {
        try {
            Review newReview = reviewService.addReview(
                    request.getProductId(),
                    request.getName(),
                    request.getContent(),
                    request.getRating()
            );
            return ResponseEntity.ok(newReview);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable int productId) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/update/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody ReviewRequest request) {

        System.out.println("=== CERERE UPDATE PRIMITĂ ===");
        System.out.println("ID Recenzie: " + reviewId);
        System.out.println("Text Primit: " + request.getContent());
        System.out.println("Rating Primit: " + request.getRating());

        try {
            Review updated = reviewService.updateReview(reviewId, request.getContent(), request.getRating());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("Recenzie ștearsă!");
    }
}
