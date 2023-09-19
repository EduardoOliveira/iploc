package eu.knoker.iploc.controllers;

import eu.knoker.iploc.services.nominatim.entities.Location;
import eu.knoker.iploc.repositories.LocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Set;

@RestController
@AllArgsConstructor
public class LocationController {
    private final LocationRepository repository;

    @GetMapping("/locations")
    Flux<Location> all(
            @RequestParam(value = "countries", required = false) String countries
    ) {
        if (countries != null) {
            countries = countries.toLowerCase();
            return repository.findByMatch(Set.of(countries.split(",")).stream().toList());
        }
        return repository.findAll();
    }


}
