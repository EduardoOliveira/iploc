package eu.knoker.iploc.services.nominatim.entities;

import com.fasterxml.jackson.annotation.JsonView;
import eu.knoker.iploc.services.nominatim.jsonviews.LocationViews;
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
    @JsonView({LocationViews.Base.class})
    private int osmId;
    @JsonView({LocationViews.Base.class})
    private Double lat;
    @JsonView({LocationViews.Base.class})
    private Double lon;

    @JsonView({LocationViews.Point.class})
    @GeoSpatialIndexed(type= GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint point;

    @JsonView({LocationViews.Base.class})
    private Double importance;

    @JsonView({LocationViews.Base.class})
    private String addressType;

    @JsonView({LocationViews.Base.class})
    private String name;

    @JsonView({LocationViews.Base.class})
    private String displayName;


    @Builder.Default
    @JsonView({LocationViews.Base.class})
    private ArrayList<Double> boundingBox = new ArrayList<>();

    @Builder.Default
    @JsonView({LocationViews.Base.class})
    private Set<String> matches = new HashSet<>();

    @JsonView({LocationViews.Geometry.class})
    @GeoSpatialIndexed(type= GeoSpatialIndexType.GEO_2DSPHERE)
    private List<GeoJsonMultiPolygon> geometry;
}
