package eu.knoker.iploc.services.shodan.entities;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private int areaCode;
    private double latitude;
    private double longitude;
    private String city;
    private String regionCode;
    private String postalCode;
    private String dmaCode;
    private String countryCode;
    private String countryCode3;
    private String countryName;

    public Location(com.fooock.shodan.model.banner.Location location) {
        this.areaCode = location.getAreaCode();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.city = location.getCity();
        this.regionCode = location.getRegionCode();
        this.postalCode = location.getPostalCode();
        this.dmaCode = location.getDmaCode();
        this.countryCode = location.getCountryCode();
        this.countryCode3 = location.getCountryCode3();
        this.countryName = location.getCountryName();
    }
}
