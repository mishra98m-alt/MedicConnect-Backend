package com.medicconnect.services;

import com.medicconnect.config.FieldConfig;
import com.medicconnect.models.FormBlock;
import com.medicconnect.models.FormField;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FieldService {

    private final Map<Long, Map<String, Object>> fieldMeta = FieldConfig.getFieldMetadata();

    // ----------------------------
    // 1️⃣ HOSPITAL REGISTRATION FORM
    // ----------------------------
    public List<FormBlock> getHospitalRegistrationForm() {
        List<FormBlock> formBlocks = new ArrayList<>();
        Map<String, Long> formMasks = Map.of(
                "organization", FieldConfig.FORM_HOSPITAL_REG_PART1,
                "administrator", FieldConfig.FORM_HOSPITAL_REG_PART2
        );

        for (Map.Entry<String, Long> entry : formMasks.entrySet()) {
            String blockName = entry.getKey();
            long mask = entry.getValue();

            List<FormField> fields = getFieldsByMask(mask);

            Map<String, Boolean> sameMap = fields.stream()
                    .filter(f -> f.getSameAsOrganizationTarget() != null)
                    .collect(Collectors.toMap(
                            FormField::getSameAsOrganizationTarget,
                            f -> false,
                            (a, b) -> b,
                            LinkedHashMap::new
                    ));

            FormBlock block = new FormBlock();
            block.setBlockName(blockName);
            block.setFields(fields);
            block.setSameAsOrganizationFields(sameMap);
            block.setAgreementChecked(false);

            formBlocks.add(block);
        }
        return formBlocks;
    }

    // ----------------------------
    // 2️⃣ BASE USER FORM
    // ----------------------------
    public List<FormBlock> getBaseFormAsBlocks() {
        FormBlock baseBlock = new FormBlock();
        baseBlock.setBlockName("personal");

        List<FormField> fields = new ArrayList<>();
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_ROLE_TYPE))); // roleType
        fields.add(createEmailField());
        fields.add(createMobileField());

        // Common personal fields
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_NAME)));
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_DOB)));
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_GENDER)));
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_BLOOD_GROUP)));
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_ADDRESS_FULL_PERSONAL)));
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_ADDRESS_COUNTRY_PERSONAL)));
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_ADDRESS_STATE_PERSONAL)));
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_ADDRESS_CITY_PERSONAL)));
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_ADDRESS_PIN_PERSONAL)));
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_DOCUMENTS_PERSONAL)));
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_CREATE_PASSWORD)));
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_CONFIRM_PASSWORD)));
        fields.add(mapToFormField(fieldMeta.get(FieldConfig.F_AGREEMENT)));

        baseBlock.setFields(fields);
        baseBlock.setSameAsOrganizationFields(Map.of("personal.email", false, "personal.mobile", false));
        baseBlock.setAgreementChecked(false);

        return List.of(baseBlock);
    }

    // ----------------------------
    // 3️⃣ ROLE-SPECIFIC FIELDS
    // ----------------------------
    public List<FormField> getFieldsForRole(String role) {
        if (role == null) return Collections.emptyList();

        if (role.equalsIgnoreCase("doctor")) {
            return getDoctorSpecificFields();
        }
        // Other roles: no extra fields
        return Collections.emptyList();
    }

    // ----------------------------
    // 4️⃣ DOCTOR-SPECIFIC FIELDS
    // ----------------------------
    private List<FormField> getDoctorSpecificFields() {
        return List.of(
                mapToFormField(fieldMeta.get(FieldConfig.F_DEGREES)),
                mapToFormField(fieldMeta.get(FieldConfig.F_CERTIFICATES)),
                mapToFormField(fieldMeta.get(FieldConfig.F_SPECIALITIES))
        );
    }

    // ----------------------------
    // 5️⃣ PLACEHOLDER METHODS FOR OTHER ROLES
    // ----------------------------
    private List<FormField> getAdminSpecificFields() { return Collections.emptyList(); }
    private List<FormField> getBillingSpecificFields() { return Collections.emptyList(); }
    private List<FormField> getPharmacistSpecificFields() { return Collections.emptyList(); }

    // ----------------------------
    // 6️⃣ HELPER METHODS
    // ----------------------------
    private List<FormField> getFieldsByMask(long mask) {
        return fieldMeta.entrySet().stream()
                .filter(e -> e.getKey() != null && (e.getKey() & mask) != 0)
                .map(e -> mapToFormField(e.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private FormField mapToFormField(Map<String, Object> meta) {
        if (meta == null) return null;

        String key = (String) meta.getOrDefault("key", "");
        String label = (String) meta.getOrDefault("label", "");
        String type = (String) meta.getOrDefault("type", "text");
        String path = (String) meta.getOrDefault("path", key);
        boolean required = Boolean.TRUE.equals(meta.get("required"));
        String placeholder = (String) meta.getOrDefault("placeholder", null);
        List<String> options = (List<String>) meta.getOrDefault("options", Collections.emptyList());

        Map<String, Object> extra = new LinkedHashMap<>(meta);
        extra.keySet().removeAll(Set.of("key", "label", "type", "path", "required", "placeholder", "options"));

        FormField field = new FormField(key, label, type, path, required, placeholder, options, extra);

        // Nested fields
        if (meta.containsKey("subFields")) {
            List<Map<String, Object>> subMetaList = (List<Map<String, Object>>) meta.get("subFields");
            List<FormField> subFields = subMetaList.stream()
                    .map(this::mapToFormField)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            field.setSubFields(subFields);
        }

        return field;
    }

    private FormField createEmailField() {
        FormField email = mapToFormField(fieldMeta.get(FieldConfig.F_EMAIL_PERSONAL));
        email.setVerifyButton(true);
        email.setNote("This email will be used for verification and login.");
        email.setSameAsOrganizationSource("organization.email");
        email.setSameAsOrganizationTarget("personal.email");
        email.getExtra().put("verifyButton", true);
        email.getExtra().put("note", email.getNote());
        return email;
    }

    private FormField createMobileField() {
        FormField mobile = mapToFormField(fieldMeta.get(FieldConfig.F_MOBILE_PERSONAL));
        mobile.setVerifyButton(true);
        mobile.setSameAsOrganizationSource("organization.mobile");
        mobile.setSameAsOrganizationTarget("personal.mobile");
        mobile.getExtra().put("verifyButton", true);
        return mobile;
    }

    // ----------------------------
    // 7️⃣ MAIN ADMIN & USER REGISTRATION FORMS
    // ----------------------------
    public List<FormBlock> getMainAdminRegistrationForm() {
        List<FormField> fields = getFieldsByMask(FieldConfig.FORM_HOSPITAL_REG_PART2);

        FormBlock adminBlock = new FormBlock();
        adminBlock.setBlockName("administrator");
        adminBlock.setFields(fields);
        adminBlock.setSameAsOrganizationFields(Collections.emptyMap());
        adminBlock.setAgreementChecked(false);

        return List.of(adminBlock);
    }

    public List<FormBlock> getUserRegistrationForm() {
        return getBaseFormAsBlocks();
    }
}
