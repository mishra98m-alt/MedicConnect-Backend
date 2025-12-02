package com.medicconnect.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormBlock {

    private String blockName;                    // e.g., "organization" or "administrator"
    private List<FormField> fields;
    private Map<String, Boolean> sameAsOrganizationFields; // Tracks multiple "Same as Organization" checkboxes
    private boolean agreementChecked;            // Checkbox for agreement

    // -----------------
    // Constructors
    // -----------------
    public FormBlock() {
        this.sameAsOrganizationFields = new HashMap<>();
        this.agreementChecked = false;
    }

    public FormBlock(String blockName, List<FormField> fields) {
        this.blockName = blockName;
        this.fields = fields;
        this.sameAsOrganizationFields = new HashMap<>();
        this.agreementChecked = false;
    }

    // -----------------
    // Getters / Setters
    // -----------------
    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public List<FormField> getFields() {
        return fields;
    }

    public void setFields(List<FormField> fields) {
        this.fields = fields;
    }

    public Map<String, Boolean> getSameAsOrganizationFields() {
        return sameAsOrganizationFields;
    }

    public void setSameAsOrganizationFields(Map<String, Boolean> sameAsOrganizationFields) {
        this.sameAsOrganizationFields = sameAsOrganizationFields;
    }

    public void setSameAsOrganizationField(String fieldPath, boolean value) {
        this.sameAsOrganizationFields.put(fieldPath, value);
    }

    public boolean isSameAsOrganizationField(String fieldPath) {
        return this.sameAsOrganizationFields.getOrDefault(fieldPath, false);
    }

    public boolean isAgreementChecked() {
        return agreementChecked;
    }

    public void setAgreementChecked(boolean agreementChecked) {
        this.agreementChecked = agreementChecked;
    }
}
