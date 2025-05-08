
package com.vawndev.spring_boot_readnovel.Enum;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IS_AVAILBLE {
    PENDING("PENDING"),
    REJECTED("REJECTED"),
    ACCEPTED("ACCEPTED");

    private final String value;

    IS_AVAILBLE(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
