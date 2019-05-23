package org.alex.service;

import static com.mongodb.client.result.DeleteResult.acknowledged;
import static com.mongodb.client.result.DeleteResult.unacknowledged;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;
import java.util.Optional;

import org.alex.exceptions.NotFoundException;
import org.alex.persistence.PersonRepository;
import org.alex.service.model.People;
import org.alex.service.model.Person;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@RunWith(MockitoJUnitRunner.class)
public class PersonServiceTest {

  private static final String PERSON_COLLECTION = "person";
  private static final String ID_FIELD = "_id";
  private static final String ID1 = "Fred's id";
  private static final String ID2 = "Jemimas's id";
  private static final String INVALID_ID = "Bad Id";
  private static final String FULL_NAME_FIELD = "fullName";
  private static final String FULL_NAME1 = "Fred O'Splinge";
  private static final String FULL_NAME2 = "Jemima O'Splinge";
  private static final String NEW_NAME = "Freddy O'Splinge";
  private static final String INVALID_NAME = "Fred McSplinge";
  private static final String ID_NOT_FOUND_MESSAGE = "Person with id: '%s' not found.";
  private static final String EMPTY_STRING = "";
  private static final long ONE_DOCUMENT = 1l;
  private static final long ZERO_DOCUMENTS = 0l;

  @Rule
  public ExpectedException ee = ExpectedException.none();

  @Mock
  private PersonRepository repository;

  @Mock
  private MongoTemplate mongoTemplate;

  @InjectMocks
  private PersonService service;

  @Captor
  private ArgumentCaptor<Query> queryCaptor;

  @Captor
  private ArgumentCaptor<String> stringCaptor;
  
  @Captor
  private ArgumentCaptor<Person> personCaptor;

  @Test
  public void shouldSavePerson() {
    Person personToSave = getPerson(FULL_NAME1);
    Person testPerson = getPersonWithId(ID1, FULL_NAME1);

    doNothing().when(mongoTemplate).insert(personToSave);

    Person actualPerson = service.savePerson(personToSave);

    // This is a bit hacky but mimicks mongo setting the id on the model after
    // saving the new document - insert method is void so can't mock the return
    verify(mongoTemplate).insert(personCaptor.capture());
    personCaptor.getValue().setId(ID1);
    assertThat(actualPerson, is(testPerson));
  }

  @Test
  public void shouldSavePeople() {
    List<Person> listOfPeopleToSave = asList(getPerson(FULL_NAME1), getPerson(FULL_NAME2));
    List<Person> savedPeople = asList(getPersonWithId(ID1, FULL_NAME1), getPersonWithId(ID2, FULL_NAME2));
    People peopleToSave = getPeople(listOfPeopleToSave);
    People testPeople = getPeople(savedPeople);

    when(repository.insert(listOfPeopleToSave)).thenReturn(savedPeople);
    
    People actualPeople = service.savePeople(peopleToSave);

    assertThat(actualPeople, is(testPeople));
  }

  @Test
  public void shouldFindPersonById() {
    Person testPerson = getPerson(FULL_NAME1);

    when(repository.findById(ID1)).thenReturn(Optional.of(testPerson));

    Person actualPerson = service.getPersonById(ID1);

    assertThat(actualPerson, is(testPerson));
  }

  @Test
  public void shouldThrowNotFoundExceptionWithInvalidId() {
    ee.expect(NotFoundException.class);
    ee.expectMessage(format(ID_NOT_FOUND_MESSAGE, INVALID_ID));

    when(repository.findById(INVALID_ID)).thenReturn(Optional.empty());

    service.getPersonById(INVALID_ID);
  }

  @Test
  public void shouldFindAllPeople() {
    List<Person> queryResult = asList(getPersonWithId(ID1, FULL_NAME1), getPersonWithId(ID2, FULL_NAME2));
    People testPeople = getPeople(queryResult);

    when(repository.findAll()).thenReturn(queryResult);

    People actualPeople = service.getPeople(EMPTY_STRING);

    assertThat(actualPeople, is(testPeople));
  }

