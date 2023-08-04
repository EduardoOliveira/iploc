package eu.knoker.iploc.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ShodanData {
    @Id
    @Getter
    @Setter
    private String ip;
    @Getter
    @Setter
    private Long lastUpdated;
    @Getter
    @Setter
    private double latitude;
    @Getter
    @Setter
    private double longitude;
    @Getter
    @Setter
    private int[] ports = new int[0];
    @Getter
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> hostnames = new ArrayList<>();
    @Getter
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> tags = new ArrayList<>();
    @Getter
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> vulnerabilities = new ArrayList<>();
    @Getter
    @Setter
    private String regionCode;
    @Getter
    @Setter
    private String areaCode;
    @Getter
    @Setter
    private String postalCode;
    @Getter
    @Setter
    private String dmaCode;
    @Getter
    @Setter
    private String countryCode;
    @Getter
    @Setter
    private String organization;
    @Getter
    @Setter
    private String asn;
    @Getter
    @Setter
    private String city;
    @Getter
    @Setter
    private String isp;
    @Getter
    @Setter
    private String lastUpdate;
    @Getter
    @Setter
    private String countryCode3;
    @Getter
    @Setter
    private String countryName;
    @Getter
    @Setter
    private String ipStr;
    @Getter
    @Setter
    private String os;
}
