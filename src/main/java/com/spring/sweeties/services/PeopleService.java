package com.spring.sweeties.services;

import com.spring.sweeties.models.Person;
import com.spring.sweeties.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public Person findByUsername(String s) {
        Optional<Person> person = peopleRepository.findByEmail(s);

        return person.orElse(null);
    }

    @Transactional
    public void updatePerson(int id, Person updatedPerson) {
        updatedPerson.setId(id);

        peopleRepository.save(updatedPerson);
    }

    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);

        return foundPerson.orElse(null);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }

    @Transactional
    public void update(int id, Person updatedUser) {
        updatedUser.setId(id);
        peopleRepository.save(updatedUser);
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }
}
