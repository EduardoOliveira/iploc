package eu.knoker.iploc.processors;

import eu.knoker.iploc.actions.EventNotification;
import eu.knoker.iploc.entities.Access;
import eu.knoker.iploc.repositories.AccessRepository;
import eu.knoker.iploc.services.aipdb.enrichers.AIPDBAccessEnricher;
import eu.knoker.iploc.services.nominatim.enrichers.NominatimAccessEnricher;
import eu.knoker.iploc.services.shodan.enrichers.ShodanAccessEnricher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Service
public class NewAccessProcessor {

    Sinks.Many<Access> sink = Sinks.unsafe().many().replay().latest();


    public NewAccessProcessor(AccessRepository accessRepository,
                              ShodanAccessEnricher shodanEnricher,
                              AIPDBAccessEnricher abuseIPDBEnricher,
                              NominatimAccessEnricher nominatimEnricher,
                              Sinks.Many<EventNotification> eventChannel
    ) {
        Scheduler shodanScheduler = Schedulers.newBoundedElastic(1, 1000, "shodan");
        Scheduler abuseIPDBScheduler = Schedulers.newBoundedElastic(1, 1000, "aipdb");
        Scheduler nominatimScheduler = Schedulers.newBoundedElastic(1, 1000, "nominatim");
        sink.asFlux().onErrorResume(throwable -> {
                    throwable.printStackTrace();
                    return Flux.empty();
                })
                .flatMap(accessRepository::upSert)
                .flatMap(access -> Flux.zip(
                                        shodanEnricher.enrich(access).subscribeOn(shodanScheduler),
                                        abuseIPDBEnricher.enrich(access).subscribeOn(abuseIPDBScheduler)
                                ).onErrorResume(throwable -> {
                                    throwable.printStackTrace();
                                    return Flux.empty();
                                })
                                .flatMap(t -> accessRepository.findByIp(t.getT1().getIp()))
                )
                .flatMap(access -> nominatimEnricher.enrich(access).subscribeOn(nominatimScheduler))
                .flatMap(accessRepository::save)
                .doOnNext(access -> eventChannel.tryEmitNext(EventNotification.builder()
                        .type(EventNotification.CHANGE_ACCESS_EVENT)
                        .access(access)
                        .build()).orThrow())
                .subscribe();
    }

    public void publish(Access access) {
        sink.tryEmitNext(access);
    }


}
