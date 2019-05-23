package org.alex.service;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import com.mongodb.client.result.DeleteResult;

import org.alex.exceptions.NotFoundException;
import org.alex.persistence.PersonRepository;
import org.alex.service.model.People;
import org.alex.service.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

  private static final String MONGO_ID_FIELD = "_id";
  private static final String FULL_NAME_FIELD = "fullName";
  private static final String PERSON_COLLECTION = "person";
  private static final String ID_NOT_FOUND_MESSAGE = "Person with id: '%s' not found.";

  @Autowired
  private PersonRepository repo;

  @Autowired
  private MongoTemplate mongoTemplate;

  public Person savePerson(Person newPerson) {
    mongoTemplate.insert(newPerson);
    // We can directly return the incoming model - Mongo sets the 
    // newly created id on it as a side effect of the insert method
    return newPerson;
  }

  public People savePeople(People newPeople) {
    return People.builder()
      .people(repo.insert(newPeople.getPeople()))
      .build();
  }

  public Person getPersonById(String id) {
    return repo.findById(id)
      .orElseThrow(() -> new NotFoundException(format(ID_NOT_FOUND_MESSAGE, id)));
  }

  public People getPeople(String fullName) {
    List<Person> people;

    if (isEmpty(fullName)) {
      people = repo.findAll();
    } else {
      people = mongoTemplate.find(getFullNameQuery(fullName), Person.class, PERSON_COLLECTION);
    }

    return People.builder()
      .people(people)
      .build();
  }

  public Person updatePerson(Person updatedPerson, String id) {
    Person current = getPersonById(id);

    // This is not going to scale well for large numbers of fields..
    String newName = updatedPerson.getFullName();
    if (newName != null) {
      current.setFullName(newName);
    }

    mongoTemplate.save(current, PERSON_COLLECTION);

    return current;
  }

  public long deletePerson(String id) {
    DeleteResult result = mongoTemplate.remove(getIdQuery(id), PERSON_COLLECTION);
    boolean queryAcknowledged = result.wasAcknowledged();

    if (queryAcknowledged) {
      long deletedCount = result.getDeletedCount();
      if (deletedCount > 0) {
        return deletedCount;
      }
    }
    throw new NotFoundException(format(ID_NOT_FOUND_MESSAGE, id));
  }

  private Query getIdQuery(String id) {
	  return new Query(Criteria.where(MONGO_ID_FIELD).is(id));
  }

  private Query getFullNameQuery(String fullName) {
    return new Query(where(FULL_NAME_FIELD).is(fullName));
  }
}