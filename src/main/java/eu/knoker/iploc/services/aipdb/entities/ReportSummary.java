package eu.knoker.iploc.services.aipdb.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ReportSummary {

    private String countryCode;
    private int totalReports;
    private Set<Integer> categories = new HashSet<>();
    private Date latestReportedAt;

    public ReportSummary(String countryCode) {
        this.countryCode = countryCode;
    }

    public void incrementTotalReports() {
        this.totalReports++;
    }

    public void updateLastReportedAt(Date reportedAt) {
        if (this.latestReportedAt == null || this.latestReportedAt.before(reportedAt)) {
            this.latestReportedAt = reportedAt;
        }
    }
}
