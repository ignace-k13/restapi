package be.abis.exercise;


import be.abis.exercise.model.Person;
import be.abis.exercise.service.PersonService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonServiceTest {

    @Autowired
    private PersonService ps;

    @Test
    @Order(1)
    public void testGetAllPersons() {
        List<Person> persons = ps.getAllPersons();
        persons.forEach(System.out::println);

        assertEquals(3,persons.size());

    }

    @Test
    @Order(2)
    public void testGetPersonById() {
        Person p = ps.findPerson(2);
        System.out.println(p);
        assertEquals("Mary",p.getFirstName());
    }

    @Test
    @Order(3)
    public void testGetPersonByEmailandPassword() {
        Person p = ps.findPerson("jdoe@abis.be", "def456");
        System.out.println(p);
        assertEquals("John",p.getFirstName());
    }

    @Test
    @Order(4)
    public void testFindPersonsByCompanyName() {
        List<Person> persons = ps.findPersonsByCompanyName("abis");
        persons.forEach(System.out::println);
        assertEquals(2,persons.size());
    }

    @Test
    @Order(5)
    public void testAddPerson() {
        Person p = ps.findPerson(2);
        Person newPers = new Person(5,"Sandy","Schillebeeckx", LocalDate.of(1978, 04,10)
                                                           ,"sschillebeeckx@abis.be","abis01","nl",p.getCompany());
        ps.addPerson(newPers);
        assertEquals(4,ps.getAllPersons().size());
    }

    @Test
    @Order(6)
    public void testChangePasswordCompletePerson() {
        Person p = ps.findPerson(5);
        p.setPassword("xxxxx");
        ps.changePassword(p);
        assertEquals("xxxxx",ps.findPerson(5).getPassword());
    }

    @Test
    @Order(7)
    public void testChangePasswordViaPatch() {
        Person p = ps.findPerson(5);
        ps.changePassword(p.getPersonId(),"changeViaPatch");
        assertEquals("changeViaPatch",ps.findPerson(5).getPassword());
    }

    @Test
    @Order(8)
    public void testDeletePerson() {
        int id=5;
        ps.deletePerson(id);
        assertNull(ps.findPerson(id));
    }



}
