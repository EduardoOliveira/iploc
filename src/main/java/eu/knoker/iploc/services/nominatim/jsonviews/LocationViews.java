package eu.knoker.iploc.services.nominatim.jsonviews;

public class LocationView {
    public interface Base{}
    public interface Point extends Base{}
    public interface Geometry extends Base{}
    public interface Full extends Point, Geometry {}
}
