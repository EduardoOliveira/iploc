package eu.knoker.iploc.entities.nominatim;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPolygon;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    @Id
    private int osmId;
    private Double lat;
    private Double lon;

    @GeoSpatialIndexed(type= GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint point;

    private Double importance;
    private String addressType;
    private String name;
    private String displayName;

    @Builder.Default
    private ArrayList<Double> boundingBox = new ArrayList<>();
    @Builder.Default
    private Set<String> matches = new HashSet<>();

    @GeoSpatialIndexed(type= GeoSpatialIndexType.GEO_2DSPHERE)
    private List<GeoJsonMultiPolygon> geometry;
}
