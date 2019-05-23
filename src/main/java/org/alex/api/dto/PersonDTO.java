package org.alex.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonDTO {
  private final String id;
  private final String fullName;
}