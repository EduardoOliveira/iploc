package eu.knoker.iploc.services.nominatim.converts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.geojson.GeoJsonObjectVisitor.Adapter;
import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.geojson.Point;
import org.geojson.Polygon;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPolygon;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class GeometryConverter extends Adapter<MultiPolygon> {
    private List<GeoJsonMultiPolygon> multiPolygonList = new ArrayList<>();
    private GeoJsonPoint point;
    private Double lat;
    private Double lon;


    @Override
    public MultiPolygon visit(Point point) {
        this.lat = point.getCoordinates().getLatitude();
        this.lon = point.getCoordinates().getLongitude();
        this.point = new GeoJsonPoint(lon, lat);
        return super.visit(point);
    }

    @Override
    public MultiPolygon visit(MultiPolygon mp) {
        mp.getCoordinates().forEach(p -> multiPolygonList.add(toMultiPolygon(p)));
        return super.visit(mp);
    }

    @Override
    public MultiPolygon visit(Polygon p) {
        multiPolygonList.add(toMultiPolygon(p.getCoordinates()));
        return super.visit(p);
    }

    private GeoJsonMultiPolygon toMultiPolygon(List<List<LngLatAlt>> polygon) {
        List<GeoJsonPolygon> polygons = new ArrayList<>();
        polygon.forEach(p -> polygons.add(toPolygon(p)));
        return new GeoJsonMultiPolygon(polygons);
    }

    private GeoJsonPolygon toPolygon(List<LngLatAlt> polygon) {
        return new GeoJsonPolygon(polygon.stream().map(this::topPoint).toList());
    }

    private org.springframework.data.geo.Point topPoint(LngLatAlt coordinates) {
        return new org.springframework.data.geo.Point(coordinates.getLongitude(), coordinates.getLatitude());
    }

}
