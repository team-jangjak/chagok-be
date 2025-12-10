package com.jangjak.chagok.external.enums;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;

public enum ImageUploadType {
    NONE(0, false),
    PROFILE(1, false),
    HABIT(2, true),
    CHECK_METHOD(3, true),
    ;

    final int value;
    final boolean hasIdInUrl;

    ImageUploadType(int value, boolean hasIdInUrl) {
        this.value = value;
        this.hasIdInUrl = hasIdInUrl;
    }

    public int value() {
        return value;
    }
    public boolean hasIdInUrl() { return hasIdInUrl; }

    public static ImageUploadType fromValue(Integer value) {
        if (value == null) return NONE;
        for (ImageUploadType type : ImageUploadType.values()) {
            if (type.value == value) {
                return type;
            }
        }

        return NONE;
    }

    public static ImageUploadType fromValue(String name) {
        for (ImageUploadType type : ImageUploadType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return NONE;
    }
}
