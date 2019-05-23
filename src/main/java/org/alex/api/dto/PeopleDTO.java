package org.alex.api.dto;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PeopleDTO {
  private final List<PersonDTO> people;
}