package com.vawndev.spring_boot_readnovel.Exceptions;

import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // miss params
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(400);
        apiResponse.setMessage("Missing request parameter: " + ex.getParameterName());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    //invalid params
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(400);
        apiResponse.setMessage("Invalid value for parameter: " + ex.getName());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // miss path variable
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiResponse> handleMissingPathVariable(MissingPathVariableException ex) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(400);
        apiResponse.setMessage("Missing path variable: " + ex.getVariableName());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // invaild path variable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolation(ConstraintViolationException ex) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(400);
        apiResponse.setMessage("Invalid path variable: " + ex.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }


    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(exception.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = org.springframework.security.access.AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = org.springframework.dao.DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse> handlingDataIntegrityViolationException(DataIntegrityViolationException exception) {
        ErrorCode errorCode = ErrorCode.ERROR_SAVE_DATA;
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        ApiResponse apiResponse = new ApiResponse();
        List<Map<String, Object>> errors = new ArrayList<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            String enumKey = fieldError.getDefaultMessage(); // VD: "INVALID_PASSWORD"

            // Tìm ErrorCode phù hợp
            ErrorCode errorCode;
            try {
                errorCode = ErrorCode.valueOf(enumKey);
            } catch (IllegalArgumentException e) {
                errorCode = ErrorCode.INVALID_KEY;
            }

            // Lấy tham số từ annotation validation
            Map<String, Object> attributes = fieldError.unwrap(ConstraintViolation.class)
                    .getConstraintDescriptor().getAttributes();

            // Lọc chỉ lấy giá trị kiểu số (bỏ qua payload, groups, message)
            List<Object> args = attributes.entrySet().stream()
                    .filter(entry -> entry.getValue() instanceof Number) // Chỉ lấy giá trị kiểu số
                    .map(Map.Entry::getValue)
                    .toList(); // Chuyển thành danh sách

            // Format message với tham số động
            String formattedMessage = errorCode.getFormattedMessage(args.toArray());

            // Thêm lỗi vào danh sách
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("field", fieldError.getField());
            errorDetails.put("message", formattedMessage);
            errors.add(errorDetails);
        }

        apiResponse.setCode(400);
        apiResponse.setMessage("Validation failed");
        apiResponse.setResult(errors); // Trả về danh sách lỗi

        return ResponseEntity.badRequest().body(apiResponse);
    }
}
