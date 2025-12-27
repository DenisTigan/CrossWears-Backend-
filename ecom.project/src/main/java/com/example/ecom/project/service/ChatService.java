package com.example.ecom.project.service;


import com.example.ecom.project.model.Message;
import com.example.ecom.project.model.User;
import com.example.ecom.project.repo.MessageRepository;
import com.example.ecom.project.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. CLIENT -> ADMIN (Trimite mesaj oricărui admin disponibil)
    public Message sendMessageToAdmin(String senderEmail, String content) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Trimitem către primul Admin găsit (sau către tine însuți dacă ești singurul admin)
        User adminReceiver = userRepository.findAll().stream()
                .filter(u -> "ROLE_ADMIN".equals(u.getRole()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No Admin found"));

        return messageRepository.save(new Message(sender, adminReceiver, content));
    }

    // 2. ADMIN -> CLIENT (Rămâne la fel)
    public Message sendMessageToUser(String adminEmail, Long userId, String content) {
        User adminSender = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        User clientReceiver = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        return messageRepository.save(new Message(adminSender, clientReceiver, content));
    }

    // 3. IA ISTORICUL (AICI AM REPARAT)
    public List<Message> getConversation(String currentUserEmail, Long otherUserId) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // CAZUL 1: CLIENTUL (Vrea să vadă tot ce a vorbit cu suportul)
        if (otherUserId == null || otherUserId == 0) {
            // Folosim metoda nouă care returnează tot
            return messageRepository.findAllMessagesForUser(currentUser);
        }

        // CAZUL 2: ADMINUL (Vrea să vadă ce a vorbit cu Userul X)
        else {
            User otherUser = userRepository.findById(otherUserId)
                    .orElseThrow(() -> new RuntimeException("Other user not found"));
            return messageRepository.findConversation(currentUser, otherUser);
        }
    }

    // 4. LISTA PENTRU INBOX
    public List<User> getClientsWithHistory() {
        return messageRepository.findClientsWithMessages();
    }


}
