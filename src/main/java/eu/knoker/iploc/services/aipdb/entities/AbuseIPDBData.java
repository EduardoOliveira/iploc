package eu.knoker.iploc.services.aipdb.entities;

import com.fasterxml.jackson.annotation.JsonView;
import eu.knoker.iploc.entities.EnrichmentStatus;
import eu.knoker.iploc.services.aipdb.jsonviews.AbuseIPDBDataViews;
import lombok.*;

import java.util.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AbuseIPDBData {
    @JsonView(AbuseIPDBDataViews.Base.class)
    private String ip;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private EnrichmentStatus enrichmentStatus = EnrichmentStatus.NOT_ENRICHED;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private Long lastUpdated;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private boolean isPublic;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private int ipVersion;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private boolean isWhitelisted;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private int abuseConfidenceScore;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private String countryCode;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private String countryName;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private String usageType;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private String isp;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private String domain;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private boolean isTor;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private int totalReports;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private int numDistinctUsers;
    @JsonView(AbuseIPDBDataViews.Base.class)
    private Date lastReportedAt;

    @JsonView(AbuseIPDBDataViews.Base.class)
    private List<String> hostnames = new ArrayList<>();

    @JsonView(AbuseIPDBDataViews.Report.class)
    private List<Report> reports = new ArrayList<>();

    @JsonView(AbuseIPDBDataViews.Base.class)
    private Map<String,ReportSummary> reportSummary = new HashMap<>();

}
