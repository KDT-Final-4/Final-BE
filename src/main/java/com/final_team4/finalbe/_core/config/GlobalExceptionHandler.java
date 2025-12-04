package com.final_team4.finalbe._core.config;

import com.final_team4.finalbe._core.exception.*;
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

    // 400: 중복 이메일이 존재할 때 발생
    @ExceptionHandler(DuplicateEmailException.class)
    public ProblemDetail handleDuplicateEmailException(DuplicateEmailException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );

        problemDetail.setTitle("Duplicate Email");
        problemDetail.setType(URI.create("/errors/duplicate-email"));

        return problemDetail;
    }

    // 401: 인증이 실패했을 때 발생
    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorizedException(UnauthorizedException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                e.getMessage()
        );

        problemDetail.setTitle("Unauthorized");
        problemDetail.setType(URI.create("/errors/unauthorized"));

        return problemDetail;
    }

    // 400 :클라이언트의 잘못된 요청 ( 비밀번호 검증 실패 등 )
    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequestException(BadRequestException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,       // 422 쓰고 싶으면 HttpStatus.UNPROCESSABLE_ENTITY
                e.getMessage()
        );

        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("/errors/bad-request"));

        return problemDetail;
    }

    // 400: 잘못된 요청 파라미터가 전달될 때 발생
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );

        problemDetail.setTitle("Invalid Argument");
        problemDetail.setType(URI.create("/errors/invalid-argument"));

        return problemDetail;
    }
}
