package org.alex.api.converter;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.alex.api.dto.PeopleDTO;
import org.alex.api.dto.PersonDTO;
import org.alex.service.model.People;
import org.alex.service.model.Person;

public class PersonConverter {
  
  private PersonConverter() {
  }

  public static PersonDTO convertPersonModelToDto(Person person) {
    if (person == null) {
      return null;
    }

    return PersonDTO.builder()
      .id(person.getId())
      .fullName(person.getFullName())
      .build();
  }

  public static Person convertPersonDtoToModel(PersonDTO dto) {
    if (dto == null) {
      return null;
    }

    return Person.builder()
      .id(dto.getId())
      .fullName(dto.getFullName())
      .build();
  }

  public static PeopleDTO convertPeopleModelToDto(People model) {
    if (model == null) {
      return null;
    }

    return PeopleDTO.builder()
      .people(model.getPeople()
        .stream()
        .map(PersonConverter::convertPersonModelToDto)
        .collect(toList()))
      .build();
  }

  public static People convertPeopleDtoToModel(PeopleDTO dto) {
    if (dto == null) {
      return null;
    } else if (dto.getPeople() == null) {
      return getPeopleModel(emptyList());
    }

    return getPeopleModel(dto.getPeople()
      .stream()
      .map(PersonConverter::convertPersonDtoToModel)
      .collect(toList()));
  }

  private static People getPeopleModel(List<Person> people) {
    return People.builder()
      .people(people)
      .build();
  }
}