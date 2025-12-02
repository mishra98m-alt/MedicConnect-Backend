package com.medicconnect.config;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;


public final class FieldConfig {

    private FieldConfig() {}

    // ----------------------
    // Field bits
    // ----------------------
    public static final long F_NAME = 1L << 0;
    public static final long F_DOB = 1L << 1;
    public static final long F_GENDER = 1L << 2;
    public static final long F_BLOOD_GROUP = 1L << 3;
    public static final long F_ORG_NAME = 1L << 4;
    public static final long F_CATEGORY = 1L << 5;
    public static final long F_REG_NO = 1L << 6;
    public static final long F_YEAR_EST = 1L << 11;
    public static final long F_OWNERSHIP_TYPE = 1L << 12;

    public static final long F_ADDRESS_FULL_ORG = 1L << 14;
    public static final long F_ADDRESS_COUNTRY_ORG = 1L << 15;
    public static final long F_ADDRESS_STATE_ORG = 1L << 16;
    public static final long F_ADDRESS_CITY_ORG = 1L << 17;
    public static final long F_ADDRESS_PIN_ORG = 1L << 18;

    public static final long F_ADDRESS_FULL_PERSONAL = 1L << 19;
    public static final long F_ADDRESS_COUNTRY_PERSONAL = 1L << 20;
    public static final long F_ADDRESS_STATE_PERSONAL = 1L << 21;
    public static final long F_ADDRESS_CITY_PERSONAL = 1L << 22;
    public static final long F_ADDRESS_PIN_PERSONAL = 1L << 23;

    public static final long F_EMAIL_PERSONAL = 1L << 24;
    public static final long F_MOBILE_PERSONAL = 1L << 25;

    public static final long F_LANDLINE = 1L << 26;
    public static final long F_DOCUMENTS_ORG = 1L << 29;
    public static final long F_DOCUMENTS_PERSONAL = 1L << 30;

    public static final long F_CREATE_PASSWORD = 1L << 31;
    public static final long F_CONFIRM_PASSWORD = 1L << 32;
    public static final long F_AGREEMENT = 1L << 33;

    public static final long F_ORG_EMAIL = 1L << 34;
    public static final long F_ORG_MOBILE = 1L << 35;

    public static final long F_SAME_AS_ORG_EMAIL = 1L << 36;
    public static final long F_SAME_AS_ORG_MOBILE = 1L << 37;

    // Doctor-specific fields (only included in Doctor form)
    public static final long F_DEGREES = 1L << 38;
    public static final long F_CERTIFICATES = 1L << 39;
    public static final long F_SPECIALITIES = 1L << 40;

    // Role type selector field
    public static final long F_ROLE_TYPE = 1L << 41;

    // ----------------------
    // Pre-built form masks
    // ----------------------
    // Keep hospital registration as-is (organization + main admin)
    public static final long FORM_HOSPITAL_REG_PART1 =
            F_ORG_NAME | F_CATEGORY | F_REG_NO | F_YEAR_EST | F_OWNERSHIP_TYPE |
            F_ADDRESS_FULL_ORG | F_ADDRESS_COUNTRY_ORG | F_ADDRESS_STATE_ORG |
            F_ADDRESS_CITY_ORG | F_ADDRESS_PIN_ORG |
            F_ORG_EMAIL | F_ORG_MOBILE | F_LANDLINE | F_DOCUMENTS_ORG;

    public static final long FORM_HOSPITAL_REG_PART2 =
            F_NAME | F_DOB | F_GENDER | F_BLOOD_GROUP |
            F_EMAIL_PERSONAL |
            F_MOBILE_PERSONAL |
            F_CREATE_PASSWORD | F_CONFIRM_PASSWORD |
            F_ADDRESS_FULL_PERSONAL | F_ADDRESS_COUNTRY_PERSONAL | F_ADDRESS_STATE_PERSONAL |
            F_ADDRESS_CITY_PERSONAL | F_ADDRESS_PIN_PERSONAL |
            F_DOCUMENTS_PERSONAL | F_AGREEMENT;

    public static final long FORM_HOSPITAL_REGISTRATION =
            FORM_HOSPITAL_REG_PART1 | FORM_HOSPITAL_REG_PART2;

