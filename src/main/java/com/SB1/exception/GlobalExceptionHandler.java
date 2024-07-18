package com.SB1.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.SB1.dto.request.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = Exception.class)
	ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) 
	{
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setCode(999);
		apiResponse.setMessage(exception.getMessage());
		return ResponseEntity.badRequest().body(apiResponse);
	}
	
	@ExceptionHandler(value = AppException.class)
	ResponseEntity<ApiResponse> handlingAppException(AppException exception) 
	{
		ErrorCode errorCode = exception.getErrorCode();
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());
		return ResponseEntity.badRequest().body(apiResponse);
	}
	
	@ExceptionHandler(value = MethodArgumentNotValidException.class) 
	ResponseEntity<ApiResponse> handingValidation (MethodArgumentNotValidException exception) {
		String enumkey = exception.getFieldError().getDefaultMessage();
		
		
		ErrorCode errorCode = ErrorCode.INVALID_KEY;
		
		try {
			errorCode = ErrorCode.valueOf(enumkey);
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
		}
		
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());
		return ResponseEntity.badRequest().body(apiResponse);
	}
}
