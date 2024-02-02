package be.abis.exercise.dto;

import be.abis.exercise.model.Person;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName="persons")
public class Persons {

	@JacksonXmlProperty(localName="person")
	@JacksonXmlElementWrapper(useWrapping = false)
	private  List<Person> persons;

	public  List<Person> getPersons() {
		return persons;
	}

	public  void setPersons(List<Person> persons) {
		this.persons = persons;
	}

	
	
	
}
