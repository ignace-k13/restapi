package be.abis.exercise.service;

import be.abis.exercise.exception.*;
import be.abis.exercise.model.Login;
import be.abis.exercise.model.Password;
import be.abis.exercise.model.Person;
import be.abis.exercise.model.Persons;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
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
	private static String apiKey=null;

	public void provideAuthentication(String username, String password){
		rt.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
	}

	@Override
	public List<Person> getAllPersons() throws Exception {
		ResponseEntity<? extends Object> re = null;
		List<Person> persons=null;
		try {
			re =  rt.exchange(baseUri,HttpMethod.GET,null,new ParameterizedTypeReference<List<Person>>(){});
			persons = (List<Person>) re.getBody();
		} catch (HttpStatusCodeException e) {
			if (HttpStatus.UNAUTHORIZED == e.getStatusCode()) {
				throw new AuthenticationException("GET OUT!, you shouldn't be here");
			} else {
				throw new Exception("some other error occurred");
			}
		}

		return persons;
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

			} else if (HttpStatus.UNAUTHORIZED == e.getStatusCode()) {
				throw new AuthenticationException("GET OUT!, you shouldn't be here");
			}
			else {
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
			HttpHeaders headers = re.getHeaders();
			apiKey = headers.get("api-key").get(0);
			System.out.println("key set to:" + apiKey);
		}catch (HttpStatusCodeException e) {
			if (HttpStatus.NOT_FOUND == e.getStatusCode()) {
				String serr = e.getResponseBodyAsString();
				ApiError ae=new ObjectMapper().readValue(serr,ApiError.class);
				throw new PersonNotFoundException(ae.getDescription());
			} else if (HttpStatus.UNAUTHORIZED == e.getStatusCode()) {
				throw new AuthenticationException("GET OUT!, you shouldn't be here");
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
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.add("accept", "application/json");
			HttpEntity<Person> requestEntity = new HttpEntity<>(p, requestHeaders);
			re = rt.exchange(baseUri, HttpMethod.POST, requestEntity, Void.class);
			System.out.println("person added ");
		} catch (HttpStatusCodeException e) {
			String serr = e.getResponseBodyAsString();
			ApiError ae = new ObjectMapper().readValue(serr, ApiError.class);
			if (HttpStatus.BAD_REQUEST == e.getStatusCode()) {
				List<ValidationError> ve = ae.getInvalidParams();
				if (ve.size() != 0) {
					//System.out.println("validation errors:");
					//ve.stream().map(v -> v.getReason()).forEach(System.out::println);
					ValidationError validationError =ve.stream().filter(v->v.getName().equals("password")).findFirst().orElse(null);
					if(validationError!=null){
						throw new PasswordTooShortException(validationError.getReason());
					}
				}
			} else if (HttpStatus.CONFLICT == e.getStatusCode()) {
				throw new PersonAlreadyExistsException(ae.getDescription());
			} else if (HttpStatus.UNAUTHORIZED == e.getStatusCode()) {
				throw new AuthenticationException("GET OUT!, you shouldn't be here");
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
			} else if (HttpStatus.UNAUTHORIZED == e.getStatusCode()) {
				throw new AuthenticationException("GET OUT!, you shouldn't be here");
			} else if (HttpStatus.FORBIDDEN == e.getStatusCode()) {
				throw new AuthorizationException("You do not have the correct rights");
			} else {
				throw new Exception("some other error occurred");
			}
		}
	}

	@Override
	public void changePassword(Person p) throws Exception {
		ResponseEntity<? extends Object> re = null;
		try {
			HttpHeaders requestHeaders = new HttpHeaders();
			System.out.println("fetching key: " + apiKey);
			requestHeaders.add("api-key", apiKey);
			requestHeaders.add("accept", "application/json");
			HttpEntity<Person> requestEntity = new HttpEntity<>(p, requestHeaders);
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
			HttpHeaders requestHeaders = new HttpHeaders();
			System.out.println("fetching key: " + apiKey);
			requestHeaders.add("api-key", apiKey);
			requestHeaders.add("accept", "application/json");
			HttpEntity<Password> requestEntity = new HttpEntity<>(password,requestHeaders);
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
		} else if(HttpStatus.UNAUTHORIZED == e.getStatusCode()){
			throw new AuthenticationException("you cannot change the pwd of another person");
		}  else {
			throw new Exception("some other error occurred");
		}
	}


}
