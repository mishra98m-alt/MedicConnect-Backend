package com.medicconnect.services;

import com.medicconnect.models.Document;
import com.medicconnect.models.Organization;
import com.medicconnect.models.Person;
import com.medicconnect.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    // ---------------- Stage Document (no link yet) ----------------
    public Document stageDocument(MultipartFile file) throws Exception {
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setFileData(file.getBytes());
        document.setVerified(false);
        return documentRepository.save(document);
    }

    // ---------------- Link staged document to a Person ----------------
    public Document linkDocumentToPerson(String documentId, Person person) {
        Long id = Long.parseLong(documentId); // FIXED
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        document.setPerson(person);
        return documentRepository.save(document);
    }

    // ---------------- Link staged document to an Organization ----------------
    public Document linkDocumentToOrganization(String documentId, Organization organization) {
        Long id = Long.parseLong(documentId); // FIXED
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        document.setOrganization(organization);
        return documentRepository.save(document);
    }

    // ---------------- Upload and link directly to Person ----------------
    public Document uploadDocumentForPerson(Person person, MultipartFile file) throws Exception {
        Document document = new Document();
        document.setPerson(person);
        document.setFileName(file.getOriginalFilename());
        document.setFileData(file.getBytes());
        document.setVerified(false);
        return documentRepository.save(document);
    }

    // ---------------- Upload and link directly to Organization ----------------
    public Document uploadDocumentForOrganization(Organization org, MultipartFile file) throws Exception {
        Document document = new Document();
        document.setOrganization(org);
        document.setFileName(file.getOriginalFilename());
        document.setFileData(file.getBytes());
        document.setVerified(false);
        return documentRepository.save(document);
    }
}
