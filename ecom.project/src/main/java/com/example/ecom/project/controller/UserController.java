package com.example.ecom.project.controller;

import com.example.ecom.project.model.Subscriber;
import com.example.ecom.project.model.User;
import com.example.ecom.project.repo.SubscriberRepository;
import com.example.ecom.project.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SubscriberRepository subscriberRepository;

    // A. Afisare date actuale
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        // Folosim .orElseThrow() pentru a rezolva eroarea de Optional
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit!"));
        return ResponseEntity.ok(user);
    }

    // B. Actualizare profil (Nume & Telefon)
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> data, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(data.get("fullName"));
        user.setPhoneNumber(data.get("phoneNumber"));
        user.setDefaultAddress(data.get("address")); // Noua linie

        userRepository.save(user);
        return ResponseEntity.ok("Profil actualizat!");
    }

    // C. Schimbarea parolei
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> data, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verificăm parola veche folosind encoder-ul
        if (!passwordEncoder.matches(data.get("oldPassword"), user.getPassword())) {
            return ResponseEntity.badRequest().body("Parola actuală este incorectă!");
        }

        // Criptăm parola nouă înainte de salvare
        user.setPassword(passwordEncoder.encode(data.get("newPassword")));
        userRepository.save(user);
        return ResponseEntity.ok("Parolă schimbată cu succes!");
    }

    // D. Toggle Newsletter
    @Transactional // Asigură sincronizarea ambelor tabele
    @PutMapping("/newsletter-toggle")
    public ResponseEntity<?> toggleNewsletter(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean newStatus = !user.isNewsletterSubscribed();
        user.setNewsletterSubscribed(newStatus);
        userRepository.save(user);

        if (newStatus) {
            // Abonare: adăugăm în tabelul de newsletter dacă nu există deja
            if (!subscriberRepository.existsByEmail(user.getEmail())) {
                subscriberRepository.save(new Subscriber(user.getEmail()));
            }
        } else {
            // DEZABONARE: Ștergem din baza de date folosind noua metodă
            subscriberRepository.deleteByEmail(user.getEmail());
        }

        return ResponseEntity.ok(newStatus);
    }

    // A. Încărcare Poză
    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file, Authentication authentication) throws IOException, IOException {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setProfilePicture(file.getBytes()); // Convertim fișierul în byte[]
        userRepository.save(user);
        return ResponseEntity.ok("Imagine salvată!");
    }

    // B. Preluare Poză (pentru tag-ul <img>)
    @GetMapping("/avatar")
    public ResponseEntity<byte[]> getAvatar(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getProfilePicture() == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(user.getProfilePicture());
    }
    @DeleteMapping("/delete-avatar")
    public ResponseEntity<?> deleteAvatar(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setProfilePicture(null); // Ștergem datele binare
        userRepository.save(user);

        return ResponseEntity.ok("Imaginea de profil a fost eliminată!");
    }
}
