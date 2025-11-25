package com.final_team4.finalbe.content.domain;

import lombok.Getter;

@Getter
public enum ContentStatus {
    AUTO("자동 생성"),
    MANUAL("수동 생성");

    private final String description;

    ContentStatus(String description) {
        this.description = description;
    }
}
