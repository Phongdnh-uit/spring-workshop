package com.example.pet_example;

import jakarta.persistence.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class PetExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetExampleApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(PersonRepository repository) {
        return args -> {
            repository.findAll().forEach(System.out::println);
        };
    }
}

@Entity
class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String own;
    private String description;

    @ManyToOne
    @JoinColumn(name = "person")
    private Person person;

    public Dog() {
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwn() {
        return own;
    }

    public void setOwn(String owner) {
        this.own = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner='" + own + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

@Entity
class Person {

    @Id
    private int id;
    private String name;

    @OneToMany(mappedBy = "person", fetch = FetchType.EAGER)
    private List<Dog> dogs = new ArrayList<>();

    public Person() {
    }

    public List<Dog> getDogs() {
        return dogs;
    }

    public void setDogs(List<Dog> dogs) {
        this.dogs = dogs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dogs=" + dogs +
                '}';
    }
}

@Repository
interface PersonRepository extends JpaRepository<Person, Integer> {
}