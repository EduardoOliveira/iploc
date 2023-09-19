package eu.knoker.iploc.repositories;

import eu.knoker.iploc.entities.Access;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;


interface AccessRepositoryInterface extends ReactiveMongoRepository<Access, String> {
    Mono<Access> findByIp(String ip);
}

@Component
@Slf4j
@RequiredArgsConstructor
public class AccessRepository {

    private final AccessRepositoryInterface accessRepositoryInterface;

    private final ReactiveMongoTemplate template;

    public Mono<Access> findByIp(String ip) {
        return accessRepositoryInterface.findByIp(ip);
    }

    public Mono<Access> save(Access access) {
        return accessRepositoryInterface.save(access);
    }

    public Mono<Access> upSert(Access access) {
        return template.upsert(query(where("ip").is(access.getIp())), new Update()
                        .inc("accessCount", 1)
                        .set("lastSeen", System.currentTimeMillis()), Access.class)
                .flatMap(a -> findByIp(access.getIp()));
    }

    public Flux<Access> findAll() {
        return accessRepositoryInterface.findAll();
    }

    public Mono<Access> findById(String id) {
        return accessRepositoryInterface.findById(id);
    }

    public Mono<Access> updateShodan(Access access) {
        return template.findAndModify(query(where("id").is(access.getId())), new Update()
                        .set("latitude", access.getLatitude())
                        .set("longitude", access.getLongitude())
                        .set("shodanData", access.getShodanData()), Access.class);
    }

    public Mono<Access> updateAbuseIPDB(Access access) {
        log.info("AbuseIPDB: Updating {}", access);
        return template.findAndModify(query(where("id").is(access.getId())), new Update()
                        .set("abuseIPDBData", access.getAbuseIPDBData()), Access.class);
    }



    public Mono<Access> incrementAccessCount(Access access) {
        return template.findAndModify(query(where("id").is(access.getId())), new Update()
                        .inc("accessCount", 1)
                        .set("lastSeen",System.currentTimeMillis()), Access.class);
    }

}