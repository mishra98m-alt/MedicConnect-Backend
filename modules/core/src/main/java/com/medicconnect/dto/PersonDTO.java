package com.medicconnect.dto;

import com.medicconnect.models.Organization;
import com.medicconnect.models.Person;
import com.medicconnect.permissions.Permission;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PersonDTO {

    private String userId;
    private String name;
    private LocalDate dob;
    private String gender;
    private String bloodGroup;
    private String email;
    private String mobile;
    private String password;
    private Boolean agreement;
    private String fullAddress;
    private String country;
    private String state;
    private String city;
    private String pincode;
    private String documents;
    private LocalDateTime registrationDate;
    private LocalDateTime associatedDate;
    private List<Permission> permissions;
    private List<String> roles; // multiple roles
    private String tempToken;
    private String orgId;

    // 🔹 NEW FIELD: flexible role-specific data (doctor/nurse/admin extras)
    private Map<String, Object> additionalInfo;


    // ---------------- Getters & Setters ----------------
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getAgreement() { return agreement; }
    public void setAgreement(Boolean agreement) { this.agreement = agreement; }

    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getDocuments() { return documents; }
    public void setDocuments(String documents) { this.documents = documents; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public LocalDateTime getAssociatedDate() { return associatedDate; }
    public void setAssociatedDate(LocalDateTime associatedDate) { this.associatedDate = associatedDate; }

    public List<Permission> getPermissions() { return permissions; }
    public void setPermissions(List<Permission> permissions) { this.permissions = permissions; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public String getTempToken() { return tempToken; }
    public void setTempToken(String tempToken) { this.tempToken = tempToken; }

    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }

    public Map<String, Object> getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(Map<String, Object> additionalInfo) { this.additionalInfo = additionalInfo; }


    // ---------------- Convert DTO → Entity ----------------
    public Person toPerson(Organization org) {
        Person person = new Person();
        person.setUserId(this.userId);
        person.setName(this.name);
        person.setDob(this.dob);
        person.setGender(this.gender);
        person.setBloodGroup(this.bloodGroup);
        person.setEmail(this.email);
        person.setMobile(this.mobile);
        person.setPassword(this.password);
        person.setAgreement(this.agreement);
        person.setFullAddress(this.fullAddress);
        person.setCountry(this.country);
        person.setState(this.state);
        person.setCity(this.city);
        person.setPincode(this.pincode);
        person.setDocuments(this.documents);
        person.setRegistrationDate(this.registrationDate);
        person.setAssociatedDate(this.associatedDate);
        person.setPermissions(this.permissions);
        person.setRoles(this.roles);
        person.setOrganization(org);

        // 🔹 Store role-based extra info as JSON or Map (if supported in Person entity)
        person.setAdditionalInfo(this.additionalInfo);

        return person;
    }

    // ---------------- Convert Entity → DTO ----------------
    public static PersonDTO fromPerson(Person person) {
        PersonDTO dto = new PersonDTO();
        dto.setUserId(person.getUserId());
        dto.setName(person.getName());
        dto.setDob(person.getDob());
        dto.setGender(person.getGender());
        dto.setBloodGroup(person.getBloodGroup());
        dto.setEmail(person.getEmail());
        dto.setMobile(person.getMobile());
        dto.setFullAddress(person.getFullAddress());
        dto.setCountry(person.getCountry());
        dto.setState(person.getState());
        dto.setCity(person.getCity());
        dto.setPincode(person.getPincode());
        dto.setDocuments(person.getDocuments());
        dto.setRegistrationDate(person.getRegistrationDate());
        dto.setAssociatedDate(person.getAssociatedDate());
        dto.setPermissions(person.getPermissions());
        dto.setRoles(person.getRoles());
        dto.setOrgId(person.getOrganization() != null ? person.getOrganization().getOrgId() : null);

        // 🔹 Copy extra info if available
        dto.setAdditionalInfo(person.getAdditionalInfo());

        return dto;
    }
}
