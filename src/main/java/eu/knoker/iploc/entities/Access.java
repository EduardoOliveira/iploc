package eu.knoker.iploc.entities;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.util.UUID;

@Entity
public class Access {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Getter
    @Column(nullable = false,unique = true)
    private String ip;

    @Getter
    @ReadOnlyProperty
    @JsonSetter(nulls = Nulls.SKIP)
    private Long count = 0L;

    @Getter
    @JsonSetter(nulls = Nulls.SKIP)
    private Long lastSeen = 0L;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    private ShodanData shodanData = new ShodanData();

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    private AbuseIPDBData abuseIPDBData = new AbuseIPDBData();

    public void setIp(String ip) {
        this.ip = ip;
        if (shodanData != null && shodanData.getIp() == null) {
            shodanData.setIp(ip);
            abuseIPDBData.setIp(ip);
        }
    }
}



