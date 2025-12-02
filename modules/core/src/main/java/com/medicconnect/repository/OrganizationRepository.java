package com.medicconnect.repository;

import com.medicconnect.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, String> {

    // OpenMRS-style lookup: returns entity (not Optional)
    Organization findByOrgId(String orgId);

    // Optional because the result can be null
    Optional<Organization> findByOrganizationName(String organizationName);
}
