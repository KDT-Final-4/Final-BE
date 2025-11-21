package com.final_team4.finalbe._core.config;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe._core.exception.PermissionDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {


    // 404: 유저가 찾는 결과가 없을 때 발생
    @ExceptionHandler(ContentNotFoundException.class)
    public ProblemDetail handleContentNotFoundException(ContentNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );

        problemDetail.setTitle("Content Not Found");
        problemDetail.setType(URI.create("/errors/content-not-found"));

        return problemDetail;
    }

    // 403: 권한이 없는 사용자가 접근할 때 발생
    @ExceptionHandler(PermissionDeniedException.class)
    public ProblemDetail handlePermissionDeniedException(PermissionDeniedException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                e.getMessage()
        );

        problemDetail.setTitle("Permission Denied");
        problemDetail.setType(URI.create("/errors/permission-denied"));

        return problemDetail;
    }
}
