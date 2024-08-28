package org.example.marketplaceservice.mappers;

import org.example.marketplaceservice.dto.JWTDTO;
import org.example.marketplaceservice.dto.PersonDTO;
import org.example.marketplaceservice.models.Person;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    private final ModelMapper mapper;

    @Autowired
    public PersonMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public PersonDTO convertToPersonDTO(Person person) {
       return mapper.map(person, PersonDTO.class);
    }

    public Person convertToPerson(PersonDTO personDTO) {
        return mapper.map(personDTO, Person.class);
    }

    public JWTDTO convertToJWTDTO(Person person) { return mapper.map(person, JWTDTO.class); }
}
