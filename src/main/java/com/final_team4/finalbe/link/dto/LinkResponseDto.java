package com.final_team4.finalbe.link.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LinkResponseDto {


    String link;

    private LinkResponseDto(String link) {
        this.link = link;
    }

    public static LinkResponseDto of(String link) {
        return new LinkResponseDto(link);
    }


}
