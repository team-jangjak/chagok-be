package com.jangjak.chagok.user.enums;

public enum GENDER {
    M, F;

    public static GENDER from(String s) {
        return (s == null) ? null : GENDER.valueOf(s.trim().toUpperCase());
    }

}