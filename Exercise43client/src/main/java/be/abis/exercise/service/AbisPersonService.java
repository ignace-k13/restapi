package be.abis.exercise.service;

import be.abis.exercise.exception.*;
import be.abis.exercise.model.Login;
import be.abis.exercise.model.Password;
import be.abis.exercise.model.Person;
import be.abis.exercise.model.Persons;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
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
	public void addPerson(Person p) throws Exception {
		ResponseEntity<? extends Object> re = null;
		try {
			re = rt.postForEntity(baseUri, p, Void.class);
			System.out.println("person added ");
		} catch (HttpStatusCodeException e) {
			String serr = e.getResponseBodyAsString();
			ApiError ae = new ObjectMapper().readValue(serr, ApiError.class);
			if (HttpStatus.BAD_REQUEST == e.getStatusCode()) {
				List<ValidationError> validationErrors = ae.getInvalidParams();
				if (validationErrors.size() != 0) {
					//System.out.println("validation errors:");
					//ve.stream().map(v -> v.getReason()).forEach(System.out::println);
					ValidationError validationError =validationErrors.stream().filter(v->v.getName().equals("password")).findFirst().orElse(null);
					if(validationError!=null){
						//System.out.println(validationError.getReason());
						throw new PasswordTooShortException(validationError.getReason());
					}
					/*for (ValidationError verr:ve){
						throw new ValidationException(verr.getReason());
					}*/
				}
			} else if (HttpStatus.CONFLICT == e.getStatusCode()) {
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
	public void changePassword(Person p) throws Exception {
		ResponseEntity<? extends Object> re = null;
		try {
			HttpEntity<Person> requestEntity = new HttpEntity<>(p);
			re = rt.exchange(baseUri + "/" + p.getPersonId(), HttpMethod.PUT, requestEntity, Void.class);
			//re = rt.postForEntity(baseUri + "/" + p.getPersonId() + "/changepwd", pw, Void.class);
			System.out.println("password changed ");
		} catch (HttpStatusCodeException e) {
			this.handlePasswordException(e);
		}
	}

	@Override
	public void changePassword(int id,String newpwd) throws Exception {
		Password password=new Password(newpwd);
	    ResponseEntity<? extends Object> re = null;
		try {
			HttpEntity<Password> requestEntity = new HttpEntity<>(password);
			re = rt.exchange(baseUri+"/"+id +"/password", HttpMethod.PATCH, requestEntity, Void.class);
			System.out.println("password changed ");
		} catch (HttpStatusCodeException e) {
			this.handlePasswordException(e);
		}
	}

	public void handlePasswordException(HttpStatusCodeException e) throws Exception{
		String serr = e.getResponseBodyAsString();
		ApiError ae = new ObjectMapper().readValue(serr, ApiError.class);
		if (HttpStatus.BAD_REQUEST == e.getStatusCode()) {
			List<ValidationError> ve = ae.getInvalidParams();
			ValidationError validationError =ve.stream().filter(v->v.getName().equals("password")).findFirst().orElse(null);
			if(validationError!=null){
				throw new PasswordTooShortException(validationError.getReason());
			}
		}  else {
			throw new Exception("some other error occurred");
		}
	}


}
