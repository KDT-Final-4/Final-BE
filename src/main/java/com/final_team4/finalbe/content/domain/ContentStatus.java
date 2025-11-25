package com.final_team4.finalbe.content.domain;

import lombok.Getter;

@Getter
public enum ContentStatus {
    PENDING("검수전"),
    APPROVED("승인"),
    REJECTED("반려");

    private final String description;

    ContentStatus(String description) {
        this.description = description;
    }
}
