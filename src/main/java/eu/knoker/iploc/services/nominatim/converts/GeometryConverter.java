package eu.knoker.iploc.services.nominatim.converts;

import com.mongodb.client.model.geojson.PolygonCoordinates;
import com.mongodb.client.model.geojson.Position;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.geojson.GeoJsonObjectVisitor.Adapter;
import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.geojson.Point;
import org.geojson.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class GeometryConverterConverter extends Adapter<MultiPolygon> {
    com.mongodb.client.model.geojson.MultiPolygon multiPolygon;
    private Double lat;
    private Double lon;


    @Override
    public MultiPolygon visit(Point point) {
        this.lat = point.getCoordinates().getLatitude();
        this.lon = point.getCoordinates().getLongitude();
        return super.visit(point);
    }
    @Override
    public MultiPolygon visit(MultiPolygon mp) {
        List<PolygonCoordinates> polygons = new ArrayList<>();
        mp.getCoordinates().forEach(polygon -> polygons.add(getPolygonCoordinates(polygon)));
        multiPolygon = new com.mongodb.client.model.geojson.MultiPolygon(polygons);
        return super.visit(mp);
    }

    @Override
    public MultiPolygon visit(Polygon p) {
        multiPolygon = new com.mongodb.client.model.geojson.MultiPolygon(
                List.of(
                        getPolygonCoordinates(p.getCoordinates())
                )
        );
        return super.visit(p);
    }

    @NotNull
    private static PolygonCoordinates getPolygonCoordinates(List<List<LngLatAlt>> polygon) {
        List<LngLatAlt> outer = polygon.get(0);
        List<Position> outerPositions = outer.stream()
                .map(lngLatAlt -> new Position(lngLatAlt.getLongitude(), lngLatAlt.getLatitude())).toList();
        List<List<LngLatAlt>> holes = polygon.subList(1, polygon.size());
        List<List<Position>> holePositions = holes.stream()
                .map(hole -> hole.stream()
                        .map(lngLatAlt -> new Position(lngLatAlt.getLongitude(), lngLatAlt.getLatitude()))
                        .toList())
                .toList();
        return new PolygonCoordinates(outerPositions, holePositions);
    }

}
