package com.github.rashrayhan.apachekafka.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.rashrayhan.apachekafka.models.LibraryEvent;
import com.github.rashrayhan.apachekafka.services.LibraryEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class LibraryController {

    private final LibraryEventService libraryEventService;

    public LibraryController(LibraryEventService libraryEventService) {
        this.libraryEventService = libraryEventService;
    }

    @PostMapping("/library-event-async")
    public ResponseEntity<HashMap<String, Integer>> addBookEventAsync(@RequestBody List<LibraryEvent> libraryEvent) {
        libraryEventService.sendLibraryEventAsync(libraryEvent);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new HashMap<String, Integer>(){{
                    put("Events sent", libraryEvent.size());
                }});
    }

    @PostMapping("/library-event-sync")
    public SendResult<Integer, String> addBookEventSync(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        return libraryEventService.sendLibraryEventSync(libraryEvent);
    }

    @PostMapping("/library-event-topic")
    public ResponseEntity<String> addBookEventTopic(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
        libraryEventService.sendLibraryEventToTopic(libraryEvent);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Events sent");
    }
}
