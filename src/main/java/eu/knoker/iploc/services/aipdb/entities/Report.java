package eu.knoker.iploc.services.aipdb.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Report {
    private Date reportedAt;
    private String comment;
    private List<Integer> categories;
    private int reporterId;
    private String reporterCountryCode;
    private String reporterCountryName;
}
