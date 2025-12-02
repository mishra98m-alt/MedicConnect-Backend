package com.medicconnect.repository;

import com.medicconnect.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, String> {

    // Find by userId (correct)
    Person findByUserId(String userId);

    // Find by email (Optional is correct)
    Optional<Person> findByEmail(String email);

    // Check email existence
    boolean existsByEmail(String email);

    // FIX: correct method signature – requires two parameters
    Optional<Person> findByEmailOrUserId(String email, String userId);
}
