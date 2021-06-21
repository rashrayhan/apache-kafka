package com.github.rashrayhan.apachekafka.controllers;


import com.github.rashrayhan.apachekafka.model.LibraryEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibraryController {


    @PostMapping("/library-event")
    public ResponseEntity<LibraryEvent> addBook(@RequestBody LibraryEvent libraryEvent){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(libraryEvent);
    }
}
