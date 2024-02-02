package be.abis.exercise.controller;

import be.abis.exercise.dto.Login;
import be.abis.exercise.dto.Password;
import be.abis.exercise.dto.Persons;
import be.abis.exercise.exception.*;
import be.abis.exercise.model.Person;
import be.abis.exercise.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            return new ResponseEntity<Object>(p, HttpStatus.OK);
        } catch (PersonNotFoundException pnfe) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            ApiError err = new ApiError("person not found", status.value(), pnfe.getMessage());
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            return new ResponseEntity<Object>(err, responseHeaders, status);
        }

    }

    @GetMapping(path = "/query", produces = MediaType.APPLICATION_XML_VALUE)
    public Persons findPersonsByCompanyName(@RequestParam("compname") String compName) {
        Persons persons = new Persons();
        persons.setPersons(ps.findPersonsByCompanyName(compName));
        return persons;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> findPersonByMailAndPwd(@RequestBody Login login) {

        try {
            Person p = ps.findPerson(login.getEmail(), login.getPassword());
            Map<Integer, String> allkeys = ps.getKeys();
            System.out.println("allkeys:" + allkeys);
            String key = allkeys.get(p.getPersonId());
            System.out.println("key=" + key);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("api-key", key);
            return new ResponseEntity<Object>(p, responseHeaders, HttpStatus.OK);

        } catch (PersonNotFoundException pnfe) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            ApiError err = new ApiError("person not found", status.value(), pnfe.getMessage());
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("content-type", MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            return new ResponseEntity<Object>(err, responseHeaders, status);
        }
    }

    @PostMapping(path = "", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public void addPerson(@Valid @RequestBody Person p) throws PersonAlreadyExistsException {
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
    public void changePasswordviaPerson(@PathVariable("id") int id, @Valid @RequestBody Person person, @RequestHeader MultiValueMap<String, String> headers) throws PersonNotFoundException, ApiKeyNotCorrectException {
        Person p = ps.findPerson(id);
        boolean ok = checkApiKey(headers, p);
        if (ok) {
            try {
                System.out.println("changing password to newpswd= " + person.getPassword());
                ps.changePassword(person, person.getPassword());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("wrong key");
            throw new ApiKeyNotCorrectException("please provide a valid API KEY");
        }
    }


    @PatchMapping("{id}/password")
    public void patchPassword(@PathVariable("id") int id, @Valid @RequestBody Password password, @RequestHeader MultiValueMap<String, String> headers) throws PersonNotFoundException, ApiKeyNotCorrectException {
        Person p = ps.findPerson(id);
        boolean ok = checkApiKey(headers, p);
        if (ok) {
            try {
                System.out.println("changing password to newpswd= " + password.getPassword());
                ps.changePassword(p, password.getPassword());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("wrong key");
            throw new ApiKeyNotCorrectException("please provide a valid API KEY");
        }
    }

    public boolean checkApiKey(MultiValueMap<String, String> headers, Person p) {
        boolean ok = false;
        if (headers.containsKey("api-key")) {
            String auth = headers.get("api-key").get(0);
            System.out.println("key passed: " + auth);

            String key = ps.getKeys().get(p.getPersonId());
            System.out.println("key of person : " + key);

            if (key.equals(auth)) {
                ok = true;
            }
        }
        return ok;
    }

}