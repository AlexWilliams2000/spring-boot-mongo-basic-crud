package org.alex.service.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class People {
  private List<Person> people;
}