package com.example.ecom.project.service;

import com.example.ecom.project.model.Product;
import com.example.ecom.project.model.Review;
import com.example.ecom.project.repo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private ProductService productService;

    public List<Review> getReviewsByProductId(int productId) {
        return reviewRepo.findByProduct_ProductId(productId);
    }


    public Review addReview(int productId, String reviewerName, String content, int rating) {


        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Produsul cu ID-ul " + productId + " nu a fost găsit!");
        }
        Review review = new Review();
        review.setProduct(product);
        review.setReviewerName(reviewerName);
        review.setContent(content);
        review.setRating(rating);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepo.save(review);
    }

    public Review updateReview(Long reviewId, String newContent, int newRating) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Recenzia nu există!"));

        review.setContent(newContent);
        review.setRating(newRating); 

        return reviewRepo.save(review);
    }

    public void deleteReview(Long reviewId) {
        reviewRepo.deleteById(reviewId);
    }
}
