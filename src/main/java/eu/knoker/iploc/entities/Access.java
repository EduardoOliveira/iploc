package eu.knoker.iploc.entities;

import com.fasterxml.jackson.annotation.JsonView;
import eu.knoker.iploc.services.aipdb.entities.AbuseIPDBData;
import eu.knoker.iploc.services.aipdb.jsonviews.AbuseIPDBDataViews;
import eu.knoker.iploc.services.shodan.entities.ShodanData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Access {
    @Id
    private String id;
    private String ip;
    private Map<Integer,Long> accessedPorts = new HashMap<>();
    private Double latitude = 0D;
    private Double longitude = 0D;
    private Long accessCount = 0L;
    private Long lastSeen = 0L;
    private ShodanData shodanData = new ShodanData();

    @JsonView(AbuseIPDBDataViews.Base.class)
    private AbuseIPDBData abuseIPDBData = new AbuseIPDBData();
}



