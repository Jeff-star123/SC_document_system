package com.sjc.schooldocuments.user;

/**
 * Account roles. A single account holds exactly one role, assigned by an administrator.
 */
public enum Role {
    STUDENT("Student"),
    STAFF("Registrar Staff"),
    ADMIN("Administrator");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
