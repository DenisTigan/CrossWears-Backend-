package com.example.ecom.project.repo;

import com.example.ecom.project.model.Message;
import com.example.ecom.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 1. (NOU) Aceasta este metoda care repară BUG-ul istoricului.
    // Returnează toate mesajele unde 'user' este expeditor SAU destinatar.
    // Astfel, clientul vede tot ce a vorbit, indiferent cu care Admin.
    @Query("SELECT m FROM Message m WHERE m.sender = :user OR m.receiver = :user ORDER BY m.timestamp ASC")
    List<Message> findAllMessagesForUser(@Param("user") User user);

    // 2. (VECHI - Păstrăm pentru Admin Panel când dă click pe un client specific)
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender = :user1 AND m.receiver = :user2) OR " +
            "(m.sender = :user2 AND m.receiver = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findConversation(@Param("user1") User user1, @Param("user2") User user2);

    // 3. (MODIFICAT) Pentru Inbox Admin:
    // Am scos "AND u.role <> 'ROLE_ADMIN'" ca să poți apărea și tu în listă dacă îți scrii singur.
    // Am adăugat "AND u.id <> :currentAdminId" ca să nu te vezi pe tine în listă DOAR DACĂ vrei neapărat (opțional).
    // Dar ca să te vezi pe tine în listă (Goal-ul tău), folosim varianta simplă:

    @Query("SELECT DISTINCT u FROM User u WHERE " +
            "u.id IN (SELECT m.sender.id FROM Message m) OR " +
            "u.id IN (SELECT m.receiver.id FROM Message m)")
    List<User> findClientsWithMessages();

    // Șterge mesajele trimise de acest user ID
    void deleteBySenderId(Long id);

    // Șterge mesajele primite de acest user ID (CRITIC!)
    void deleteByReceiverId(Long id);
}
