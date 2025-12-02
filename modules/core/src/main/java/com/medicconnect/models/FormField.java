package com.medicconnect.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormField {

    private String key;
    private String label;
    private String type;
    private String path;
    private boolean required;
    private String placeholder;
    private List<String> options;
    private Map<String, Object> extra;

    // Optional metadata
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sameAsOrganizationSource;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sameAsOrganizationTarget;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean verifyButton;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String note;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FormField> subFields; // <-- Added for multi-entry fields

    // ----------------- Constructors -----------------
    public FormField() { this(false); }

    public FormField(boolean required) {
        this.required = required;
        this.options = List.of();
        this.extra = new LinkedHashMap<>();
    }

    public FormField(String key, String label, String type, String path, Boolean required,
                     String placeholder, List<String> options, Map<String, Object> extra) {
        this.key = key;
        this.label = label;
        this.type = type;
        this.path = path;
        this.required = required != null && required;
        this.placeholder = placeholder;
        this.options = options != null ? options : List.of();
        this.extra = extra != null ? extra : new LinkedHashMap<>();
    }

    // ----------------- Getters / Setters -----------------
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }

    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) {
        this.options = options != null ? options : List.of();
    }

    public Map<String, Object> getExtra() { return extra; }
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra != null ? extra : new LinkedHashMap<>();
    }

    public String getSameAsOrganizationSource() { return sameAsOrganizationSource; }
    public void setSameAsOrganizationSource(String sameAsOrganizationSource) { this.sameAsOrganizationSource = sameAsOrganizationSource; }

    public String getSameAsOrganizationTarget() { return sameAsOrganizationTarget; }
    public void setSameAsOrganizationTarget(String sameAsOrganizationTarget) { this.sameAsOrganizationTarget = sameAsOrganizationTarget; }

    public boolean isVerifyButton() { return verifyButton; }
    public void setVerifyButton(boolean verifyButton) { this.verifyButton = verifyButton; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public List<FormField> getSubFields() { return subFields; }      // Getter
    public void setSubFields(List<FormField> subFields) { this.subFields = subFields; } // Setter
}
