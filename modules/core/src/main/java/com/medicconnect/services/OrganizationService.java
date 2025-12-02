package com.medicconnect.services;

import com.medicconnect.dto.OrganizationDTO;
import com.medicconnect.models.Organization;
import com.medicconnect.repository.OrganizationRepository;
import com.medicconnect.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final IdGeneratorService idGeneratorService;

    public OrganizationService(OrganizationRepository organizationRepository,
                               IdGeneratorService idGeneratorService) {
        this.organizationRepository = organizationRepository;
        this.idGeneratorService = idGeneratorService;
    }

    /** Name lookup (repository returns Optional correctly) */
    public Optional<Organization> findByOrganizationNameOptional(String name) {
        return organizationRepository.findByOrganizationName(name);
    }

    /** Lookup by orgId (returns entity, NOT Optional) */
    public Organization findByOrgId(String orgId) {
        Organization org = organizationRepository.findByOrgId(orgId);
        if (org == null) {
            throw new RuntimeException("Organization not found with ID: " + orgId);
        }
        return org;
    }

    /** Create organization from map input */
    @Transactional
    public Organization createOrganizationFromMap(Map<String, Object> orgData) {

        Organization org = new Organization();

        org.setOrganizationName((String) orgData.get("organizationName"));
        org.setCategory((String) orgData.get("category"));
        org.setRegistrationNumber((String) orgData.get("registrationNumber"));

        if (orgData.get("yearOfEstablishment") != null) {
            org.setYearOfEstablishment(Integer.parseInt(
                    orgData.get("yearOfEstablishment").toString()
            ));
        }

        org.setOwnershipType((String) orgData.get("ownershipType"));

        Map<String, Object> address = (Map<String, Object>) orgData.get("address");
        if (address != null) {
            org.setFullAddress((String) address.get("fullAddress"));
            org.setCountry((String) address.get("country"));
            org.setState((String) address.get("state"));
            org.setCity((String) address.get("city"));
            org.setPincode((String) address.get("pincode"));
        }

        org.setEmail((String) orgData.get("email"));
        org.setMobile((String) orgData.get("mobile"));
        org.setDocuments((String) orgData.get("documents"));

        // Validate email format
        ValidationUtils.validateEmail(org.getEmail());

        // Generate unique Org ID (ORG1001, ORG1002…)
        org.setOrgId(idGeneratorService.generateOrgId());

        return organizationRepository.save(org);
    }

    /** Create organization normally */
    public Organization createOrganization(Organization org) {
        ValidationUtils.validateEmail(org.getEmail());
        org.setOrgId(idGeneratorService.generateOrgId());
        return organizationRepository.save(org);
    }

    /** FIXED: repository.findById(id) returns Optional<Organization> */
    public Organization getOrganizationById(String id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    /** Update using DTO */
    @Transactional
    public Organization updateOrganization(String id, OrganizationDTO dto) {
        Organization org = getOrganizationById(id);

        if (dto.getOrganizationName() != null) org.setOrganizationName(dto.getOrganizationName());
        if (dto.getCategory() != null) org.setCategory(dto.getCategory());
        if (dto.getRegistrationNumber() != null) org.setRegistrationNumber(dto.getRegistrationNumber());
        if (dto.getOwnershipType() != null) org.setOwnershipType(dto.getOwnershipType());
        if (dto.getYearOfEstablishment() != null) org.setYearOfEstablishment(dto.getYearOfEstablishment());
        if (dto.getDocuments() != null) org.setDocuments(dto.getDocuments());

        return organizationRepository.save(org);
    }

    /** Update full entity */
    @Transactional
    public Organization updateOrganization(Organization org) {
        return organizationRepository.save(org);
    }

    public void deleteOrganization(String id) {
        organizationRepository.deleteById(id);
    }
}
