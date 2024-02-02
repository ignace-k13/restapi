package be.abis.exercise;

import be.abis.exercise.exception.*;
import be.abis.exercise.model.Person;
import be.abis.exercise.service.PersonService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class PersonServiceTest {

    @Autowired
    private PersonService ps;

    @Test
    public void testGetAllPersonsUnauthorized() {
        List<Person> persons = null;
        try {
            persons = ps.getAllPersons();
        } catch (Exception e) {
            assertEquals("GET OUT!, you shouldn't be here",e.getMessage());
        }

    }

    @Test
    public void testAuthenticationFails() throws Exception {
        ps.provideAuthentication("abis02", "wrong");
        assertThrows(AuthenticationException.class ,()->ps.findPerson(2));
    }


    @Test
    public void testGetAllPersons() throws Exception {
        ps.provideAuthentication("abis01", "abis01");
        List<Person> persons = ps.getAllPersons();
        persons.forEach(System.out::println);
        assertEquals(3,persons.size());
    }

    @Test
    public void testGetPersonById() throws Exception {
        ps.provideAuthentication("abis01", "abis01");
        Person p = ps.findPerson(2);
        System.out.println(p);
        assertEquals("Mary",p.getFirstName());
    }

    @Test
    public void testGetPersonById9NotFound() throws Exception {
        ps.provideAuthentication("abis02", "abis02");
       assertThrows(PersonNotFoundException.class,()->ps.findPerson(9));
    }


    @Test
    public void testGetPersonByEmailandPassword() throws Exception {
        Person p = ps.findPerson("jdoe@abis.be", "def456");
        System.out.println(p);
        assertEquals("John",p.getFirstName());
    }

    @Test
    public void testWrongEmailandPassword() throws Exception {
        assertThrows(PersonNotFoundException.class,()->ps.findPerson("jdoe@abis.be", "defxxx"));
    }


    @Test
    public void testFindPersonsByCompanyName() {
        ps.provideAuthentication("abis01", "abis01");
        List<Person> persons = ps.findPersonsByCompanyName("abis");
        persons.forEach(System.out::println);
        assertEquals(2,persons.size());
    }

    @Test
    public void testAddPersonPwdTooShort() throws Exception {
        ps.provideAuthentication("abis02", "abis02");
        Person p = ps.findPerson(2);
        Person newPers = new Person(5,"Sandy","Schillebeeckx", LocalDate.of(1978, 04,10)
                ,"sschillebeeckx@abis.be","abis","nl",p.getCompany());
        assertThrows(PasswordTooShortException.class,()->ps.addPerson(newPers));

    }

    @Test
    public void testAddPerson() throws Exception {
        ps.provideAuthentication("abis01", "abis01");
        Person p = ps.findPerson(2);
        Person newPers = new Person(5,"Sandy","Schillebeeckx", LocalDate.of(1978, 04,10)
                                                           ,"sschillebeeckx@abis.be","abis01","nl",p.getCompany());
        ps.addPerson(newPers);
        assertEquals(4,ps.getAllPersons().size());
        ps.deletePerson(5);
    }


    @Test
    public void testAddPersonThatAlreadyExists() throws Exception {
        ps.provideAuthentication("abis01", "abis01");
        Person p = ps.findPerson(2);
        Person newPers = new Person(6,"Sam","Schillebeeckx", LocalDate.of(1995, 03,15)
                ,"jdoe@abis.be","abis01","nl",p.getCompany());
        assertThrows(PersonAlreadyExistsException.class,()->ps.addPerson(newPers));
    }


    @Test
    public void testChangePasswordTooShortCompletePerson() throws Exception {
        ps.provideAuthentication("abis01", "abis01");
        Person p = ps.findPerson(2);
        p.setPassword("xxxx");
        assertThrows(PasswordTooShortException.class,()->ps.changePassword(p));
    }

    @Test
    public void testChangePasswordCompletePerson() throws Exception {
        Person p = ps.findPerson("jdoe@abis.be", "def456");
        p.setPassword("xxxxx");
        ps.changePassword(p);
        ps.provideAuthentication("abis01", "abis01");
        assertEquals("xxxxx",ps.findPerson(1).getPassword());
        p.setPassword("def456");
        ps.changePassword(p);
    }

    @Test
    public void testChangePasswordTooShortViaPatch() throws Exception {
        ps.provideAuthentication("abis02", "abis02");
        Person p = ps.findPerson(2);
        assertThrows(PasswordTooShortException.class,()->ps.changePassword(p.getPersonId(),"bla2"));
    }

    @Test
    public void testChangePasswordViaPatch() throws Exception {
        Person p = ps.findPerson("jdoe@abis.be", "def456");
        ps.changePassword(p.getPersonId(),"changeViaPatch");
        ps.provideAuthentication("abis02", "abis02");
        assertEquals("changeViaPatch",ps.findPerson(1).getPassword());
        ps.changePassword(p.getPersonId(),"def456");
    }

    @Test
    public void testDeletePerson() throws Exception {
        ps.provideAuthentication("abis01", "abis01");
        Person p = ps.findPerson(2);
        Person newPers = new Person(5,"Sandy","Schillebeeckx", LocalDate.of(1978, 04,10)
                ,"sschillebeeckx@abis.be","abis01","nl",p.getCompany());
        ps.addPerson(newPers);
        ps.deletePerson(5);
        assertThrows(PersonNotFoundException.class,()->ps.findPerson(5));
    }

    @Test
    public void testDeleteNonExistingPersonThrowsException() throws Exception {
        ps.provideAuthentication("abis02", "abis02");
        assertThrows(PersonCanNotBeDeletedException.class,()->ps.deletePerson(6));
    }

    @Test
    public void testAuthenticationOk() throws Exception {
        ps.provideAuthentication("abis01", "abis01");
        Person p = ps.findPerson(2);
        Person newPers = new Person(5,"Sandy","Schillebeeckx", LocalDate.of(1978, 04,10)
                ,"sschillebeeckx@abis.be","abis01","nl",p.getCompany());
        ps.addPerson(newPers);
        Person loggedInPerson = ps.findPerson("sschillebeeckx@abis.be", "abis01");
        loggedInPerson.setPassword("newpwd");
        ps.changePassword(loggedInPerson);
        Person afterChange = ps.findPerson(5);
        assertEquals("newpwd",afterChange.getPassword());
        ps.deletePerson(5);
    }




}
