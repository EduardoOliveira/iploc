package eu.knoker.iploc.entities;

import eu.knoker.iploc.enrichers.abuseipdb.AbuseIPDB;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class AbuseIPDBData {

    @Id
    @Getter
    @Setter
    public String ip;
    @Getter
    @Setter
    private Long lastUpdated;
    @Getter
    @Setter
    public boolean isPublic;
    @Getter
    @Setter
    public int ipVersion;
    @Getter
    @Setter
    public boolean isWhitelisted;
    @Getter
    @Setter
    public int abuseConfidenceScore;
    @Getter
    @Setter
    public String countryCode;
    @Getter
    @Setter
    public String countryName;
    @Getter
    @Setter
    public String usageType;
    @Getter
    @Setter
    public String isp;
    @Getter
    @Setter
    public String domain;
    @Getter
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    public List<String> hostnames = new ArrayList<>();
    @Getter
    @Setter
    public boolean isTor;
    @Getter
    @Setter
    public int totalReports;
    @Getter
    @Setter
    public int numDistinctUsers;
    @Getter
    @Setter
    public Date lastReportedAt;
    @Getter
    @Setter
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public List<AbuseIPDBReport> reports = new ArrayList<>();
}
