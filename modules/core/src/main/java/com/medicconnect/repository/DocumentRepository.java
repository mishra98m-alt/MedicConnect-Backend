package com.medicconnect.repository;

import com.medicconnect.models.Document;
import com.medicconnect.models.Organization;
import com.medicconnect.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByPerson(Person person);
    List<Document> findByOrganization(Organization organization);
    List<Document> findByPersonAndVerifiedTrue(Person person);
    List<Document> findByOrganizationAndVerifiedTrue(Organization organization);
}
