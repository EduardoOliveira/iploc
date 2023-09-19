package eu.knoker.iploc.services.nominatim;

import eu.knoker.iploc.services.nominatim.entities.Location;
import eu.knoker.iploc.services.nominatim.converts.GeometryConverter;
import lombok.extern.slf4j.Slf4j;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.ArrayList;

@Service
@Slf4j
public class NominatimService {

    private final String baseUrl = "https://nominatim.openstreetmap.org/search?format=geojson";

    private final WebClient client;

    public NominatimService() {
        client = WebClient.builder()
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                }).codecs(codecs -> codecs
                        .defaultCodecs()
                        .maxInMemorySize(10000 * 1024))
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().followRedirect(true)
                                .wiretap("reactor.netty.http.client.HttpClient")
                )).build();
    }

    public Mono<Location> searchCountryByName(String countryName) {
        return Mono.zip(
                        searchCountryPointByName(countryName),
                        searchCountryPolygonByName(countryName)
                )
                .map(t -> {
                    Location.LocationBuilder lb = Location.builder();
                    lb.osmId(t.getT1().getProperty("osm_id"));
                    lb.importance(t.getT1().getProperty("importance"));
                    lb.addressType(t.getT1().getProperty("addresstype"));
                    lb.name(t.getT1().getProperty("name"));
                    lb.displayName(t.getT1().getProperty("display_name"));

                    GeometryConverter gc = new GeometryConverter();

                    t.getT1().getGeometry().accept(gc);
                    lb.lat(gc.getLat());
                    lb.lon(gc.getLon());
                    lb.point(gc.getPoint());

                    t.getT2().getGeometry().accept(gc);
                    lb.geometry(gc.getMultiPolygonList());

                    ArrayList<Double> bb = new ArrayList<>();
                    for (int i = 0; i < t.getT1().getBbox().length; i++) {
                        bb.add(t.getT1().getBbox()[i]);
                    }
                    lb.boundingBox(bb);

                    return lb.build();
                });
    }

    public Mono<Feature> searchCountryPolygonByName(String countryName) {
        return searchCountryName(countryName, true, true);
    }

    public Mono<Feature> searchCountryPointByName(String countryName) {
        return searchCountryName(countryName, true, false);
    }

    public Mono<Feature> searchCountryName(String countryName, boolean fallback, boolean withPolygon) {
        Mono<Feature> rtn = getFeatureMono("country", countryName, withPolygon);
        if (fallback) {
            rtn = rtn.switchIfEmpty(getFeatureMono("city", countryName, withPolygon))
                    .switchIfEmpty(getFeatureMono("state", countryName, withPolygon));
        }
        return rtn;
    }

    private Mono<Feature> getFeatureMono(String parameterName, String value, boolean withPolygon) {
        String extraParams = "polygon_geojson=1";
        if (!withPolygon) {
            extraParams = "";
        }
        return getRequest(parameterName, value, extraParams);
    }

    @NotNull
    private Mono<Feature> getRequest(String parameterName, String value, String extraParams) {
        return client.get()
                .uri(baseUrl + "&" + parameterName + "=" + value + "&" + extraParams)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<FeatureCollection>() {
                })
                .mapNotNull(response -> {
                    if (!response.getFeatures().isEmpty()) {
                        return response.getFeatures().get(0);
                    }
                    return null;
                }).onErrorResume(e -> {
                    e.printStackTrace();
                    log.error("Error while searching for {} {}: {}", parameterName, value, e.getMessage());
                    return Mono.empty();
                });
    }


}
