package com.github.rashrayhan.apachekafka.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.rashrayhan.apachekafka.models.Book;
import com.github.rashrayhan.apachekafka.models.LibraryEvent;
import com.github.rashrayhan.apachekafka.services.LibraryEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibraryControllerTest {

    @Mock
    private LibraryEventService mockLibraryEventService;

    private LibraryController controllerUnderTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controllerUnderTest = new LibraryController(
                mockLibraryEventService);
    }

    @DisplayName("Async - Should delegate call to library event service")
    @Test
    void addBookEventAsync() {
        List<LibraryEvent> libraryEvents = buildLibraryEvent(3);

        ResponseEntity<HashMap<String, Integer>> expectedResponse = ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new HashMap<String, Integer>(){{
                    put("Events sent", libraryEvents.size());
                }});


        ResponseEntity<HashMap<String, Integer>> actualResponse = controllerUnderTest.addBookEventAsync(libraryEvents);
        verify(mockLibraryEventService, times(1)).sendLibraryEventAsync(libraryEvents);
        assertEquals(actualResponse, expectedResponse);
    }


    @DisplayName("Sync - Should delegate call to library event service")
    @Test
    void addBookEventSync() throws ExecutionException, JsonProcessingException, InterruptedException {
        LibraryEvent libraryEvent = buildLibraryEvent(1).get(0);

        controllerUnderTest.addBookEventSync(libraryEvent);
        verify(mockLibraryEventService, times(1)).sendLibraryEventSync(libraryEvent);

    }

    @DisplayName("Topic - Should delegate call to library event service")
    @Test
    void addBookEventTopic() throws JsonProcessingException {
        LibraryEvent libraryEvent = buildLibraryEvent(1).get(0);

        ResponseEntity<String> expectedResponse = ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Events sent");


        ResponseEntity<String> actualResponse = controllerUnderTest.addBookEventTopic(libraryEvent);
        verify(mockLibraryEventService, times(1)).sendLibraryEventToTopic(libraryEvent);
        assertEquals(actualResponse, expectedResponse);
    }

    private List<LibraryEvent> buildLibraryEvent(int times) {
        List<LibraryEvent> libraryEvents = new ArrayList<>();

        for(int i =1; i<=times; i++){
            libraryEvents.add(LibraryEvent
                    .builder()
                    .id(i)
                    .book(Book.builder()
                            .id(Long.valueOf(i))
                            .bookName("book " + i)
                            .bookAuthor("author " + i)
                            .build())
                    .build());
        }

        return libraryEvents;
    }
}
