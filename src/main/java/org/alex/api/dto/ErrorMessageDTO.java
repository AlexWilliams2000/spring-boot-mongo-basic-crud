package org.alex.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorMessageDTO {
  private final String error;
}