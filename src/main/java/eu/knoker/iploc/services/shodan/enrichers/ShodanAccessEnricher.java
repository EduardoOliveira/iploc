package eu.knoker.iploc.services.shodan.enrichers;

import com.fooock.shodan.ShodanRestApi;
import com.fooock.shodan.model.host.Host;
import eu.knoker.iploc.entities.Access;
import eu.knoker.iploc.entities.EnrichmentStatus;
import eu.knoker.iploc.repositories.AccessRepository;
import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import retrofit2.HttpException;

@Component
@Slf4j
public class ShodanAccessEnricher {

    @Value("${shodan.apiKey}")
    private ShodanRestApi api;

    @Value("${shodan.refeshTimeout}")
    private long timeOut;

    @Autowired
    private AccessRepository repository;

    private Observable<Host> getIpInfo(String ip) {
        return api.hostByIp(ip);
    }

    public Mono<Access> enrich (Access access){
        if (access.getShodanData().getEnrichmentStatus().equals(EnrichmentStatus.ENRICHED) &&
                access.getShodanData().getLastUpdated() + timeOut > System.currentTimeMillis()) {
            log.info("Shodan: Skipping {}", access.getIp());
            return Mono.just(access);
        }

        log.info("Shodan: Enriching {}", access.getIp());
        return Mono.fromSupplier(() -> {
            try {
                Host host = getIpInfo(access.getIp()).blockingFirst();
                access.getShodanData().update(host);
                access.getShodanData().setEnrichmentStatus(EnrichmentStatus.ENRICHED);
                if (host.getLatitude() != 0 || host.getLongitude() != 0) {
                    access.setLatitude(host.getLatitude());
                    access.setLongitude(host.getLongitude());
                }
            } catch (Exception e) {
                if (((HttpException) e).code() == 404) {
                    log.info("Shodan: IP Not found {}", access.getIp());
                    access.getShodanData().setEnrichmentStatus(EnrichmentStatus.ENRICHMENT_MISSING);
                } else {
                    e.printStackTrace();
                    access.getShodanData().setEnrichmentStatus(EnrichmentStatus.ENRICHMENT_FAILED);
                }
                access.getShodanData().setLastUpdated(System.currentTimeMillis());
            }
            return access;
        }).flatMap(repository::updateShodan);
    }
}
