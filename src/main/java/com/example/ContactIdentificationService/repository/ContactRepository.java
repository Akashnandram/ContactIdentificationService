package com.example.ContactIdentificationService.repository;

import com.example.ContactIdentificationService.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByEmailOrPhoneNumber(String email, String phoneNumber);
}
