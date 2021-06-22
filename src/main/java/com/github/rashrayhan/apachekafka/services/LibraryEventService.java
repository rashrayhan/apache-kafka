package com.github.rashrayhan.apachekafka.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.rashrayhan.apachekafka.config.AutoCreateConfig;
import com.github.rashrayhan.apachekafka.models.LibraryEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class LibraryEventService {

    KafkaTemplate<Integer, String> kafkaTemplate;
    ObjectMapper objectMapper;
    AutoCreateConfig autoCreateConfig;

    public LibraryEventService(KafkaTemplate<Integer, String> kafkaTemplate,
                               ObjectMapper objectMapper,
                               AutoCreateConfig autoCreateConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.autoCreateConfig = autoCreateConfig;
    }


    public void sendLibraryEventAsync(List<LibraryEvent> libraryEvent) {
        libraryEvent.forEach(event -> {

            Integer key = event.getId();
            String value = null;
            try {
                value = objectMapper.writeValueAsString(event);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            log.info("Sending message...");
            ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.sendDefault(key, value);

            handleCallback(key, listenableFuture);
        });
    }

    public SendResult<Integer, String> sendLibraryEventSync(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        Integer key = libraryEvent.getId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        log.info("Sending message...");
        SendResult<Integer, String> sendEvent = kafkaTemplate.sendDefault(key, value).get();

        return sendEvent;
    }

    public void sendLibraryEventToTopic(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        log.info("Sending message...");
        ListenableFuture<SendResult<Integer, String>> sendEvent = kafkaTemplate.send(autoCreateConfig.libraryEvents().name(), key, value);

        handleCallback(key, sendEvent);
    }


    private void handleCallback(Integer key, ListenableFuture<SendResult<Integer, String>> listenableFuture) {
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @SneakyThrows
            @Override
            public void onFailure(Throwable ex) {
                log.error("Message sending failed {}", ex.getMessage());
                throw ex;
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                log.info("Message with key: {} sent into partition {}", key,
                        result.getRecordMetadata().partition());
            }
        });
    }

}
