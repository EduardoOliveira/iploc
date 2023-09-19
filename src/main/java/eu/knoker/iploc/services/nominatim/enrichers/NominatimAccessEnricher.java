package eu.knoker.iploc.services.nominatim.enrichers;

import eu.knoker.iploc.entities.Access;
import eu.knoker.iploc.entities.EnrichmentStatus;
import eu.knoker.iploc.repositories.LocationRepository;
import eu.knoker.iploc.services.nominatim.NominatimService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@Slf4j
public class AccessEnricher {

    @Autowired
    private LocationRepository repository;

    @Autowired
    private NominatimService service;

    public Mono<Access> enrich(Access access) {
        if (shouldEnrich(access)) {
            log.info("Nominatim: Enriching {}", access.getIp());

            return repository.getOneByMatch(access.getAbuseIPDBData().getCountryName())
                    .switchIfEmpty(service.searchCountryByName(access.getAbuseIPDBData().getCountryName()))
                    .flatMap(location -> {
                        location.getMatches().add(access.getAbuseIPDBData().getCountryCode().toLowerCase());
                        location.getMatches().add(access.getAbuseIPDBData().getCountryName().toLowerCase());
                        return this.repository.save(location);
                    })
                    .doOnNext(location -> {
                        access.setLatitude(location.getLat());
                        access.setLongitude(location.getLon());
                    })
                    .onErrorContinue((throwable, o) -> throwable.printStackTrace())
                    .map(location -> access);
        }
        return Mono.just(access);
    }


    private boolean shouldEnrich(Access access) {
        return access.getLatitude() == 0L && access.getLongitude() == 0L
                && (
                access.getShodan().getEnrichmentStatus() == EnrichmentStatus.ENRICHMENT_FAILED
                        || access.getShodan().getEnrichmentStatus() == EnrichmentStatus.ENRICHMENT_MISSING
        )
                && access.getAbuseIPDBData().getEnrichmentStatus() == EnrichmentStatus.ENRICHED
                && !Objects.equals(access.getAbuseIPDBData().getCountryCode(), "");
    }

}
