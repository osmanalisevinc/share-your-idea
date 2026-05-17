package com.share.exception;

 import com.share.constants.Constants;
 import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.naming.NoPermissionException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            BusinessException.class,
            IllegalArgumentException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            MissingRequestHeaderException.class,
            HttpClientErrorException.BadRequest.class,
    })

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestException(Exception ex) {
        log.debug("handleBadRequestException", ex);

        return ex.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestException(MethodArgumentNotValidException ex) {
        log.debug("handleBadRequestException", ex);

        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        return errors.toString();
    }

    @ExceptionHandler(NoPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleNoPermissionException(Exception ex) {
        log.debug("handleNoPermissionException", ex);

        return Constants.FORBIDDEN;
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoSuchElementException(Exception ex) {
        log.debug("handleNoSuchElementException", ex);

        return ex.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex) {
        log.debug("Unexpected exception", ex);

        return ex.getMessage();
    }


}
