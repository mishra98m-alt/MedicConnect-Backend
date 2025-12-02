package com.medicconnect.controllers;

import com.medicconnect.models.Document;
import com.medicconnect.models.Organization;
import com.medicconnect.models.Person;
import com.medicconnect.repository.DocumentRepository;
import com.medicconnect.services.OrganizationService;
import com.medicconnect.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final PersonService personService;
    private final OrganizationService organizationService;

    @Autowired
    public DocumentController(DocumentRepository documentRepository,
                              PersonService personService,
                              OrganizationService organizationService) {
        this.documentRepository = documentRepository;
        this.personService = personService;
        this.organizationService = organizationService;
    }

    // ---------------- Upload ----------------
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "personId", required = false) String personId,
            @RequestParam(value = "orgId", required = false) String orgId) {

        try {
            Document document = new Document();
            document.setFileName(file.getOriginalFilename());
            document.setFileData(file.getBytes());
            document.setVerified(false);

            if (personId != null) {
                Person person = personService.findById(personId);
                document.setPerson(person);
            }

            if (orgId != null) {
                Organization org = organizationService.getOrganizationById(orgId);
                document.setOrganization(org);
            }

            // Save document to generate documentId
            document = documentRepository.save(document);

            // Append documentId to associated entities
            if (personId != null) {
                Person person = personService.findById(personId);
                person.appendDocument(document.getDocumentId());
                personService.updatePerson(person);
            }

            if (orgId != null) {
                Organization org = organizationService.getOrganizationById(orgId);
                org.appendDocument(document.getDocumentId());
                organizationService.updateOrganization(org);
            }

            // Return documentId in response
            return ResponseEntity.ok(Map.of(
                    "message", "Document uploaded successfully",
                    "documentId", document.getDocumentId()
            ));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "File upload failed",
                            "error", e.getMessage()
                    ));
        }
    }

    // ---------------- List ----------------
    @GetMapping("/person/{personId}")
    public ResponseEntity<List<Document>> getDocumentsByPerson(@PathVariable String personId) {
        Person person = personService.findById(personId);
        List<Document> documents = documentRepository.findByPerson(person);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/organization/{orgId}")
    public ResponseEntity<List<Document>> getDocumentsByOrganization(@PathVariable String orgId) {
        Organization org = organizationService.getOrganizationById(orgId);
        List<Document> documents = documentRepository.findByOrganization(org);
        return ResponseEntity.ok(documents);
    }

    // ---------------- Verified Documents ----------------
    @GetMapping("/person/{personId}/verified")
    public ResponseEntity<List<Document>> getVerifiedDocumentsByPerson(@PathVariable String personId) {
        Person person = personService.findById(personId);
        List<Document> verifiedDocs = documentRepository.findByPersonAndVerifiedTrue(person);
        return ResponseEntity.ok(verifiedDocs);
    }

    @GetMapping("/organization/{orgId}/verified")
    public ResponseEntity<List<Document>> getVerifiedDocumentsByOrganization(@PathVariable String orgId) {
        Organization org = organizationService.getOrganizationById(orgId);
        List<Document> verifiedDocs = documentRepository.findByOrganizationAndVerifiedTrue(org);
        return ResponseEntity.ok(verifiedDocs);
    }

    // ---------------- Download ----------------
    @GetMapping("/download/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable String documentId) {
        Optional<Document> optionalDoc = documentRepository.findAll().stream()
                .filter(doc -> doc.getDocumentId().equals(documentId))
                .findFirst();

        if (optionalDoc.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Document document = optionalDoc.get();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + document.getFileName() + "\"")
                .body(document.getFileData());
    }
}
