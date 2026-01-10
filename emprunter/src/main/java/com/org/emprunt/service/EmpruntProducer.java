package com.org.emprunt.service;

import com.org.emprunt.DTO.EmpruntEvent;
import com.org.emprunt.entities.Emprunter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmpruntProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EmpruntProducer(KafkaTemplate<String,Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmpruntEvent(EmpruntEvent e){
        kafkaTemplate.send("emprunt-created", e);
    }
}

