package eu.knoker.iploc.services.aipdb.enrichers;

import eu.knoker.iploc.entities.Access;
import eu.knoker.iploc.entities.EnrichmentStatus;
import eu.knoker.iploc.processors.ReportLocationProcessor;
import eu.knoker.iploc.repositories.AccessRepository;
import eu.knoker.iploc.services.aipdb.entities.AbuseIPDBData;
import eu.knoker.iploc.services.aipdb.entities.Report;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.HashMap;

@Component
@Slf4j
public class AccessEnricher {

    @Value("${abuseipdb.apiKey}")
    private String apiKey;

    @Value("${abuseipdb.refeshTimeout}")
    private long timeOut;

    @Autowired
    private AccessRepository repository;

    @Autowired
    private ReportLocationProcessor reportLocationProcessor;

    private final String baseUrl = "https://api.abuseipdb.com/api/v2/check";

    private WebClient client;

    @PostConstruct
    public void init() {
        client = WebClient.builder()
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.add("Key", apiKey);
                }).codecs(codecs -> codecs
                        .defaultCodecs()
                        .maxInMemorySize(10000 * 1024))
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().followRedirect(true)
                        //.wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
                )).build();
    }

    public Mono<Access> enrich(Access access) {
        if (access.getAbuseIPDBData().getEnrichmentStatus().equals(EnrichmentStatus.ENRICHED) &&
                access.getAbuseIPDBData().getLastUpdated() + timeOut > System.currentTimeMillis()) {
            log.info("AbuseIPDB: Skipping {}", access.getIp());
            return Mono.just(access);
        }
        log.info("AbuseIPDB: Enriching {}", access.getIp());

        String url = baseUrl + "?verbose&maxAgeInDays=90&ipAddress=" + access.getIp();

        return client.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<HashMap<String, AbuseIPDBData>>() {
                })
                .map(response -> {
                    AbuseIPDBData data = response.get("data");
                    data.setLastUpdated(System.currentTimeMillis());
                    data.setIp(access.getIp());
                    data.getReports().stream().map(Report::getReporterCountryCode).forEach(c -> data.getReportedIn().add(c));
                    data.setEnrichmentStatus(EnrichmentStatus.ENRICHED);
                    access.setAbuseIPDBData(data);
                    return access;
                })
                .doOnNext(a -> {
                    a.getAbuseIPDBData().getReports()
                            .forEach(reportLocationProcessor::publish);
                })
                .flatMap(repository::updateAbuseIPDB);
    }
}
