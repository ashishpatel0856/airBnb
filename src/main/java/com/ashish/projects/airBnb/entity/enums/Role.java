package com.ashish.projects.airBnb.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    GUEST, USER,
    HOTEL_MANAGER;



    @JsonCreator
    public static Role fromString(String key) {
        return key == null ? null : Role.valueOf(key.toUpperCase());
    }
}
