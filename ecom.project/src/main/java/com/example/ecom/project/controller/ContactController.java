package com.example.ecom.project.controller;

import com.example.ecom.project.dto.ContactRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody ContactRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("contact.crosswears@gmail.com");
            message.setSubject("CONTACT: " + request.getSubject());

            // Verificăm dacă email-ul nu este gol înainte de a seta ReplyTo pentru a evita crash-ul
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                message.setReplyTo(request.getEmail());
            }

            message.setText("Mesaj de la " + request.getName() + " (" + request.getEmail() + "):\n\n" + request.getMessage());

            mailSender.send(message);
            return ResponseEntity.ok("Succes");

        } catch (Exception e) {
            // Păstrăm doar return-ul de eroare către client
            return ResponseEntity.status(500).body("Eroare la trimiterea mesajului.");
        }
    }
}
