package com.example.ContactIdentificationService.service;

import com.example.ContactIdentificationService.dto.IdentifyRequest;
import com.example.ContactIdentificationService.dto.IdentifyResponse;
import com.example.ContactIdentificationService.model.Contact;
import com.example.ContactIdentificationService.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepo;

    public IdentifyResponse identify(IdentifyRequest req) {
        String email = req.getEmail();
        String phone = req.getPhoneNumber();

        List<Contact> matchedContacts = contactRepo.findByEmailOrPhoneNumber(email, phone);
        if (matchedContacts.isEmpty()) {
            Contact newContact = new Contact();
            newContact.setEmail(email);
            newContact.setPhoneNumber(phone);
            newContact.setLinkPrecedence(Contact.LinkPrecedence.PRIMARY);
            contactRepo.save(newContact);

            return buildResponse(newContact, List.of(), List.of(), List.of());
        }

        Contact primary = matchedContacts.stream()
                .filter(c -> c.getLinkPrecedence() == Contact.LinkPrecedence.PRIMARY)
                .min(Comparator.comparing(Contact::getCreatedAt))
                .orElse(matchedContacts.get(0));

        if (primary.getLinkedId() != null) {
            primary = contactRepo.findById(primary.getLinkedId()).orElse(primary);
        }

        Set<String> emails = new LinkedHashSet<>();
        Set<String> phones = new LinkedHashSet<>();
        List<Long> secondaryIds = new ArrayList<>();

        for (Contact c : matchedContacts) {
            if (c.getId().equals(primary.getId())) {
                if (c.getEmail() != null) emails.add(c.getEmail());
                if (c.getPhoneNumber() != null) phones.add(c.getPhoneNumber());
                continue;
            }

            if (c.getEmail() != null) emails.add(c.getEmail());
            if (c.getPhoneNumber() != null) phones.add(c.getPhoneNumber());

            if (c.getLinkPrecedence() == Contact.LinkPrecedence.PRIMARY && !c.getId().equals(primary.getId())) {
                c.setLinkPrecedence(Contact.LinkPrecedence.SECONDARY);
                c.setLinkedId(primary.getId());
                contactRepo.save(c);
            }

            secondaryIds.add(c.getId());
        }

        if ((email != null && !emails.contains(email)) || (phone != null && !phones.contains(phone))) {
            Contact newSecondary = new Contact();
            newSecondary.setEmail(email);
            newSecondary.setPhoneNumber(phone);
            newSecondary.setLinkPrecedence(Contact.LinkPrecedence.SECONDARY);
            newSecondary.setLinkedId(primary.getId());
            contactRepo.save(newSecondary);
            secondaryIds.add(newSecondary.getId());
            if (email != null) emails.add(email);
            if (phone != null) phones.add(phone);
        }

        return buildResponse(primary, emails, phones, secondaryIds);
    }

    private IdentifyResponse buildResponse(Contact primary, Collection<String> emails, Collection<String> phones, List<Long> secondaryIds) {
        IdentifyResponse.ContactDTO dto = new IdentifyResponse.ContactDTO();
        dto.setPrimaryContactId(primary.getId());
        dto.setEmails(new ArrayList<>(emails));
        dto.setPhoneNumbers(new ArrayList<>(phones));
        dto.setSecondaryContactIds(secondaryIds);

        IdentifyResponse response = new IdentifyResponse();
        response.setContact(dto);
        return response;
    }
}
