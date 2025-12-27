package com.example.ecom.project.controller;

import com.example.ecom.project.dto.ChatRequest;
import com.example.ecom.project.model.Message;
import com.example.ecom.project.model.User;
import com.example.ecom.project.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChatController {
    @Autowired
    private ChatService chatService;

    // A. TRIMITE MESAJ
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequest request) {
        String email = getCurrentUserEmail();
        try {
            Message saved;
            // Dacă nu e specificat ID-ul destinatarului, considerăm că e mesaj către Admin
            if (request.getReceiverId() == null || request.getReceiverId() == 0) {
                saved = chatService.sendMessageToAdmin(email, request.getContent());
            } else {
                // Altfel, e mesaj către un user specific (de la Admin)
                saved = chatService.sendMessageToUser(email, request.getReceiverId(), request.getContent());
            }
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // B. VEZI ISTORIC (Chat-ul propriu-zis)
    // Clientul apelează: /history (fără parametri)
    // Adminul apelează: /history?userId=5 (când dă click pe Ion Popescu)
    @GetMapping("/history")
    public ResponseEntity<List<Message>> getHistory(@RequestParam(required = false) Long userId) {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(chatService.getConversation(email, userId));
    }

    // C. PENTRU ADMIN: LISTA DE CLIENȚI (Inbox-ul)
    @GetMapping("/list-clients")
    public ResponseEntity<List<User>> getClientsList() {
        // Aici ai putea verifica suplimentar dacă userul curent e Admin
        return ResponseEntity.ok(chatService.getClientsWithHistory());
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}

