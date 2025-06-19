package com.ds.auth.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.ds.auth.response.ApiResponse;

@RestControllerAdvice
public class FileConfig {

    /**
     * [파일 크기 초과 예외 처리]
     * - 설정된 파일 크기 제한을 초과하면 400 (BAD_REQUEST) 응답 반환
     *
     * @param e MaxUploadSizeExceededException
     * @return ApiResponse<String> (에러 메시지 포함)
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleMaxSizeException(MaxUploadSizeExceededException e) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, "파일 크기는 최대 10MB까지만 가능합니다.");
    }
}
