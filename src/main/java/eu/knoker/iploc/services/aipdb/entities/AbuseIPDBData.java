package eu.knoker.iploc.services.aipdb.entities;

import eu.knoker.iploc.entities.EnrichmentStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AbuseIPDB {
    private String ip;
    private EnrichmentStatus enrichmentStatus = EnrichmentStatus.NOT_ENRICHED;
    private Long lastUpdated;
    private boolean isPublic;
    private int ipVersion;
    private boolean isWhitelisted;
    private int abuseConfidenceScore;
    private String countryCode;
    private String countryName;
    private String usageType;
    private String isp;
    private String domain;
    private boolean isTor;
    private int totalReports;
    private int numDistinctUsers;
    private Date lastReportedAt;

    private List<String> hostnames = new ArrayList<>();
    private List<AbuseIPDBReport> reports = new ArrayList<>();

}
