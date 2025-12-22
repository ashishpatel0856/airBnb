package com.ashish.projects.airBnb.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE, FEMALE,OTHER;
    @JsonCreator
    public static Gender fromString(String key) {
        return key == null ? null : Gender.valueOf(key.toUpperCase());
    }
}