    // ----------------------
    // Additional user forms (use hospital_reg_part2 as base)
    // - Admin, Pharmacist, Billing & Inventory: same as admin part2 + role selector
    // - Doctor: add degrees/certificates/specialities (multi-entry)
    // ----------------------
    public static final long FORM_ADMIN = FORM_HOSPITAL_REG_PART2 | F_ROLE_TYPE;
    public static final long FORM_BILLING_INVENTORY = FORM_HOSPITAL_REG_PART2 | F_ROLE_TYPE;
    public static final long FORM_PHARMACIST = FORM_HOSPITAL_REG_PART2 | F_ROLE_TYPE;
    public static final long FORM_DOCTOR = FORM_HOSPITAL_REG_PART2 | F_ROLE_TYPE | F_DEGREES | F_CERTIFICATES | F_SPECIALITIES;

    // ----------------------
    // Map of named form masks
    // ----------------------
    public static Map<String, Long> getFormMasks() {
        Map<String, Long> m = new LinkedHashMap<>();
        m.put("hospital_registration_part1", FORM_HOSPITAL_REG_PART1);
        m.put("hospital_registration_part2", FORM_HOSPITAL_REG_PART2);
        m.put("hospital_registration", FORM_HOSPITAL_REGISTRATION);
        m.put("admin_form", FORM_ADMIN);
        m.put("doctor_form", FORM_DOCTOR);
        m.put("billing_inventory_form", FORM_BILLING_INVENTORY);
        m.put("pharmacist_form", FORM_PHARMACIST);
        return m;
    }

    // ----------------------
    // Role -> form mask mapping
    // ----------------------
    public static Map<String, Long> getRoleFormMasks() {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("Admin", FORM_ADMIN);
        map.put("Doctor", FORM_DOCTOR);
        map.put("Billing and Inventory", FORM_BILLING_INVENTORY);
        map.put("Pharmacist", FORM_PHARMACIST);
        return map;
    }

    // ----------------------
    // Full registry of field metadata (all fields)
    public static Map<Long, Map<String, Object>> getFieldMetadata() {
        Map<Long, Map<String, Object>> map = new LinkedHashMap<>();

        // Role selector
        map.put(F_ROLE_TYPE, createFieldWithOptions(
                "roleType",
                "Role Type",
                "multi-select",
                "user.roleType",
                true,
                List.of("Admin", "Doctor", "Billing and Inventory", "Pharmacist")
        ));

        // Doctor-specific multi-entry fields
        map.put(F_DEGREES, Map.of(
                "key", "degrees",
                "label", "Degrees",
                "type", "multi-entry",
                "subFields", List.of(
                        Map.of("key", "degreeName", "label", "Degree Name", "type", "text", "required", true, "placeholder", "e.g., MBBS"),
                        Map.of("key", "yearOfCompletion", "label", "Year of Completion", "type", "number", "required", true, "placeholder", "YYYY"),
                        Map.of("key", "uploadFile", "label", "Upload Degree (optional)", "type", "file", "required", false)
                ),
                "addButton", true,
                "description", "Add multiple degrees; either type the degree or upload the document."
        ));

        map.put(F_CERTIFICATES, Map.of(
                "key", "certificates",
                "label", "Certificates",
                "type", "multi-entry",
                "subFields", List.of(
                        Map.of("key", "certificateName", "label", "Certificate Name", "type", "text", "required", true, "placeholder", "e.g., ACLS"),
                        Map.of("key", "yearOfCompletion", "label", "Year of Completion", "type", "number", "required", true, "placeholder", "YYYY"),
                        Map.of("key", "uploadFile", "label", "Upload Certificate (optional)", "type", "file", "required", false)
                ),
                "addButton", true,
                "description", "Add multiple certificates; either type the certificate or upload the document."
        ));

        map.put(F_SPECIALITIES, Map.of(
                "key", "specialities",
                "label", "Specialities",
                "type", "multi-entry",
                "subFields", List.of(
                        Map.of("key", "specialityName", "label", "Speciality Name", "type", "text", "required", true, "placeholder", "e.g., Cardiology"),
                        Map.of("key", "yearsOfExperience", "label", "Years of Experience", "type", "number", "required", true, "placeholder", "e.g., 5")
                ),
                "addButton", true,
                "description", "Add multiple specialities and specify years of experience for each."
        ));

        // Add all base fields (organization + personal + contact + password/agreement)
        map.putAll(getBaseFields());

        return map;
    }

