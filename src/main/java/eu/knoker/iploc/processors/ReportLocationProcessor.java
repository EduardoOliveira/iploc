package eu.knoker.iploc.processors;

import eu.knoker.iploc.services.aipdb.entities.Report;
import eu.knoker.iploc.repositories.LocationRepository;
import eu.knoker.iploc.services.nominatim.NominatimService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class ReportLocationProcessor {

    Sinks.Many<Report> sink = Sinks.unsafe().many().replay().latest();

    public ReportLocationProcessor(LocationRepository locationRepository,
                                   NominatimService nominatimService) {
        sink.asFlux()
                .onErrorResume(throwable -> {
                    throwable.printStackTrace();
                    return Flux.empty();
                })
                .filter(report -> report.getReporterCountryName() != null)
                .doOnNext(report -> log.info("Processing {}", report.getReporterCountryName()))
                .publishOn(Schedulers.newBoundedElastic(1, 1000, "report-location-processor"))
                .filterWhen(report -> locationRepository.existsByMatch(report.getReporterCountryName().toLowerCase()).map(Boolean.FALSE::equals))
                .doOnNext(report -> log.info("Fetching {}", report.getReporterCountryName()))
                .flatMap(report -> nominatimService.searchCountryByName(report.getReporterCountryName())
                        .flatMap(location -> {
                            location.getMatches().add(report.getReporterCountryName().toLowerCase());
                            location.getMatches().add(report.getReporterCountryCode().toLowerCase());
                            return locationRepository.save(location);
                        }),1)
                .subscribe();
    }


    public void publish(Report report) {
        sink.tryEmitNext(report);
    }
}
