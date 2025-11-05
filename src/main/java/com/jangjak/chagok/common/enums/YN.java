package com.jangjak.chagok.common.enums;

public enum YN {
    Y, N;

    public static YN from(String s) {
        return (s == null) ? null : YN.valueOf(s.trim().toUpperCase());
    }
}
