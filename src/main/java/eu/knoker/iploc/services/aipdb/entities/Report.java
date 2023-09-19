package eu.knoker.iploc.services.aipdb.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AbuseIPDBReport {
    private Date reportedAt;
    private String comment;
    private int[] categories;
    private int reporterId;
    private String reporterCountryCode;
    private String reporterCountryName;
}
