package com.final_team4.finalbe.schedule.domain;

import lombok.Getter;

@Getter
public enum RepeatInterval {
    DAILY("매일"),
    WEEKLY("매주"),
    MONTHLY("매월"),
    YEARLY("매년");

    private final String description;

    RepeatInterval(String description) {
        this.description = description;
    }

}
