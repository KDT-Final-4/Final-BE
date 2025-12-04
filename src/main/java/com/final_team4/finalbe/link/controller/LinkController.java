package com.final_team4.finalbe.link.controller;

import com.final_team4.finalbe.link.dto.LinkResponseDto;
import com.final_team4.finalbe.link.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name= "Link", description = "링크 조회 API")
public class LinkController {

    private final LinkService linkService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "jobId로 링크 조회",
            description = "jobId를 받아 사용자의 IP와 함께 링크 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "jobId 누락 또는 잘못된 요청")
    })
    public LinkResponseDto getLink(@RequestParam("jobId") @NotBlank String jobId, HttpServletRequest request) {
        String clientIp = resolveClientIp(request);
        return linkService.resolveLink(jobId,clientIp);
    }

    //사용자 Ip 수집
    private String resolveClientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");
        if(StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        return StringUtils.hasText(realIp) ? realIp : request.getRemoteAddr();
    }
}
