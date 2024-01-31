package be.abis.exercise.controller;

import be.abis.exercise.model.Person;
import be.abis.exercise.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

	@Autowired PersonService ps;

	@GetMapping("persons")
	public Person findPersonByMailAndPwd(@RequestParam(name="mail") String emailAddress, @RequestParam(name="pwd") String passWord){
		Person p = ps.findPerson(emailAddress, passWord);
		return p;
	}


	
	
}
