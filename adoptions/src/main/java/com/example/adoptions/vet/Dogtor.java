package com.example.adoptions.vet;

import com.example.adoptions.adoptions.DogAdoptionEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
public class Dogtor {

    @ApplicationModuleListener
    public void schedule(DogAdoptionEvent dogAdoptionEvent) {
        // Simulate a long-running task
        try {
            Thread.sleep(5000);
            System.out.println("Scheduled " + dogAdoptionEvent.dogId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}