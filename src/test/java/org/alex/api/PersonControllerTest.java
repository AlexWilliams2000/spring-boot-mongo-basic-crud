package org.alex.api;

import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import java.io.UnsupportedEncodingException;

import org.alex.exceptions.NotFoundException;
import org.alex.service.PersonService;
import org.alex.service.model.People;
import org.alex.service.model.Person;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {
  
  private static final String ERROR_FIELD = "error";
  private static final String PEOPLE = "people";
  private static final String PERSON_URL = "/person";
  private static final String PEOPLE_URL = "/people";
  
  private static final String MISSING_REQUEST_BODY_MESSAGE = "Required request body is missing";
  private static final String INVALID_ID_MESSAGE = "Person with id: '%s' not found.";

  private static final String JSON_PATH = "$";
  private static final String UTF_8 = "UTF-8";
  private static final String EMPTY_STRING = "";

  private static final String FULL_NAME_FIELD = "fullName";
  private static final String FULL_NAME1 = "Fred O'Splinge";
  private static final String UPDATED_NAME = "Freddy O'Splinge";
  private static final String FULL_NAME2 = "Jemima O'Splinge";
  private static final String ID_FIELD = "id";
  private static final String ID1 = "uuid1";
  private static final String ID2 = "uuid2";

  @MockBean
  private PersonService personService;

  @Autowired
  private MockMvc mvc;

  @Test
  public void shouldPostNewPersonAndReturn201Created() throws Exception {
    String url = personUrl();
    Person requestModel = getPersonWithNameModel(FULL_NAME1);
    Person responseModel = getPersonWithIdAndNameModel(ID1, FULL_NAME1);
    String requestBody = getPersonWithNameJson(FULL_NAME1)
      .toString();
    String responseBody = getPersonWithIdAndNameJson(ID1, FULL_NAME1)
      .toString();

    when(personService.savePerson((requestModel)))
      .thenReturn(responseModel);

    mvc.perform(post(url).contentType(APPLICATION_JSON_UTF8).content(requestBody))
      .andExpect(status().isCreated())
      .andExpect(content().json(responseBody));

    verify(personService).savePerson(requestModel);
  }

  @Test
  public void shouldReturn400BadRequestIfPostPersonRequestBodyIsMissing() throws Exception {
    String url = personUrl();
    String errorResponseBody = getMissingRequestBodyErrorResponseJson()
      .toString();

    mvc.perform(post(url).contentType(APPLICATION_JSON_UTF8))
      .andExpect(status().isBadRequest())
      .andExpect(content().json(errorResponseBody));
  }

  @Test
  public void shouldPostNewPeopleAndReturn201Created() throws Exception {
    String url = peopleUrl();
    People requestModel = getPeopleWithNameModel();
    People responseModel = getPeopleWithIdAndNameModel();
    String newPeopleRequestBody = getPeopleJson()
      .toString();
    String newPeopleResponseBody = getPeopleWithIdJson()
      .toString();

    when(personService.savePeople((requestModel)))
      .thenReturn(responseModel);

    mvc.perform(post(url).contentType(APPLICATION_JSON_UTF8).content(newPeopleRequestBody))
      .andExpect(status().isCreated())
      .andExpect(content().json(newPeopleResponseBody));

    verify(personService).savePeople(requestModel);

  }

  @Test
  public void shouldReturn400BadRequestIfPostPeopleRequestBodyIsMissing() throws Exception {
    String url = peopleUrl();
    String errorResponseBody = getErrorResponseJson(MISSING_REQUEST_BODY_MESSAGE)
      .toString();

    mvc.perform(post(url).contentType(APPLICATION_JSON_UTF8))
      .andExpect(status().isBadRequest())
      .andExpect(content().json(errorResponseBody));
  }

  @Test
  public void shouldGetPersonByIdAndReturn200Ok() throws Exception {
    String url = personWithIdUrl();
    Person responseModel = getPersonWithIdAndNameModel(ID1, FULL_NAME1);
    String responseBody = getPersonWithIdAndNameJson(ID1, FULL_NAME1)
      .toString();

    when(personService.getPersonById(ID1))
      .thenReturn(responseModel);

    mvc.perform(get(url)).andExpect(status().isOk())
      .andExpect(content().json(responseBody));

    verify(personService).getPersonById(ID1);
  }

  @Test
  public void shouldReturn404NotFoundOnGetPersonWithInvalidId() throws Exception {
    String url = personWithIdUrl();
    String responseBody = getErrorResponseJson(format(INVALID_ID_MESSAGE, ID1))
      .toString();

    doThrow(new NotFoundException(format(INVALID_ID_MESSAGE, ID1)))
      .when(personService).getPersonById(ID1);

    mvc.perform(get(url)).andExpect(status().isNotFound())
      .andExpect(content().json(responseBody));

    verify(personService).getPersonById(ID1);
  }

  @Test
  public void shouldGetPeopleByFullNameAndReturn200Ok() throws Exception {
    String url = peopleUrlWithNameParameter();
    People responseModel = getPeopleWithDifferentIdsAndSameNameModel();
    String responseBody = getPeopleWithDifferentIdsAndSameNameJson()
      .toString();

    when(personService.getPeople(FULL_NAME1))
      .thenReturn(responseModel);

    mvc.perform(get(url)).andExpect(status().isOk())
      .andExpect(content().json(responseBody));

    verify(personService).getPeople(FULL_NAME1);
  }

  @Test
  public void shouldGetAllPeopleAndReturn200Ok() throws Exception {
    String url = peopleUrl();
    People responseModel = getPeopleWithIdAndNameModel();
    String responseBody = getPeopleWithIdJson()
      .toString();

    when(personService.getPeople(EMPTY_STRING))
      .thenReturn(responseModel);

    mvc.perform(get(url)).andExpect(status().isOk())
      .andExpect(content().json(responseBody));

    verify(personService).getPeople(EMPTY_STRING);
  }

  @Test
  public void shouldPutToExisitngPersonAndReturn200Ok() throws Exception {
    String url = personWithIdUrl();
    Person personWithUpdatedName = getPersonWithIdAndNameModel(ID1, UPDATED_NAME);

    // Identical since there's only one field at the moment,
    // response body should be the whole person not just the fields to update
    String requestBody = getPersonWithIdAndNameJson(ID1, UPDATED_NAME)
      .toString();
    String responseBody = requestBody;

    when(personService.updatePerson(personWithUpdatedName, ID1))
      .thenReturn(personWithUpdatedName);

    mvc.perform(put(url).contentType(APPLICATION_JSON_UTF8).content(requestBody))
      .andExpect(status().isOk())
      .andExpect(content().json(responseBody));

    verify(personService).updatePerson(personWithUpdatedName, ID1);
  }

  @Test
  public void shouldReturn404NotFoundOnPutToPersonWithInvalidId() throws Exception {
    String url = personWithIdUrl();
    Person requestModel = getPersonWithIdAndNameModel(ID1, FULL_NAME1);
    String requestBody = getPersonWithIdAndNameJson(ID1, FULL_NAME1)
      .toString();
    String responseBody = getErrorResponseJson(format(INVALID_ID_MESSAGE, ID1))
      .toString();

    doThrow(new NotFoundException(format(INVALID_ID_MESSAGE, ID1)))
      .when(personService).updatePerson(requestModel, ID1);

    mvc.perform(put(url).contentType(APPLICATION_JSON_UTF8).content(requestBody))
      .andExpect(status().isNotFound())
      .andExpect(content().json(responseBody));

    verify(personService).updatePerson(requestModel, ID1);
  }

  @Test
  public void shouldReturn400BadRequestIfPutToPersonRequestBodyIsMissing() throws Exception {
    String url = personWithIdUrl();
    String errorResponseBody = getErrorResponseJson(MISSING_REQUEST_BODY_MESSAGE)
      .toString();

    mvc.perform(put(url).contentType(APPLICATION_JSON_UTF8))
      .andExpect(status().isBadRequest())
      .andExpect(content().json(errorResponseBody));
  }

  @Test
  public void shouldDeletePersonAndReturn204NoContent() throws Exception {
    String url = personWithIdUrl();

    mvc.perform(delete(url))
      .andExpect(status().isNoContent())
      .andExpect(jsonPath(JSON_PATH).doesNotExist());

    verify(personService).deletePerson(ID1);
  }

  @Test
  public void shouldReturn404NotFoundOnDeletePersonWithInvalidId() throws Exception {
    String url = personWithIdUrl();
    String responseBody = getErrorResponseJson(format(INVALID_ID_MESSAGE, ID1))
      .toString();

    doThrow(new NotFoundException(format(INVALID_ID_MESSAGE, ID1)))
      .when(personService).deletePerson(ID1);

    mvc.perform(delete(url))
      .andExpect(status().isNotFound())
      .andExpect(content().json(responseBody));

    verify(personService).deletePerson(ID1);
  }

  private String personUrl() {
    return fromPath(PERSON_URL).build()
      .toString();
  }

  private String personWithIdUrl() {
    return fromPath(PERSON_URL).pathSegment(ID1)
      .build()
      .toString();
  }

  private String peopleUrl() {
    return fromPath(PEOPLE_URL).build()
      .toString();
  }

  private String peopleUrlWithNameParameter() throws UnsupportedEncodingException {
    return fromPath(PEOPLE_URL).queryParam(FULL_NAME_FIELD, encode(FULL_NAME1, UTF_8))
      .build()
      .toString();
  }

  private JSONObject getPersonWithNameJson(String name) throws JSONException {
    return new JSONObject().put(FULL_NAME_FIELD, name);
  }

  private JSONObject getPersonWithIdAndNameJson(String id, String name) throws JSONException {
    return getPersonWithNameJson(name).put(ID_FIELD, id);
  }

  private JSONObject getPeopleJson() throws JSONException {
    return new JSONObject().put(
      PEOPLE, 
      new JSONArray()
        .put(getPersonWithNameJson(FULL_NAME1))
        .put(getPersonWithNameJson(FULL_NAME2)));
  }

  private JSONObject getPeopleWithIdJson() throws JSONException {
    return new JSONObject().put(
      PEOPLE, 
      new JSONArray()
        .put(getPersonWithIdAndNameJson(ID1, FULL_NAME1))
        .put(getPersonWithIdAndNameJson(ID2, FULL_NAME2)));
  }

  private JSONObject getPeopleWithDifferentIdsAndSameNameJson() throws JSONException {
    return new JSONObject().put(
      PEOPLE, 
      new JSONArray()
        .put(getPersonWithIdAndNameJson(ID1, FULL_NAME1))
        .put(getPersonWithIdAndNameJson(ID2, FULL_NAME1)));
  }

  private JSONObject getErrorResponseJson(String message) throws JSONException {
    return new JSONObject().put(ERROR_FIELD, message);
  }

  private JSONObject getMissingRequestBodyErrorResponseJson() throws JSONException {
    return new JSONObject().put(ERROR_FIELD, MISSING_REQUEST_BODY_MESSAGE);
  }

  private Person getPersonWithNameModel(String name) {
    return Person.builder()
      .fullName(name)
      .build();
  }
  
  private Person getPersonWithIdAndNameModel(String id, String name) {
    return getPersonWithNameModel(name).setId(id);
  }

  private People getPeopleWithNameModel() {
    return People.builder()
      .people(asList(
        getPersonWithNameModel(FULL_NAME1), 
        getPersonWithNameModel(FULL_NAME2)))
      .build();
  }
  
  private People getPeopleWithIdAndNameModel() {
    return People.builder()
      .people(asList(
        getPersonWithIdAndNameModel(ID1, FULL_NAME1), 
        getPersonWithIdAndNameModel(ID2, FULL_NAME2)))
      .build();
  }
  
  private People getPeopleWithDifferentIdsAndSameNameModel() {
    return People.builder()
      .people(asList(
        getPersonWithIdAndNameModel(ID1, FULL_NAME1), 
        getPersonWithIdAndNameModel(ID2, FULL_NAME1)))
      .build();
  }
}