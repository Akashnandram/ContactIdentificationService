package com.example.ContactIdentificationService.controller;

import com.example.ContactIdentificationService.dto.IdentifyRequest;
import com.example.ContactIdentificationService.dto.IdentifyResponse;
import com.example.ContactIdentificationService.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identify")
public class IdentifyController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public ResponseEntity<IdentifyResponse> identify(@RequestBody IdentifyRequest request) {
        return ResponseEntity.ok(contactService.identify(request));
    }
}
