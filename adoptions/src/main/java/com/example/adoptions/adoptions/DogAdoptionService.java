package com.example.adoptions.adoptions;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class DogController {
  private final DogAdoptionService dogAdoptionService;

  DogController(DogAdoptionService dogAdoptionService) {
    this.dogAdoptionService = dogAdoptionService;
  }

  @PostMapping("dogs/{dogId}/adoptions")
  void adopt(@PathVariable("dogId") int dogId, @RequestParam String owner) {
    this.dogAdoptionService.adoptDog(dogId, owner);
  }
}

@Service
@Transactional
class DogAdoptionService {

  private final DogAdoptionRepository dogAdoptionRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  DogAdoptionService(DogAdoptionRepository dogAdoptionRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.dogAdoptionRepository = dogAdoptionRepository;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  void adoptDog(int id, String owner) {
    this.dogAdoptionRepository
        .findById(id)
        .ifPresent(
            dog -> {
              var updated =
                  dogAdoptionRepository.save(
                      new Dog(dog.id(), dog.name(), owner, dog.description()));
              applicationEventPublisher.publishEvent(new DogAdoptionEvent(updated.id()));
              System.out.println("adopted [" + updated + "]");
            });
  }
}

interface DogAdoptionRepository extends ListCrudRepository<Dog, Integer> {}

record Dog(@Id int id, String name, String owner, String description) {}
