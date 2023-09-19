package eu.knoker.iploc.services.shodan.entities;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Options {
    private String raw;

    public Options(com.fooock.shodan.model.banner.Options options) {
        this.raw = options.getRaw();
    }
}
