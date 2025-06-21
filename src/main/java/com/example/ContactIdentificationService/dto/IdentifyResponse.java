package com.example.ContactIdentificationService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentifyResponse {
    private ContactDTO contact;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContactDTO {
        private Long primaryContactId;
        private List<String> emails;
        private List<String> phoneNumbers;
        private List<Long> secondaryContactIds;
    }
}
