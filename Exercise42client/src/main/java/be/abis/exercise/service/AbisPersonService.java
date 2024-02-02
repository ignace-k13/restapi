package be.abis.exercise.service;

import be.abis.exercise.exception.ApiError;
import be.abis.exercise.exception.PersonAlreadyExistsException;
import be.abis.exercise.exception.PersonCanNotBeDeletedException;
import be.abis.exercise.exception.PersonNotFoundException;
import be.abis.exercise.model.Login;
import be.abis.exercise.model.Password;
import be.abis.exercise.model.Person;
import be.abis.exercise.model.Persons;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class AbisPersonService implements PersonService {
	
	@Autowired
	private RestTemplate rt;
	
	private String baseUri = "http://localhost:8080/exercise/api/persons";
	

	@Override
	public List<Person> getAllPersons() {
		ResponseEntity<List<Person>> persons = rt.exchange(baseUri,HttpMethod.GET,null,new ParameterizedTypeReference<List<Person>>(){});
		return persons.getBody();
	}

	/*@Override
	public List<Person> getAllPersons() {
		Person[] persons = rt.getForObject(baseUri,Person[].class);
		List<Person> personList = new ArrayList<>(Arrays.asList(persons));
		return personList;
	}*/

	@Override
	public Person findPerson(int id) throws Exception {
		ResponseEntity<? extends Object> re=null;
		Person p = null;
		try {
			re= rt.getForEntity(baseUri+"/"+id,Person.class);
			p=(Person)re.getBody();
		}catch (HttpStatusCodeException e) {
			if (HttpStatus.NOT_FOUND == e.getStatusCode()) {
				String serr = e.getResponseBodyAsString();
				ApiError ae=new ObjectMapper().readValue(serr,ApiError.class);
				throw new PersonNotFoundException(ae.getDescription());

			} else {
				throw new Exception("some other error occurred");
			}
		}
		return p;

	}

	@Override
	public Person findPerson(String emailAddress, String passWord) throws Exception{
		Login login = new Login();
		login.setEmail(emailAddress);
		login.setPassword(passWord);
		ResponseEntity<? extends Object> re=null;
		Person p = null;
		try {
			re= rt.postForEntity(baseUri+"/login",login,Person.class);
			p=(Person)re.getBody();
		}catch (HttpStatusCodeException e) {
			if (HttpStatus.NOT_FOUND == e.getStatusCode()) {
				String serr = e.getResponseBodyAsString();
				ApiError ae=new ObjectMapper().readValue(serr,ApiError.class);
				throw new PersonNotFoundException(ae.getDescription());

			} else {
				throw new Exception("some other error occurred");
			}
		}
		return p;
	}

	@Override
	public List<Person> findPersonsByCompanyName(String compName) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUri+"/query")
											 .queryParam("compname", compName);
		ResponseEntity<Persons> persons = rt.exchange(uriBuilder.toUriString(),HttpMethod.GET,null,Persons.class);
		return persons.getBody().getPersons();
	}

	@Override
	public void addPerson(Person p) throws Exception{
		ResponseEntity<? extends Object> re=null;
		try {
			re=rt.postForEntity(baseUri,p,Void.class);
			System.out.println("person added ");
		}catch (HttpStatusCodeException e) {
			if (HttpStatus.CONFLICT == e.getStatusCode()) {
				String serr = e.getResponseBodyAsString();
				ApiError ae=new ObjectMapper().readValue(serr,ApiError.class);
				throw new PersonAlreadyExistsException(ae.getDescription());

			} else {
				throw new Exception("some other error occurred");
			}
		}
	}

	@Override
	public void deletePerson(int id) throws Exception{
		ResponseEntity<? extends Object> re=null;
		try {
			re=rt.exchange(baseUri+"/"+id,HttpMethod.DELETE,null,Void.class);
			System.out.println("person deleted ");
		}catch (HttpStatusCodeException e) {
			if (HttpStatus.CONFLICT == e.getStatusCode()) {
				String serr = e.getResponseBodyAsString();
				ApiError ae=new ObjectMapper().readValue(serr,ApiError.class);
				throw new PersonCanNotBeDeletedException(ae.getDescription());

			} else {
				throw new Exception("some other error occurred");
			}
		}
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
