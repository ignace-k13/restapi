package be.abis.exercise;

import be.abis.exercise.exception.PersonNotFoundException;
import be.abis.exercise.model.Address;
import be.abis.exercise.model.Company;
import be.abis.exercise.model.Person;
import be.abis.exercise.repository.PersonRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest {
	
	@Autowired
	PersonRepository personRepository;

	@Test
	@Order(1)
	public void person1ShouldBeCalledJohn() throws PersonNotFoundException {
		String firstName = personRepository.findPerson(1).getFirstName();
		assertEquals("John",firstName);
	}

	@Test
	@Order(2)
	public void person9ThrowsException() throws PersonNotFoundException {
		assertThrows(PersonNotFoundException.class,()->personRepository.findPerson(9));
	}

	@Test
	@Order(3)
	public void thereShouldBe3PersonsInTheFile(){
		int nrOfPersons = personRepository.getAllPersons().size();
		assertEquals(3,nrOfPersons);
	}

	@Test
	@Order(4)
	public void addNewPerson() throws IOException {
		Address a = new Address("Diestsevest",32,"3000","Leuven");
		Company c = new Company("Abis","016/455610","BE12345678",a);
		Person p = new Person(4,"Sandy","Schillebeeckx", LocalDate.of(1978,04,10),"sschillebeeckx@abis.be","abis123","nl",c);
		personRepository.addPerson(p);
	}

	@Test
	@Order(5)
	public void changePassWordOfAddedPerson() throws IOException, PersonNotFoundException {
		Person p = personRepository.findPerson("sschillebeeckx@abis.be","abis123");
		personRepository.changePassword(p,"blabla");
	}

	@Test
	@Order(6)
	public void deleteAddedPerson(){
		personRepository.deletePerson(4);
	}

	@Test
	@Order(7)
	public void thereAre2PersonsWorkingAtAbis(){
		List<Person> persons = personRepository.findPersonsByCompanyName("abis");
		assertEquals(2,persons.size());
	}
	

}
