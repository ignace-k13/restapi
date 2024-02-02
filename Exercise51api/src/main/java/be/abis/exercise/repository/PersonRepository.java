package be.abis.exercise.repository;

import be.abis.exercise.exception.PersonAlreadyExistsException;
import be.abis.exercise.exception.PersonCanNotBeDeletedException;
import be.abis.exercise.exception.PersonNotFoundException;
import be.abis.exercise.model.Person;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PersonRepository {

	List<Person> getAllPersons();
	Person findPerson(int id) throws PersonNotFoundException;
	Person findPerson(String emailAddress, String passWord) throws PersonNotFoundException;
	void addPerson(Person p) throws IOException, PersonAlreadyExistsException;
	void deletePerson(int id) throws PersonCanNotBeDeletedException;
	void changePassword(Person p, String newPswd) throws IOException;
	List<Person> findPersonsByCompanyName(String compName);
	Map<Integer, String> getKeys();

}