  @Test
  public void shouldFindPeopleByName() {
    List<Person> queryResult = singletonList(getPersonWithId(ID1, FULL_NAME1));
    People testPeople = getPeople(queryResult);
    Query fullNameQuery = getFullNameQuery(FULL_NAME1);

    when(mongoTemplate.find(fullNameQuery, Person.class, PERSON_COLLECTION))
      .thenReturn(queryResult);

    People actualPeople = service.getPeople(FULL_NAME1);

    assertThat(actualPeople, is(testPeople));
  }

  @Test
  public void shouldReturnEmptyListWhenNameIsInvalid() {
    Query invalidNameQuery = getFullNameQuery(INVALID_NAME);
    People nobody = getNobody();
    
    when(mongoTemplate.find(invalidNameQuery, Person.class, PERSON_COLLECTION))
      .thenReturn(emptyList());
    
    People actualPeople = service.getPeople(INVALID_NAME);

    assertThat(actualPeople, is(nobody));
  }
  
  @Test
  public void shouldUpdatePerson() {
    Person personToUpdate = getPersonWithId(ID1, FULL_NAME1);
    Person updatedPerson = getPersonWithId(ID1, NEW_NAME);

    when(repository.findById(ID1)).thenReturn(Optional.of(personToUpdate));

    Person actualPerson = service.updatePerson(updatedPerson, ID1);
    
    verify(mongoTemplate).save(updatedPerson, PERSON_COLLECTION);
    assertThat(actualPerson, is(updatedPerson));
  }

  @Test
  public void shouldNotUpdatePersonWithNullValues() {
    Person personToUpdate = getPersonWithId(ID1, FULL_NAME1);
    Person personWithNullName = getPersonWithId(ID1, null);

    when(repository.findById(ID1)).thenReturn(Optional.of(personToUpdate));

    Person actualPerson = service.updatePerson(personWithNullName, ID1);

    verify(mongoTemplate).save(personToUpdate, PERSON_COLLECTION);
    assertThat(actualPerson, is(personToUpdate));
  }
  
  @Test
  public void shouldDeletePerson() {
    Query testQuery = getIdQuery();

    when(mongoTemplate.remove(any(Query.class), any(String.class)))
      .thenReturn(acknowledged(ONE_DOCUMENT));

    long deletedDocumentCount = service.deletePerson(ID1);

    verify(mongoTemplate).remove(queryCaptor.capture(), stringCaptor.capture());
    assertThat(queryCaptor.getValue(), is(testQuery));
    assertThat(stringCaptor.getValue(), is(PERSON_COLLECTION));
    assertThat(deletedDocumentCount, is(ONE_DOCUMENT));
  }
  
  @Test
  public void shouldThrowNotFoundExceptionIfNoDocumentsDeleted() {
    ee.expect(NotFoundException.class);
    ee.expectMessage(format(ID_NOT_FOUND_MESSAGE, INVALID_ID));
    
    when(mongoTemplate.remove(any(Query.class), any(String.class)))
      .thenReturn(acknowledged(ZERO_DOCUMENTS));
    
    service.deletePerson(INVALID_ID);
  }
  
  @Test
  public void shouldThrowNotFoundExceptionIfDeletionNotAcknowledged() {
    ee.expect(NotFoundException.class);
    ee.expectMessage(format(ID_NOT_FOUND_MESSAGE, INVALID_ID));
    
    when(mongoTemplate.remove(any(Query.class), any(String.class)))
      .thenReturn(unacknowledged());
    
    service.deletePerson(INVALID_ID);
  }
  
  private People getPeople(List<Person> people) {
    return People.builder()
      .people(people)
      .build();
  }

  private People getNobody() {
    return People.builder()
      .people(emptyList())
      .build();
  }
  
  private Person getPerson(String name) {
    return Person.builder()
      .fullName(name)
      .build();
  }
  
  private Person getPersonWithId(String id, String name) {
    return Person.builder()
      .id(id)
      .fullName(name)
      .build();
  }

  private Query getIdQuery() {
    return new Query(where(ID_FIELD).is(ID1));
  }
  
  private Query getFullNameQuery(String name) {
  return new Query(where(FULL_NAME_FIELD).is(name));
  }
}