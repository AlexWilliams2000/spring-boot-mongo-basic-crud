package org.alex.exceptions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.alex.api.dto.ErrorMessageDTO;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandling {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(NOT_FOUND)
  public ErrorMessageDTO handleDocumentNotFoundException(NotFoundException ex) {
    return getErrorDTO(ex.getMessage());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(BAD_REQUEST)
  public ErrorMessageDTO handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    // Get the cause without the internal detail
    return getErrorDTO(ex.getMessage().split(":")[0]);
  }

  private ErrorMessageDTO getErrorDTO(String message) {
    return ErrorMessageDTO.builder().error(message).build();
  }
}