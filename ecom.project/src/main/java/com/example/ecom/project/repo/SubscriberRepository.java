package com.example.ecom.project.repo;

import com.example.ecom.project.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    boolean existsByEmail(String email);
    Optional<Subscriber> findByEmail(String email);
    @Transactional
    void deleteByEmail(String email);
}
