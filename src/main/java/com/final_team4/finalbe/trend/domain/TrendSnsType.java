package com.final_team4.finalbe.trend.domain;

import java.util.Arrays;

public enum TrendSnsType {
    GOOGLE,
    INSTAGRAM,
    X;

    public static TrendSnsType from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(null);
    }
}
