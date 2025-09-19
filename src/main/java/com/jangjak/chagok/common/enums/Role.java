package com.jangjak.chagok.common.enums;

public enum Role {
    USER, ADMIN;

    public static Role from(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
