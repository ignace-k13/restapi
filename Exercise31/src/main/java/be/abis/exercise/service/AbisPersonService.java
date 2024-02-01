package be.abis.exercise.service;

import be.abis.exercise.model.Login;
import be.abis.exercise.model.Password;
import be.abis.exercise.model.Person;
import be.abis.exercise.model.Persons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AbisPersonService implements PersonService {
	
	@Autowired
	private RestTemplate rt;
	
	private String baseUri = "http://localhost:8080/exercise/api/persons";
	

	@Override
	public List<Person> getAllPersons() {
		ResponseEntity<List<Person>> persons =
				rt.exchange(baseUri,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<List<Person>>(){});
		return persons.getBody();
	}

	/*@Override
	public List<Person> getAllPersons() {
		Person[] persons = rt.getForObject(baseUri,Person[].class);
		List<Person> personList = new ArrayList<>(Arrays.asList(persons));
		return personList;
	}*/

	/*@Override
	public List<Person> getAllPersons() {
		List persons = rt.getForObject(baseUri,List.class);
		return (List<Person>)persons;
	}*/


	@Override
	public Person findPerson(int id) {
		return rt.getForObject(baseUri+"/"+id,Person.class);
	}

	@Override
	public Person findPerson(String emailAddress, String passWord) {
		Login login = new Login();
		login.setEmail(emailAddress);
		login.setPassword(passWord);
		Person person = rt.postForObject(baseUri+"/login",login,Person.class);
		return person;
	}

	@Override
	public List<Person> findPersonsByCompanyName(String compName) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUri+"/query")
											 .queryParam("compname", compName);
		ResponseEntity<Persons> persons = rt.exchange(uriBuilder.toUriString(),HttpMethod.GET,null,Persons.class);
		return persons.getBody().getPersons();
	}

	@Override
	public void addPerson(Person p) {
		rt.postForObject(baseUri,p,Void.class);
		System.out.println("person added ");
	}

	@Override
	public void deletePerson(int id) {
		rt.delete(baseUri+"/"+id);
        System.out.println("person deleted ");
	}
	
	@Override
	public void changePassword(Person p){		
        rt.put(baseUri+"/"+p.getPersonId(),p);
        System.out.println("password changed ");
	}

	@Override
	public void changePassword(int id,String newpwd){
		Password password=new Password(newpwd);
		rt.patchForObject(baseUri+"/"+id +"/password", password,Void.class);
		System.out.println("password changed ");
	}


}
