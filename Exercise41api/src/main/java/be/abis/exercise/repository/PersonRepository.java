package be.abis.exercise.repository;

import be.abis.exercise.exception.PersonNotFoundException;
import be.abis.exercise.model.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface PersonRepository {
	
	List<Person> getAllPersons();
	Person findPerson(int id) throws PersonNotFoundException;
	Person findPerson(String emailAddress, String passWord) throws PersonNotFoundException;
	void addPerson(Person p) throws IOException;
	void deletePerson(int id);
	void changePassword(Person p, String newPswd) throws IOException;
	List<Person> findPersonsByCompanyName(String compName);
}
