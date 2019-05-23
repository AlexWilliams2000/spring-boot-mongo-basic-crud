package org.alex.service.model;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
public class Person {

  @Id
  private String id;
  private String fullName;
}