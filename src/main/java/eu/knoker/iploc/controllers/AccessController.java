package eu.knoker.iploc.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import eu.knoker.iploc.actions.EventNotification;
import eu.knoker.iploc.entities.Access;
import eu.knoker.iploc.services.aipdb.entities.Report;
import eu.knoker.iploc.services.shodan.entities.Banner;
import eu.knoker.iploc.processors.NewAccessProcessor;
import eu.knoker.iploc.repositories.AccessRepository;
import eu.knoker.iploc.repositories.LocationRepository;
import eu.knoker.iploc.services.nominatim.entities.Location;
import eu.knoker.iploc.services.nominatim.jsonviews.LocationViews;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
public class AccessController {
    private final AccessRepository repository;
    private final LocationRepository locationRepository;

    private final Flux<EventNotification> eventNotifications;
    private final NewAccessProcessor newAccessProcessor;

    @PostMapping("/accesses")
    @ResponseStatus(value = HttpStatus.OK)
    void newAccess(@RequestBody Access access) {
        log.info("New access: {}", access.getIp());
        newAccessProcessor.publish(access);
    }

    @GetMapping("/accesses")
    Flux<Access> all() {
        return repository.findAll();
    }

    @GetMapping("/accesses/{id}")
    Mono<Access> one(@PathVariable String id) {
        return repository.findById(id).switchIfEmpty(Mono.error(new Exception("Not found"))).map(access -> {
            access.getShodanData().getBanners().clear();
            access.getShodanData().getVulnerabilities().clear();
            access.getAbuseIPDBData().getReports().clear();
            return access;
        });
    }

    @GetMapping("/accesses/{id}/banners")
    Mono<Map<String, Object>> banners(@PathVariable String id,
                         @RequestParam("page") int page,
                         @RequestParam("size") int size) {
        return repository.findById(id).switchIfEmpty(Mono.error(new Exception("Not found")))
                .map(access -> {
                    List<Banner> banners = access.getShodanData().getBanners();
                    return pageList(banners, page, size);
                }).map(banners -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("banners", banners.getValue1());
                    map.put("total", banners.getValue1().size());
                    map.put("pages", banners.getValue0());
                    map.put("page", page);
                    map.put("size", size);
                    return map;
                });
    }

    @GetMapping("/accesses/{id}/vulnerabilities")
    Mono<Map<String, Object>> vulnerabilities(@PathVariable String id,
                                       @RequestParam("page") int page,
                                       @RequestParam("size") int size) {
        return repository.findById(id).switchIfEmpty(Mono.error(new Exception("Not found")))
                .map(access -> {
                    List<String> vulnerabilities = access.getShodanData().getVulnerabilities();
                    return pageList(vulnerabilities, page, size);
                }).map(vulnerabilities -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("vulnerabilities", vulnerabilities.getValue1());
                    map.put("total", vulnerabilities.getValue1().size());
                    map.put("pages", vulnerabilities.getValue0());
                    map.put("page", page);
                    map.put("size", size);
                    return map;
                });

    }

    @GetMapping("/accesses/{id}/reports")
    Mono<Map<String, Object>> reports(@PathVariable String id,
                                      @RequestParam("page") int page,
                                      @RequestParam("size") int size) {
        return repository.findById(id).switchIfEmpty(Mono.error(new Exception("Not found")))
                .map(access -> {
                    List<Report> reports = access.getAbuseIPDBData().getReports();
                    return pageList(reports, page, size);
                })
                .map(abuseIPDBReports -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("reports", abuseIPDBReports.getValue1());
                    map.put("total", abuseIPDBReports.getValue1().size());
                    map.put("pages", abuseIPDBReports.getValue0());
                    map.put("page", page);
                    map.put("size", size);
                    return map;
                });
    }

    @JsonView(LocationViews.Full.class)
    @GetMapping("/accesses/{id}/reports/locations")
    Flux<Location> reports(@PathVariable String id) {
        return repository.findById(id).switchIfEmpty(Mono.error(new Exception("Not found")))
                .map(access -> access.getAbuseIPDBData()
                        .getReports().stream()
                        .map(Report::getReporterCountryName)
                        .map(String::toLowerCase)
                        .toList())
                .map(locationRepository::findByMatch)
                .flatMapMany(Flux::from);
    }

    @GetMapping(value = "/accesses/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Access> stream() {
        log.info("Stream");
        return Flux.concat(
                repository.findAll(),
                eventNotifications.filter(eventNotification -> eventNotification.getType().equals(EventNotification.CHANGE_ACCESS_EVENT))
                        .map(EventNotification::getAccess)
        ).map(access -> {
            access.getShodanData().getBanners().clear();
            access.getShodanData().getVulnerabilities().clear();
            return access;
        });
    }

    @NotNull
    @Contract("_, _, _ -> new")
    private Pair<Integer, List<?>> pageList(@NotNull List<?> list, int page, int size) {
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, list.size());
        if (fromIndex > list.size()) {
            return new Pair<>(0, list);
        }
        return new Pair<>((list.size() / size)+1, list.subList(fromIndex, toIndex));
    }
}
