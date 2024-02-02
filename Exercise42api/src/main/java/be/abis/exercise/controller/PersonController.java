package be.abis.exercise.controller;

import be.abis.exercise.dto.Login;
import be.abis.exercise.dto.Password;
import be.abis.exercise.dto.Persons;
import be.abis.exercise.exception.ApiError;
import be.abis.exercise.exception.PersonAlreadyExistsException;
import be.abis.exercise.exception.PersonCanNotBeDeletedException;
import be.abis.exercise.exception.PersonNotFoundException;
import be.abis.exercise.model.Person;
import be.abis.exercise.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("persons")
public class PersonController {

	@Autowired
	PersonService ps;

	@GetMapping("")
	public List<Person> getAllPersons() {
		return ps.getAllPersons();
	}

	@GetMapping("{id}")
	public ResponseEntity<? extends Object> findPerson(@PathVariable("id") int id) {

		try {
			Person p = ps.findPerson(id);
			return new ResponseEntity<Person>(p, HttpStatus.OK);
		} catch (PersonNotFoundException pnfe) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			ApiError err = new ApiError("person not found", status.value(), pnfe.getMessage());
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
			return new ResponseEntity<ApiError>(err, responseHeaders, status);
		}

	}

	@GetMapping(path="/query", produces= MediaType.APPLICATION_XML_VALUE)
	public Persons findPersonsByCompanyName(@RequestParam("compname") String compName) {
		Persons persons= new Persons();
		persons.setPersons( ps.findPersonsByCompanyName(compName));
		return persons;
	}

	@PostMapping("/login")
	public ResponseEntity<Object> findPersonByMailAndPwd(@RequestBody Login login) {

		try {
			Person p = ps.findPerson(login.getEmail(), login.getPassword());
			return new ResponseEntity<Object>(p, HttpStatus.OK);
		} catch (PersonNotFoundException pnfe) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			ApiError err = new ApiError("person not found", status.value(), pnfe.getMessage());
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
			return new ResponseEntity<Object>(err, responseHeaders, status);
		}
	}

	@PostMapping(path="", consumes={MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
	public void addPerson(@RequestBody Person p) throws PersonAlreadyExistsException {
		try {
			ps.addPerson(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@DeleteMapping("{id}")
	public void deletePerson(@PathVariable("id") int id) throws PersonCanNotBeDeletedException {
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
	public void patchPassword(@PathVariable("id") int id, @RequestBody Password password) throws PersonNotFoundException {
		Person p = ps.findPerson(id);
		try {
			System.out.println("changing password to newpswd= " + password.getPassword());
			ps.changePassword(p, password.getPassword());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}