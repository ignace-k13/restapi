package be.abis.exercise.service;

import be.abis.exercise.model.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface PersonService {

	List<Person> getAllPersons();
    Person findPerson(int id);
    Person findPerson(String emailAddress, String passWord);
    void addPerson(Person p) throws IOException;
    void deletePerson(int id);
    void changePassword(Person p, String newPswd) throws IOException;

}
