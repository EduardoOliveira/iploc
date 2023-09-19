package eu.knoker.iploc.services.nominatim.jsonviews;

public class LocationViews {
    public interface Base{}
    public interface Point extends Base{}
    public interface Geometry extends Base{}
    public interface Full extends Point, Geometry {}
}
