package org.alex.api;

import static org.alex.api.converter.PersonConverter.convertPeopleDtoToModel;
import static org.alex.api.converter.PersonConverter.convertPeopleModelToDto;
import static org.alex.api.converter.PersonConverter.convertPersonDtoToModel;
import static org.alex.api.converter.PersonConverter.convertPersonModelToDto;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.util.UriUtils.decode;

import org.alex.api.dto.PeopleDTO;
import org.alex.api.dto.PersonDTO;
import org.alex.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

  private static final char SPACE_CHAR = ' ';
  private static final char URL_SPACE_DELIMITER_CHAR = '+';
  private static final String UTF_8 = "UTF-8";

  @Autowired
  private PersonService personService;

  // TODO: Replace MongoDB Ids with UUID
  // TODO: Avoid exposing internal details e.g. db schema, internal error messages
  // TODO: Add validation on requests
  // TODO: Add role and user based security

  @PostMapping("/person")
  @ResponseStatus(value = CREATED)
  public PersonDTO savePerson(@RequestBody PersonDTO request) {
    return convertPersonModelToDto(
      personService.savePerson(
        convertPersonDtoToModel(request)));
  }

  @PostMapping("/people")
  @ResponseStatus(value = CREATED)
  public PeopleDTO savePeople(@RequestBody PeopleDTO request) {
    return convertPeopleModelToDto(
      personService.savePeople(
        convertPeopleDtoToModel(request)));
  }

  @GetMapping("/person/{personId}")
  public PersonDTO getPerson(@PathVariable(value = "personId") String id) {
    return convertPersonModelToDto(personService.getPersonById(id));
  }

  @GetMapping("/people")
  public PeopleDTO getPeople(@RequestParam(defaultValue = "") String fullName) {
    return convertPeopleModelToDto(
        personService.getPeople(
          decode(fullName, UTF_8).replace(URL_SPACE_DELIMITER_CHAR, SPACE_CHAR)));
  }

  @PutMapping("/person/{personId}")
  public PersonDTO updatePerson(
    @RequestBody PersonDTO personUpdate, 
    @PathVariable(value = "personId") String id) {
    return convertPersonModelToDto(
      personService.updatePerson(
        convertPersonDtoToModel(personUpdate), id));
  }

  @DeleteMapping("/person/{personId}")
  @ResponseStatus(value = NO_CONTENT)
  public void deletePerson(@PathVariable(value = "personId") String id) {
    personService.deletePerson(id);
  }
}