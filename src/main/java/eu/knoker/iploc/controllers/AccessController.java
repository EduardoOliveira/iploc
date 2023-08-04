package eu.knoker.iploc.controllers;

import eu.knoker.iploc.entities.Access;
import eu.knoker.iploc.repositories.AccessRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
//@RequestMapping("/accesses")
public class AccessController {
    private final AccessRepository repository;

    private final ApplicationEventPublisher publisher;
    public AccessController(AccessRepository repository, ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @GetMapping("/")
    String hello() {
        return "Hello World!";
    }

    @PostMapping("/accesses")
    Access newAccess(@RequestBody Access access) {
         Access a = Optional.ofNullable(repository.findByIp(access.getIp())).orElse(access);
         a = repository.save(a);
         repository.increment(a.getId());
         publisher.publishEvent(a);
         return a;
    }

    @GetMapping("/accesses/{id}")
    Access one(@PathVariable UUID id){
        return repository.findById(id).orElseThrow();
    }
}
