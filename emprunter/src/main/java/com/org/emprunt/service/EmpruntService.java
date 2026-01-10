package com.org.emprunt.service;

import com.org.emprunt.DTO.EmpruntDetailsDTO;
import com.org.emprunt.DTO.EmpruntEvent;
import com.org.emprunt.entities.Emprunter;
import com.org.emprunt.feign.BookClient;
import com.org.emprunt.feign.UserClient;
import com.org.emprunt.repositories.EmpruntRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class EmpruntService {

    private final EmpruntRepository repo;
    private final UserClient userClient;
    private final BookClient bookClient;
    private final EmpruntProducer empruntProducer;

    public EmpruntService(EmpruntRepository repo, UserClient userClient, BookClient bookClient, EmpruntProducer empruntProducer) {
        this.repo = repo;
        this.userClient = userClient;
        this.bookClient = bookClient;
        this.empruntProducer = empruntProducer;
    }

    public Emprunter createEmprunt(Long userId, Long bookId) {

        // 1. Vérifier user existe
        userClient.getUser(userId);

        // 2. Vérifier book existe
        bookClient.getBook(bookId);

        // 3. Créer l’emprunt
        Emprunter b = new Emprunter();
        b.setUserId(userId);
        b.setBookId(bookId);

        Emprunter saved = repo.save(b);

        EmpruntEvent event = new EmpruntEvent(
                saved.getId(),
                saved.getUserId(),
                saved.getBookId()
        );
        empruntProducer.sendEmpruntEvent(event);

        return saved;
    }

    public List<EmpruntDetailsDTO> getAllEmprunts() {
        return repo.findAll().stream().map(e -> {

            var user = userClient.getUser(e.getUserId());
            var book = bookClient.getBook(e.getBookId());

            return new EmpruntDetailsDTO(
                    e.getId(),
                    user.getName(),
                    book.getTitle(),
                    e.getEmpruntDate());
        }).collect(Collectors.toList());
    }

}
