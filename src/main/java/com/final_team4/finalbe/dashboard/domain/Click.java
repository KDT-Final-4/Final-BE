package com.final_team4.finalbe.dashboard.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Click {

    private Long id;
    private Long productId;
    private LocalDateTime clickedAt;
    private String ip;


}
