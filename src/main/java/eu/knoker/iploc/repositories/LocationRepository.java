package eu.knoker.iploc.repositories;

import eu.knoker.iploc.services.nominatim.entities.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

interface LocationRepositoryInterface extends ReactiveMongoRepository<Location, String> {
}

@Component
@RequiredArgsConstructor
public class LocationRepository {
    private final LocationRepositoryInterface locationRepositoryInterface;

    private final ReactiveMongoTemplate template;

    public Mono<Location> save(Location location) {
        return locationRepositoryInterface.save(location);
    }


    public Flux<Location> findByMatch(List<String> matches) {
        Query query = new Query();
        query.addCriteria(Criteria.where("matches").in(matches));
        return template.find(query, Location.class);
    }

    public Mono<Location> getOneByMatch(String match) {
        Query query = new Query();
        query.addCriteria(Criteria.where("matches").in(match));
        return template.findOne(query, Location.class);
    }

    public Mono<Boolean> existsByMatch(String match) {
        Query query = new Query();
        query.addCriteria(Criteria.where("matches").in(match));
        return template.exists(query, Location.class);
    }

    public Flux<Location> findAll() {
        return locationRepositoryInterface.findAll();
    }
}