    /**
     * Returns only fields relevant to given role.
     * The frontend should call this when role selection changes to update visible fields in real-time.
     *
     * @param role role string: "Admin", "Doctor", "Billing and Inventory", "Pharmacist"
     * @return ordered map of field-bit -> field metadata for that role
     */
    public static Map<Long, Map<String, Object>> getFormByRole(String role) {
        Objects.requireNonNull(role, "role must not be null");
        Map<String, Long> roleMasks = getRoleFormMasks();
        Long mask = roleMasks.get(role);
        if (mask == null) {
            // default to admin form if unknown role
            mask = FORM_ADMIN;
        }

        Map<Long, Map<String, Object>> all = getFieldMetadata();
        Map<Long, Map<String, Object>> subset = new LinkedHashMap<>();

        for (Map.Entry<Long, Map<String, Object>> e : all.entrySet()) {
            long bit = e.getKey();
            if ((mask & bit) != 0) {
                subset.put(bit, e.getValue());
            }
        }

        return subset;
    }

    // ----------------------
    // Base reusable field metadata (organization + personal + common)
    // ----------------------
    private static Map<Long, Map<String, Object>> getBaseFields() {
        Map<Long, Map<String, Object>> map = new LinkedHashMap<>();

        // Organization basic
        map.put(F_ORG_NAME, createBasicField("organizationName", "Name of the Organization", "text", "organization.name", true, "e.g., City Hospital", 3, 100, "^[\\w\\s]+$"));
        map.put(F_CATEGORY, createFieldWithOptions("category", "Category", "select", "organization.category", true, List.of("Hospital", "Clinic")));
        map.put(F_REG_NO, createBasicField("registrationNumber", "Registration Number", "text", "organization.registration_number", false, null, 5, 50, "^[A-Za-z0-9/-]+$"));
        map.put(F_YEAR_EST, createBasicField("yearOfEstablishment", "Year of Establishment", "number", "organization.year_of_establishment", false, "YYYY", 1900, LocalDate.now().getYear(), null));
        map.put(F_OWNERSHIP_TYPE, createFieldWithOptions("ownershipType", "Ownership Type", "select", "organization.ownership_type", false, List.of("Private", "Trust", "Government", "Corporation")));

        // Organization address & contact
        map.put(F_ADDRESS_FULL_ORG, createBasicField("fullAddress", "Full Address", "textarea", "organization.address.full_address", false, "Street, landmark, etc.", 5, 200, null));
        map.put(F_ADDRESS_COUNTRY_ORG, createBasicField("country", "Country", "location", "organization.address.country", false, "country", 2, 50, null));
        map.put(F_ADDRESS_STATE_ORG, createBasicField("state", "State", "location", "organization.address.state", false, "state", 2, 50, null));
        map.put(F_ADDRESS_CITY_ORG, createBasicField("city", "City", "location", "organization.address.city", false, "city", 2, 50, null));
        map.put(F_ADDRESS_PIN_ORG, createBasicField("pincode", "Pin Code", "location", "organization.address.pincode", false, null, 4, 10, "^[0-9]+$"));

        map.put(F_ORG_EMAIL, createEmailField("email", "Email ID", "organization.email", true, "example@domain.com"));
        map.put(F_ORG_MOBILE, createPhoneField("mobile", "Mobile", "organization.mobile", true, "Enter mobile"));

        // Landline & org docs
        map.put(F_LANDLINE, createBasicField("landline", "Landline", "text", "organization.landline", false, "Enter complete landline including country & area code", 5, 20, "^[0-9+-]+$"));
        map.put(F_DOCUMENTS_ORG, createBasicField("documents", "Documents", "file", "organization.documents", false, null, null, null, null));

        // Personal fields
        map.put(F_NAME, createBasicField("name", "Name", "text", "personal.name", true, null, 3, 50, "^[a-zA-Z\\s]+$"));
        map.put(F_DOB, createDobField("dob", "Date Of Birth", "date", "personal.dob", true));
        map.put(F_GENDER, createFieldWithOptions("gender", "Gender", "select", "personal.gender", false, List.of("Male", "Female", "Others", "Prefer not to say")));
        map.put(F_BLOOD_GROUP, createFieldWithOptions("bloodGroup", "Blood Group", "select", "personal.bloodGroup", false, List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")));

        // Personal contact & address & docs
        map.put(F_EMAIL_PERSONAL, createEmailFieldWithNote(
                "email",
                "Email ID",
                "personal.email",
                true,
                "Always use your personal email, e.g., example@domain.com",
                "This email will be used for verification and login."
        ));

        map.put(F_MOBILE_PERSONAL, createPhoneFieldWithNote(
                "mobile",
                "Mobile",
                "personal.mobile",
                true,
                "Always use your personal mobile number",
                "Your mobile number will be used for OTP verification."
        ));

        map.put(F_ADDRESS_FULL_PERSONAL, createBasicField("fullAddress", "Full Address", "textarea", "personal.address.full_address", false, "Street, landmark, etc.", 5, 200, null));
        map.put(F_ADDRESS_COUNTRY_PERSONAL, createBasicField("country", "Country", "location", "personal.address.country", false, "country", 2, 50, null));
        map.put(F_ADDRESS_STATE_PERSONAL, createBasicField("state", "State", "location", "personal.address.state", false, "state", 2, 50, null));
        map.put(F_ADDRESS_CITY_PERSONAL, createBasicField("city", "City", "location", "personal.address.city", false, "city", 2, 50, null));
        map.put(F_ADDRESS_PIN_PERSONAL, createBasicField("pincode", "Pin Code", "text", "personal.address.pincode", false, null, 4, 10, "^[0-9]+$"));

        map.put(F_DOCUMENTS_PERSONAL, createBasicField("documents", "Documents", "file", "personal.documents", false, null, null, null, null));

        // Password & agreement
        map.put(F_CREATE_PASSWORD, createPasswordField("password", "Create Password", "password", "auth.password", true));
        map.put(F_CONFIRM_PASSWORD, createPasswordField("confirmPassword", "Confirm Password", "password", "auth.confirmPassword", true));
        map.put(F_AGREEMENT, createBasicField("agreement", "I hereby declare that all the information provided above is true and correct.", "checkbox", "auth.agreement", true, null, null, null, null));

        return map;
    }

    // ----------------------
    // Helper methods (metadata factories)
    // ----------------------
    private static Map<String, Object> createBasicField(String key, String label, String type, String path, boolean required,
                                                        String placeholder, Integer minLength, Integer maxLength, String regex) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("key", key);
        m.put("label", label);
        m.put("type", type);
        m.put("path", path != null ? path : key);
        m.put("required", required);
        if (placeholder != null) m.put("placeholder", placeholder);
        if (minLength != null) m.put("minLength", minLength);
        if (maxLength != null) m.put("maxLength", maxLength);
        if (regex != null) m.put("pattern", regex);
        if ("date".equals(type)) m.put("widget", "calendar");
        return m;
    }

    private static Map<String, Object> createFieldWithOptions(String key, String label, String type, String path, boolean required, List<String> options) {
        Map<String, Object> m = createBasicField(key, label, type, path, required, null, null, null, null);
        m.put("options", options != null ? options : Collections.emptyList());
        return m;
    }

    private static Map<String, Object> createEmailField(String key, String label, String path, boolean required, String placeholder) {
        Map<String, Object> m = createBasicField(key, label, "email", path, required, placeholder, 5, 100, "^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$");
        m.put("verifyButton", true); // frontend should render "Verify" if present
        return m;
    }

    private static Map<String, Object> createEmailFieldWithNote(String key, String label, String path, boolean required, String placeholder, String note) {
        Map<String, Object> m = createEmailField(key, label, path, required, placeholder);
        m.put("note", note);
        return m;
    }

    private static Map<String, Object> createPhoneField(String key, String label, String path, boolean required, String placeholder) {
        Map<String, Object> m = createBasicField(key, label, "phone", path, required, placeholder, 8, 15, "^[0-9]+$");
        m.put("verifyButton", true);
        return m;
    }

    private static Map<String, Object> createPhoneFieldWithNote(String key, String label, String path, boolean required, String placeholder, String note) {
        Map<String, Object> m = createPhoneField(key, label, path, required, placeholder);
        m.put("note", note);
        return m;
    }

    private static Map<String, Object> createDobField(String key, String label, String type, String path, boolean required) {
        Map<String, Object> m = createBasicField(key, label, type, path, required, null, null, null, null);
        m.put("widget", "calendar");
        m.put("validator", (Validator<LocalDate>) dob -> dob != null && Period.between(dob, LocalDate.now()).getYears() >= 18);
        m.put("errorMessage", "User must be 18 years or older.");
        return m;
    }

    private static Map<String, Object> createPasswordField(String key, String label, String type, String path, boolean required) {
        Map<String, Object> m = createBasicField(key, label, type, path, required, "Enter strong password", 8, 50, null);
        m.put("strengthMeter", true);
        return m;
    }

    @FunctionalInterface
    public interface Validator<T> {
        boolean validate(T value);
    }
}
