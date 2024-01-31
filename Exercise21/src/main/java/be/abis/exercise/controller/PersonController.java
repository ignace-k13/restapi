package be.abis.exercise.controller;

import be.abis.exercise.dto.Password;
import be.abis.exercise.model.Person;
import be.abis.exercise.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("persons")
public class PersonController {

	@Autowired
	PersonService ps;

	@GetMapping("login")
	public Person findPersonByMailAndPwd(@RequestParam(name = "mail") String emailAddress, @RequestParam(name = "pwd") String passWord) {
		Person p = ps.findPerson(emailAddress, passWord);
		return p;
	}

	@GetMapping("")
	public List<Person> getAllPersons() {
		return ps.getAllPersons();
	}

	@GetMapping("{id}")
	public Person findPerson(@PathVariable int id) {
		return ps.findPerson(id);
	}

	@PostMapping("")
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