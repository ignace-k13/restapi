package be.abis.exercise.service;

import be.abis.exercise.model.Person;

import java.util.List;

public interface PersonService {
	
	    List<Person> getAllPersons();
	    Person findPerson(int id);
	    Person findPerson(String emailAddress, String passWord);
	    List<Person> findPersonsByCompanyName(String compName);
	    void addPerson(Person p);
	    void deletePerson(int id);
	    void changePassword(Person p);
	    void changePassword(int id,String newpwd);
}
