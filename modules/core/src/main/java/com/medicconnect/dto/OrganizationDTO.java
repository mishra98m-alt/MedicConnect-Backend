package com.medicconnect.dto;

import com.medicconnect.models.Organization;
import java.util.Date;
import java.util.List;
import com.medicconnect.models.Person;

public class OrganizationDTO {

    private String orgId;
    private String organizationName;
    private String category;
    private String registrationNumber;
    private Integer yearOfEstablishment;
    private String ownershipType;
    private String email;
    private String mobile;
    private String landline;
    private String fullAddress;
    private String country;
    private String state;
    private String city;
    private String pincode;
    private String documents;
    private Date createdAt;
    private List<Person> persons;

    // ---------------- Getters & Setters ----------------
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public Integer getYearOfEstablishment() { return yearOfEstablishment; }
    public void setYearOfEstablishment(Integer yearOfEstablishment) { this.yearOfEstablishment = yearOfEstablishment; }

    public String getOwnershipType() { return ownershipType; }
    public void setOwnershipType(String ownershipType) { this.ownershipType = ownershipType; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getLandline() { return landline; }
    public void setLandline(String landline) { this.landline = landline; }

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

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public List<Person> getPersons() { return persons; }
    public void setPersons(List<Person> persons) { this.persons = persons; }

    // ---------------- Convert DTO to Entity ----------------
    public Organization toOrganization() {
        Organization org = new Organization();
        org.setOrgId(this.orgId);
        org.setOrganizationName(this.organizationName);
        org.setCategory(this.category);
        org.setRegistrationNumber(this.registrationNumber);
        org.setYearOfEstablishment(this.yearOfEstablishment);
        org.setOwnershipType(this.ownershipType);
        org.setEmail(this.email);
        org.setMobile(this.mobile);
        org.setLandline(this.landline);
        org.setFullAddress(this.fullAddress);
        org.setCountry(this.country);
        org.setState(this.state);
        org.setCity(this.city);
        org.setPincode(this.pincode);
        org.setDocuments(this.documents);
        org.setCreatedAt(this.createdAt);
        org.setPersons(this.persons);
        return org;
    }
}
