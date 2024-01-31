package be.abis.exercise.controller;

import be.abis.exercise.dto.Login;
import be.abis.exercise.dto.Password;
import be.abis.exercise.dto.Persons;
import be.abis.exercise.model.Person;
import be.abis.exercise.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="persons")
public class PersonController {

	@Autowired
	PersonService ps;

	@GetMapping("")
	public List<Person> getAllPersons() {
		return ps.getAllPersons();
	}

	@GetMapping(path="{id}")
	public Person findPerson(@PathVariable int id) {
		return ps.findPerson(id);
	}

	@GetMapping(path="/query", produces= MediaType.APPLICATION_XML_VALUE)
	public Persons findPersonsByCompanyName(@RequestParam("compname") String compName) {
		Persons persons= new Persons();
		persons.setPersons( ps.findPersonsByCompanyName(compName));
		return persons;
	}

	@PostMapping("/login")
	public Person findPersonByMailAndPwd(@RequestBody Login login){
		Person p = ps.findPerson(login.getEmail(), login.getPassword());
		return p;
	}

	@PostMapping(path="", consumes={MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
	public void addPerson(@RequestBody Person p) {
		try {
			ps.addPerson(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@DeleteMapping("{id}")
	public void deletePerson(@PathVariable("id") int id) {
		ps.deletePerson(id);
	}


	@PutMapping("{id}")
	public void changePasswordviaPerson(@PathVariable("id") int id, @RequestBody Person person) {
		try {
			System.out.println("changing password to newpswd= " + person.getPassword());
			ps.changePassword(person, person.getPassword());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@PatchMapping("{id}/password")
	public void patchPassword(@PathVariable("id") int id, @RequestBody Password password) {
		Person p = ps.findPerson(id);
		try {
			System.out.println("changing password to newpswd= " + password.getPassword());
			ps.changePassword(p, password.getPassword());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}