package eu.knoker.iploc.services.shodan.entities;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {
    private String crawler;
    private String id;
    private String module;

    public Metadata(com.fooock.shodan.model.banner.Metadata metadata) {
        this.crawler = metadata.getCrawler();
        this.id = metadata.getId();
        this.module = metadata.getModule();
    }
}
