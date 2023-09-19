package eu.knoker.iploc.eventbridges;

import eu.knoker.iploc.actions.EventNotification;
import eu.knoker.iploc.actions.aipdb.AbuseIPDB;
import eu.knoker.iploc.actions.nominatim.Nominatim;
import eu.knoker.iploc.actions.shodan.Shodan;
import eu.knoker.iploc.entities.Access;
import eu.knoker.iploc.repositories.AccessRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Service
public class NewAccessEventBridge {

    Sinks.Many<Access> sink = Sinks.unsafe().many().replay().latest();


    public NewAccessEventBridge(AccessRepository accessRepository,
                                Shodan shodan,
                                AbuseIPDB abuseIPDB,
                                Nominatim nominatim,
                                Sinks.Many<EventNotification> eventChannel
    ) {
        Scheduler shodanScheduler = Schedulers.newBoundedElastic(1, 1000, "shodan");
        Scheduler abuseIPDBScheduler = Schedulers.newBoundedElastic(1, 1000, "aipdb");
        Scheduler nominatimScheduler = Schedulers.newBoundedElastic(1, 1000, "nominatim");
        sink.asFlux()
                .flatMap(accessRepository::upSert)
                .flatMap(access -> Flux.zip(
                                        shodan.enrich(access).subscribeOn(shodanScheduler),
                                        abuseIPDB.enrich(access).subscribeOn(abuseIPDBScheduler)
                                )
                                .flatMap(t -> accessRepository.findByIp(t.getT1().getIp()))
                )
                .flatMap(access -> nominatim.enrich(access).subscribeOn(nominatimScheduler))
                .flatMap(accessRepository::save)
                .doOnNext(access -> eventChannel.tryEmitNext(EventNotification.builder()
                        .type(EventNotification.CHANGE_ACCESS_EVENT)
                        .access(access)
                        .build()).orThrow())
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }

    public void publish(Access access) {
        sink.tryEmitNext(access);
    }


}
