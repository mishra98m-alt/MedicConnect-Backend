package com.medicconnect.services;

import com.medicconnect.models.Person;
import com.medicconnect.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ------------------- LOGIN -------------------
    public boolean login(String identifier, String password) {

        // Try email (returns Optional)
        Optional<Person> personOpt = personRepository.findByEmail(identifier);

        Person person = personOpt.orElseGet(() ->
                personRepository.findByUserId(identifier)
        );

        // User not found
        if (person == null) {
            return false;
        }

        // Validate password
        return passwordEncoder.matches(password, person.getPassword());
    }

    // ------------------- CHANGE PASSWORD -------------------
    public boolean changePassword(String userId, String newPassword) {

        Person person = personRepository.findByUserId(userId);
        if (person == null) {
            return false;
        }

        person.setPassword(passwordEncoder.encode(newPassword));
        personRepository.save(person);
        return true;
    }
}
