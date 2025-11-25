package com.final_team4.finalbe.content.domain;

import lombok.Getter;

@Getter
public enum ContentGenType {
    PENDING("검수전"),
    APPROVED("승인"),
    REJECTED("반려");

    private final String description;

    ContentGenType(String description) {
        this.description = description;
    }
}
