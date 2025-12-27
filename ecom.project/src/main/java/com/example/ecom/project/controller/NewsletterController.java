package com.example.ecom.project.controller;


import com.example.ecom.project.model.NewsletterLog;
import com.example.ecom.project.model.Subscriber;
import com.example.ecom.project.repo.NewsletterLogRepository;
import com.example.ecom.project.repo.SubscriberRepository;
import com.example.ecom.project.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/newsletter")
@CrossOrigin(origins = "*")
public class NewsletterController {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NewsletterLogRepository logRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. PUBLIC: Oricine se poate abona de pe site
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeFromFooter(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        // 1. Salvăm în tabelul de Newsletter (Subscribers)
        if (!subscriberRepository.existsByEmail(email)) {
            subscriberRepository.save(new Subscriber(email));
        }

        // 2. SINCRONIZARE: Căutăm dacă există un User cu acest email
        // Dacă există, îi setăm bifa de newsletter pe 'true'
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setNewsletterSubscribed(true);
            userRepository.save(user);
        });

        return ResponseEntity.ok("Te-ai abonat cu succes!");
    }

    // 2. ADMIN: Trimiterea efectivă a mail-urilor
    // Verificăm rolul ROLE_ADMIN conform bazei tale de date
    @PostMapping("/admin/send")
    public ResponseEntity<String> sendNewsletter(@RequestBody Map<String, String> body) {
        String subject = body.get("subject");
        String message = body.get("message");
        List<Subscriber> subscribers = subscriberRepository.findAll();

        for (Subscriber sub : subscribers) {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(sub.getEmail());
            email.setSubject(subject);

            // PASUL A: Construim link-ul (folosind email-ul abonatului curent)
            String linkDezabonare = "http://localhost:8080/api/newsletter/unsubscribe?email=" + sub.getEmail();

            // PASUL B: Cream mesajul final (mesajul tau + link-ul)
            String mesajFinal = message + "\n\n---\nPentru dezabonare, click aici: " + linkDezabonare;

            // PASUL C: Trimitem mesajul FINAL, nu cel original
            email.setText(mesajFinal);

            email.setFrom("adresa_ta@gmail.com"); // Pune aici adresa ta reala
            mailSender.send(email);
        }
        NewsletterLog log = new NewsletterLog();
        log.setSubject(body.get("subject"));
        log.setRecipientCount(subscribers.size()); // Folosim .size() pentru List
        logRepository.save(log);

        return ResponseEntity.ok("Newsletter trimis către " + subscribers.size() + " persoane!");
    }
    @GetMapping("/admin/history")
    public List<NewsletterLog> getHistory() {
        return logRepository.findAll();
    }

    @GetMapping("/unsubscribe")
    public String unsubscribe(@RequestParam String email) {
        // 1. Căutăm abonatul. findByEmail returnează acum un Optional<Subscriber>
        return subscriberRepository.findByEmail(email)
                .map(sub -> {
                    // 2. Dacă a fost găsit, îl ștergem
                    subscriberRepository.delete(sub);
                    return "Te-ai dezabonat cu succes. Ne pare rău să te vedem plecând!";
                })
                // 3. Dacă Optional-ul este gol (email-ul nu există), returnăm mesajul de eroare
                .orElse("Email-ul nu a fost găsit.");
    }

}
