package com.medicconnect.controllers;

import com.medicconnect.dto.PersonDTO;
import com.medicconnect.models.Person;
import com.medicconnect.models.Status;
import com.medicconnect.services.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final PersonService personService;

    public AdminController(PersonService personService) {
        this.personService = personService;
    }

    // ---------------- Create Main Admin ----------------
    @PostMapping("/create-main-admin")
    public ResponseEntity<Person> createMainAdmin(@RequestBody PersonDTO dto) {
        Person mainAdmin = personService.createMainAdmin(dto);
        return ResponseEntity.ok(mainAdmin);
    }

    // ---------------- Create Single User (Pending Approval) ----------------
    @PostMapping("/org/{orgId}/create-user")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public ResponseEntity<Person> createUser(
            @PathVariable String orgId,
            @RequestBody PersonDTO dto
    ) {
        dto.setOrgId(orgId); // Ensure orgId is set for the user
        Person user = personService.createUser(dto);
        return ResponseEntity.ok(user);
    }

    // ---------------- Approve User ----------------
    @PostMapping("/user/{userId}/approve")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public ResponseEntity<Person> approveUser(@PathVariable String userId) {
        Person approvedUser = personService.approveUser(userId);
        return ResponseEntity.ok(approvedUser);
    }

    // ---------------- List All Users in Organization ----------------
    @GetMapping("/org/{orgId}/users")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public ResponseEntity<List<Person>> getUsers(@PathVariable String orgId) {
        List<Person> users = personService.getAllPersons().stream()
                .filter(u -> u.getOrganization() != null && orgId.equals(u.getOrganization().getOrgId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // ---------------- List Pending Users ----------------
    @GetMapping("/org/{orgId}/users/pending")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public ResponseEntity<List<Person>> getPendingUsers(@PathVariable String orgId) {
        List<Person> pendingUsers = personService.getAllPersons().stream()
                .filter(u -> u.getOrganization() != null
                        && orgId.equals(u.getOrganization().getOrgId())
                        && u.getStatus() == Status.PENDING)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pendingUsers);
    }
}
