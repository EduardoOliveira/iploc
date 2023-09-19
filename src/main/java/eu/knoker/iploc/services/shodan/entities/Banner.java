package eu.knoker.iploc.entities.shodan;

import lombok.*;

import java.util.Optional;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Banner {
    private int port;
    private long ip;
    private String asn;
    private String data;
    private String ipStr;
    private String ipv6;
    private String timestamp;
    private String[] hostnames;
    private String[] domains;
    private Location location;
    private Options options;
    private Metadata metadata;
    private boolean isSslEnabled;
    private SslInfo sslInfo;
    private int uptime;
    private String link;
    private String title;
    private String html;
    private String product;
    private String version;
    private String isp;
    private String os;
    private String transport;
    private String deviceType;
    private String info;
    private String[] cpe;

    public Banner(com.fooock.shodan.model.banner.Banner banner) {
        this.port = banner.getPort();
        this.ip = banner.getIp();
        this.asn = banner.getAsn();
        this.data = banner.getData();
        this.ipStr = banner.getIpStr();
        this.ipv6 = banner.getIpv6();
        this.timestamp = banner.getTimestamp();
        this.hostnames = banner.getHostnames();
        this.domains = banner.getDomains();
        this.location = Optional.ofNullable(banner.getLocation()).map(Location::new).orElse(null);
        this.options = Optional.ofNullable(banner.getOptions()).map(Options::new).orElse(null);
        this.metadata = Optional.ofNullable(banner.getMetadata()).map(Metadata::new).orElse(null);
        this.isSslEnabled = banner.isSslEnabled();
        this.sslInfo = Optional.ofNullable(banner.getSslInfo()).map(SslInfo::new).orElse(null);
        this.uptime = banner.getUptime();
        this.link = banner.getLink();
        this.title = banner.getTitle();
        this.html = banner.getHtml();
        this.product = banner.getProduct();
        this.version = banner.getVersion();
        this.isp = banner.getIsp();
        this.os = banner.getOs();
        this.transport = banner.getTransport();
        this.deviceType = banner.getDeviceType();
        this.info = banner.getInfo();
        this.cpe = banner.getCpe();
    }
}
