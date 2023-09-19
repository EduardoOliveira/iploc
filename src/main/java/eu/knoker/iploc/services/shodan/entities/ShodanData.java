package eu.knoker.iploc.services.shodan.entities;

import com.fooock.shodan.model.host.Host;
import eu.knoker.iploc.entities.EnrichmentStatus;
import lombok.*;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Shodan {
    private String ip;
    private EnrichmentStatus enrichmentStatus = EnrichmentStatus.NOT_ENRICHED;

    private Long lastUpdated;
    private double latitude;
    private double longitude;
    private int[] ports = new int[0];
    private String regionCode;
    private String areaCode;
    private String postalCode;
    private String dmaCode;
    private String countryCode;
    private String organization;
    private String asn;
    private String city;
    private String isp;
    private String lastUpdate;
    private String countryCode3;
    private String countryName;
    private String ipStr;
    private String os;
    private List<String> hostnames = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private List<String> vulnerabilities = new ArrayList<>();
    private List<Banner> banners = new ArrayList<>();

    @Transient
    private int totalBanners;
    @Transient
    private int totalVulnerabilities;
    @AccessType(AccessType.Type.PROPERTY)
    public void setBanners(List<Banner> banners) {
        this.banners = banners;
        this.totalBanners = banners.size();
    }

    @AccessType(AccessType.Type.PROPERTY)
    public void setVulnerabilities(List<String> vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
        this.totalVulnerabilities = vulnerabilities.size();
    }

    public Shodan(Host host) {
        this.latitude = host.getLatitude();
        this.longitude = host.getLongitude();
        this.ports = host.getPorts();
        this.hostnames = List.of(host.getHostnames());
        this.tags = List.of(host.getTags());
        this.vulnerabilities = List.of(host.getVulnerabilities());
        this.regionCode = host.getRegionCode();
        this.areaCode = host.getAreaCode();
        this.postalCode = host.getPostalCode();
        this.dmaCode = host.getDmaCode();
        this.countryCode = host.getCountryCode();
        this.organization = host.getOrganization();
        this.asn = host.getAsn();
        this.city = host.getCity();
        this.isp = host.getIsp();
        this.lastUpdate = host.getLastUpdate();
        this.countryCode3 = host.getCountryCode3();
        this.countryName = host.getCountryName();
        this.ipStr = host.getIpStr();
        this.os = host.getOs();
        host.getBanners().stream().map(Banner::new).forEach(banners::add);
        this.lastUpdated = System.currentTimeMillis();
    }

    public void update(Host host) {
        this.latitude = host.getLatitude();
        this.longitude = host.getLongitude();
        this.ports = host.getPorts();
        this.hostnames = List.of(Optional.ofNullable(host.getHostnames()).orElse(new String[0]));
        this.tags = List.of(Optional.ofNullable(host.getTags()).orElse(new String[0]));
        this.vulnerabilities = List.of(Optional.ofNullable(host.getVulnerabilities()).orElse(new String[0]));
        this.regionCode = host.getRegionCode();
        this.areaCode = host.getAreaCode();
        this.postalCode = host.getPostalCode();
        this.dmaCode = host.getDmaCode();
        this.countryCode = host.getCountryCode();
        this.organization = host.getOrganization();
        this.asn = host.getAsn();
        this.city = host.getCity();
        this.isp = host.getIsp();
        this.lastUpdate = host.getLastUpdate();
        this.countryCode3 = host.getCountryCode3();
        this.countryName = host.getCountryName();
        this.ipStr = host.getIpStr();
        this.os = host.getOs();
        this.banners.clear();
        host.getBanners().stream().map(Banner::new).forEach(banners::add);
        this.lastUpdated = System.currentTimeMillis();
    }
}
