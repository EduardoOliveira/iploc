package eu.knoker.iploc.entities.shodan;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Cipher {
    private int bits;
    private String version;
    private String name;

    public Cipher(com.fooock.shodan.model.banner.Cipher cipher) {
        this.bits = cipher.getBits();
        this.version = cipher.getVersion();
        this.name = cipher.getName();
    }
}
