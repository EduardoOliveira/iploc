package eu.knoker.iploc.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
public class AbuseIPDBReport {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public int id;
    @Getter
    @Setter
    public Date reportedAt;
    @Getter
    @Setter
    public String comment;
    @Getter
    @Setter
    public int[] categories;
    @Getter
    @Setter
    public int reporterId;
    @Getter
    @Setter
    public String reporterCountryCode;
    @Getter
    @Setter
    public String reporterCountryName;
}
