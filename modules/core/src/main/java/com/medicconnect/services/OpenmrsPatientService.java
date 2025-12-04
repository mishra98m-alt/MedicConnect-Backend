package com.medicconnect.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.medicconnect.api.OpenmrsApiClient;
import com.medicconnect.config.OpenmrsConfig;
import com.medicconnect.dto.OpenmrsPatientPayload;
import org.springframework.stereotype.Service;

@Service
public class OpenmrsPatientService {

    private final OpenmrsApiClient api;
    private final OpenmrsConfig config;
    private final ObjectMapper mapper = new ObjectMapper();

    public OpenmrsPatientService(OpenmrsApiClient api, OpenmrsConfig config) {
        this.api = api;
        this.config = config;
    }

    public String createPatient(OpenmrsPatientPayload payload) throws Exception {

        // -------------------------------
        // BUILD PERSON OBJECT
        // -------------------------------
        ObjectNode person = mapper.createObjectNode();
        person.put("gender", payload.getGender());
        person.put("birthdate", payload.getBirthdate());
        person.put("birthdateEstimated", payload.isBirthdateEstimated());

        person.set("names", mapper.valueToTree(payload.getNames()));

        // -------------------------------
        // CLEAN ADDRESS NODE (remove nulls)
        // -------------------------------
        if (payload.getAddresses() != null && !payload.getAddresses().isEmpty()) {

            ArrayNode addrList = mapper.valueToTree(payload.getAddresses());

            if (addrList.size() > 0) {
                ObjectNode addr = (ObjectNode) addrList.get(0);

                removeIfNull(addr, "address2");
                removeIfNull(addr, "stateProvince");
                removeIfNull(addr, "country");
                removeIfNull(addr, "postalCode");
            }

            person.set("addresses", addrList);
        }

        // -------------------------------
        // IDENTIFIERS
        // -------------------------------
        ArrayNode identifiers = mapper.valueToTree(payload.getIdentifiers());

        identifiers.forEach(id -> {
            ObjectNode node = (ObjectNode) id;
            if (!node.has("preferred") || node.get("preferred").isNull()) {
                node.put("preferred", true);
            }
        });

        // -------------------------------
        // FINAL REQUEST
        // -------------------------------
        ObjectNode request = mapper.createObjectNode();
        request.set("person", person);
        request.set("identifiers", identifiers);

        // -------------------------------
        // CALL OPENMRS
        // -------------------------------
        JsonNode resp = api.post("/patient", request);

        if (resp.has("uuid")) {
            return resp.get("uuid").asText();
        }

        throw new RuntimeException("OpenMRS did not return UUID → " + resp);
    }

    // -------------------------------------
    // REMOVE NULL FIELDS (OpenMRS requirement)
    // -------------------------------------
    private void removeIfNull(ObjectNode node, String field) {
        if (node.has(field) && node.get(field).isNull()) {
            node.remove(field);
        }
    }
}
