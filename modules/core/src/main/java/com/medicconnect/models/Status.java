package com.medicconnect.models;

public enum Status {
    PENDING,      // initial state
    APPROVED,     // when admin approves user/person
    REJECTED,     // when admin rejects registration
    ACTIVE,       // active account
    INACTIVE,     // disabled by user
    BLOCKED,      // disabled by admin/security
    DELETED       // soft delete
}
