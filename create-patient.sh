#!/usr/bin/env bash
# create-patient.sh
# Usage: ./create-patient.sh
# Edit the variables below if your OpenMRS URL, credentials, or identifiers differ.

set -euo pipefail

# --- CONFIG (edit if needed) ---
OPENMRS_BASE="http://localhost:8081/openmrs/ws/rest/v1"
AUTH_USER="admin"
AUTH_PASS="Admin123"

# The identifier source you used earlier (IDGEN source UUID)
IDENTIFIER_SOURCE_UUID="691eed12-c0f1-11e2-94be-8c13b969e334"

# Identifier type UUID (OpenMRS ID typically)
IDENTIFIER_TYPE_UUID="05a29f94-c0ed-11e2-94be-8c13b969e334"

# Location UUID for the identifier
LOCATION_UUID="8d6c993e-c2cc-11de-8d13-0010c6dffd0f"

# Patient data defaults (change as needed)
GIVEN_NAME="Rahul"
FAMILY_NAME="Sharma"
GENDER="M"
BIRTHDATE="1990-05-10"
ADDRESS1="Sector 21"
CITYVILLAGE="Noida"
STATEPROVINCE="UP"
COUNTRY="India"
POSTALCODE="201301"

# --- generate identifier ---
echo "🔎 Generating identifier from source ${IDENTIFIER_SOURCE_UUID}..."
GEN_PAYLOAD='{}'
GEN_URL="${OPENMRS_BASE}/idgen/identifiersource/${IDENTIFIER_SOURCE_UUID}/identifier"

# Use curl to get identifier; quietly capture body
GEN_RESP=$(curl -s -u "${AUTH_USER}:${AUTH_PASS}" -H "Content-Type: application/json" -d "${GEN_PAYLOAD}" "${GEN_URL}")

# Extract identifier value (handles simple JSON like {"identifier":"10000X"})
IDENTIFIER=$(printf '%s' "$GEN_RESP" | sed -n 's/.*"identifier"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p' || true)

if [ -z "$IDENTIFIER" ]; then
  echo "❌ Failed to get identifier. Response from server:"
  echo "$GEN_RESP"
  exit 2
fi

echo "✅ Got identifier: $IDENTIFIER"

# --- build patient JSON ---
TMP_JSON=$(mktemp /tmp/patient-XXXX.json)
cat > "$TMP_JSON" <<EOF
{
  "person": {
    "gender": "${GENDER}",
    "birthdate": "${BIRTHDATE}",
    "birthdateEstimated": false,
    "names": [
      {
        "givenName": "${GIVEN_NAME}",
        "familyName": "${FAMILY_NAME}",
        "preferred": true
      }
    ],
    "addresses": [
      {
        "address1": "${ADDRESS1}",
        "cityVillage": "${CITYVILLAGE}",
        "stateProvince": "${STATEPROVINCE}",
        "country": "${COUNTRY}",
        "postalCode": "${POSTALCODE}",
        "preferred": true
      }
    ]
  },
  "identifiers": [
    {
      "identifier": "${IDENTIFIER}",
      "identifierType": "${IDENTIFIER_TYPE_UUID}",
      "location": "${LOCATION_UUID}",
      "preferred": true
    }
  ]
}
EOF

echo "📄 Patient JSON written to ${TMP_JSON}:"
cat "$TMP_JSON"

# --- create patient ---
echo
echo "🚀 Creating patient..."
CREATE_URL="${OPENMRS_BASE}/patient"
# Capture response and HTTP code
HTTP_RESPONSE=$(mktemp /tmp/openmrs-response-XXXX.txt)
HTTP_CODE=$(curl -s -u "${AUTH_USER}:${AUTH_PASS}" -H "Content-Type: application/json" -d @"${TMP_JSON}" -o "${HTTP_RESPONSE}" -w "%{http_code}" "${CREATE_URL}")

echo "HTTP ${HTTP_CODE}"
echo "Response body:"
cat "${HTTP_RESPONSE}"
echo

if [[ "$HTTP_CODE" =~ ^2 ]]; then
  # try to show returned uuid from JSON (if present)
  UUID=$(sed -n 's/.*"uuid"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p' "${HTTP_RESPONSE}" || true)
  if [ -n "$UUID" ]; then
    echo "🎉 Patient created successfully. UUID: ${UUID}"
  else
    echo "🎉 Request succeeded (2xx). Server response above."
  fi
  # cleanup
  rm -f "$TMP_JSON" "$HTTP_RESPONSE"
  exit 0
else
  echo "❗ Patient creation failed. See response above for details."
  # keep files for debugging
  echo "Saved request json: ${TMP_JSON}"
  echo "Saved response: ${HTTP_RESPONSE}"
  exit 3
fi
