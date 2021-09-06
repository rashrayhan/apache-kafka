package com.github.rashrayhan.apachekafka.integration;

import com.github.rashrayhan.apachekafka.models.Book;
import com.github.rashrayhan.apachekafka.models.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@EmbeddedKafka(topics = {"test-library-event"}, partitions = 3)
public class LibraryIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void addBookEventAsync(){
        List<LibraryEvent> libraryEvents = new ArrayList<>();
        libraryEvents.add(LibraryEvent
                .builder()
                .id(null)
                .book(Book.builder()
                        .id(Long.valueOf(1))
                        .bookName("book 1")
                        .bookAuthor("author 1")
                        .build())
                .build());

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<List<LibraryEvent>> request = new HttpEntity<>(libraryEvents, headers);

        ResponseEntity<HashMap> responseEntity = restTemplate.exchange("/library-event-async",
                HttpMethod.POST,
                request,
                HashMap.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().get("Events sent"));
    }
}
