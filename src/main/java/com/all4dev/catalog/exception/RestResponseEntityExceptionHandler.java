package com.all4dev.catalog.exception;

import com.all4dev.catalog.CatalogApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogApplication.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        List<String> errorList = new ArrayList<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName;
            try {
                fieldName = ((FieldError) error).getField();

            } catch (ClassCastException ex) {
                fieldName = error.getObjectName();
            }
            String message = error.getDefaultMessage();
            errorList.add(String.format("%s: %s\n", fieldName, message));
        });
        LOGGER.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new ApiError(HttpStatus.valueOf(statusCode.value()), "Request/arguments are invalid!", errorList), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        LOGGER.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new ApiError(HttpStatus.valueOf(statusCode.value()), "Internal Server Error!", exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NullPointerException.class})
    protected ResponseEntity<Object> handleNullPointerException(Exception exception) {
        LOGGER.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error!", "Please contact developer team for further debuggen!"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ResourceAlreadyExistException.class})
    protected ResponseEntity<Object> handleResourceAlreadyExistException(Exception exception) {
        LOGGER.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST, "Resource already exist!", exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    protected ResponseEntity<Object> handleResourceNotFoundException(Exception exception) {
        LOGGER.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND, "Resource not found!", exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({CSVImportExceptionException.class})
    protected ResponseEntity<Object> handleCSVImportExceptionException(CSVImportExceptionException exception) {
        List<String> errorList = new ArrayList<>();
        exception.getMap().forEach((line, error) -> {
            int lineNumber = line;
            String message = error.getMessage();
            errorList.add(String.format("line %s: %s\n", lineNumber, message));
        });
        LOGGER.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST, "CSV importer partial failed!", errorList), HttpStatus.BAD_REQUEST);    }

    @ExceptionHandler({InvalidFileNameException.class})
    protected ResponseEntity<Object> handleInvalidFileNameException(Exception exception) {
        LOGGER.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST, "Filename is invalid!", exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
