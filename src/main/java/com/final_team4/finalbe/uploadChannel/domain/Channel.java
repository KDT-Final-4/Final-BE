package com.final_team4.finalbe.uploadChannel.domain;

import lombok.Getter;

@Getter
public enum Channel {
    X("X"),
    INSTAGRAM("인스타그램"),
    NAVER_BLOG("네이버 블로그");

    private final String description;

    Channel(String description) {
        this.description = description;
    }

}
