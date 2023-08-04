package eu.knoker.iploc.enrichers.abuseipdb;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.knoker.iploc.entities.AbuseIPDBData;
import eu.knoker.iploc.entities.Access;
import eu.knoker.iploc.repositories.AbuseIPDBDataRepository;
import io.netty.handler.logging.LogLevel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.util.HashMap;

@Service
public class AbuseIPDB {

    @Value("${abuseipdb.apiKey}")
    private String apiKey;

    @Value("${abuseipdb.refeshTimeout}")
    private long timeOut;

    @Autowired
    private AbuseIPDBDataRepository repository;

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

    @EventListener()
    public void enrich(Access access) {
        if(access.getAbuseIPDBData().getLastUpdated()==null ||
                access.getAbuseIPDBData().getLastUpdated() - System.currentTimeMillis() > this.timeOut) {

            String url = baseUrl + "?verbose&maxAgeInDays=90&ipAddress=" + access.getIp();

            client.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<HashMap<String,AbuseIPDBData>>(){})
                    .subscribe(response -> {

                        AbuseIPDBData data = response.get("data");
                        data.setLastUpdated(System.currentTimeMillis());
                        data.setIp(access.getIp());
                        access.setAbuseIPDBData(repository.save(data));
                    });

        }
    }
}
