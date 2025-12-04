package com.final_team4.finalbe.link.controller;

import com.final_team4.finalbe.link.dto.LinkResponseDto;
import com.final_team4.finalbe.link.service.LinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/link")
@RequiredArgsConstructor
@Validated
public class LinkController {

    private final LinkService linkService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public LinkResponseDto getLink(@RequestParam("jobId") @NotBlank String jobId, HttpServletRequest request) {
        String clientIp = resolveClientIp(request);
        return linkService.resolveLink(jobId,clientIp);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if(StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        return StringUtils.hasText(realIp) ? realIp : request.getRemoteAddr();
    }
}
