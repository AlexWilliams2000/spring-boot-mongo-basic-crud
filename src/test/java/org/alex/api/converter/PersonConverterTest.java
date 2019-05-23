package org.alex.api.converter;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.alex.api.dto.PeopleDTO;
import org.alex.api.dto.PersonDTO;
import org.alex.service.model.People;
import org.alex.service.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PersonConverterTest {

  private static final String NAME1 = "Fred O'Splinge";
  private static final String NAME2 = "Jemima O'Splinge";
  private static final String ID1 = "id1";
  private static final String ID2 = "id2";

  @Test
  public void shouldConvertPersonDTOtoModel() {
    PersonDTO dto = getPersonDto(ID1, NAME1);
    Person testModel = getPersonModel(ID1, NAME1);

    Person actualModel = PersonConverter.convertPersonDtoToModel(dto);

    assertThat(actualModel, is(testModel));
  }

  @Test
  public void shouldHandleNullDto() {
    Person actualModel = PersonConverter.convertPersonDtoToModel(null);

    assertThat(actualModel, is(nullValue()));
  }

  @Test
  public void shouldConvertPersonModeltoDTO() {
    Person person = getPersonModel(ID1, NAME1);
    PersonDTO testDto = getPersonDto(ID1, NAME1);

    PersonDTO actualDto = PersonConverter.convertPersonModelToDto(person);

    assertThat(actualDto, is(testDto));
  }

  @Test
  public void shouldHandleNullModel() {
    PersonDTO actualDto = PersonConverter.convertPersonModelToDto(null);

    assertThat(actualDto, is(nullValue()));
  }

  @Test
  public void shouldConvertPeopleDtoToModel() {
    People testModel = getPeopleModel();
    PeopleDTO dto = getPeopleDto();

    People actualModel = PersonConverter.convertPeopleDtoToModel(dto);
    assertThat(actualModel, is(testModel));
  }

  @Test
  public void shouldHandleNullPeopleDto() {
    People actualDto = PersonConverter.convertPeopleDtoToModel(null);

    assertThat(actualDto, is(nullValue()));
  }

  @Test
  public void shouldHandleNullPesonDtoList() {
    PeopleDTO testDto = getPeopleDtoWithNullPersonDtoList();
    People testModel = getPeopleModelWithEmptyPersonList();

    People actualModel = PersonConverter.convertPeopleDtoToModel(testDto);
    assertThat(actualModel, is(testModel));
  }

  @Test
  public void shouldConvertPeopleModelToDto() {
    PeopleDTO testDto = getPeopleDto();
    People model = getPeopleModel();

    PeopleDTO actualDto = PersonConverter.convertPeopleModelToDto(model);
    assertThat(actualDto, is(testDto));
  }

  @Test
  public void shouldHandleNullPeopleModel() {
    PeopleDTO actualDto = PersonConverter.convertPeopleModelToDto(null);

    assertThat(actualDto, is(nullValue()));
  }

  private Person getPersonModel(String id, String name) {
    return Person.builder()
      .id(id)
      .fullName(name)
      .build();
  }

  private PersonDTO getPersonDto(String id, String name) {
    return PersonDTO.builder()
      .id(id)
      .fullName(name)
      .build();
  }

  private People getPeopleModel() {
    return People.builder()
      .people(asList(getPersonModel(ID1, NAME1), getPersonModel(ID2, NAME2)))
      .build();
  }

  private PeopleDTO getPeopleDto() {
    return PeopleDTO.builder()
      .people(asList(getPersonDto(ID1, NAME1), getPersonDto(ID2, NAME2)))
      .build();
  }

  private People getPeopleModelWithEmptyPersonList() {
    return People.builder()
      .people(emptyList())
      .build();
  }

  private PeopleDTO getPeopleDtoWithNullPersonDtoList() {
    return PeopleDTO.builder()
      .build();
  }
}