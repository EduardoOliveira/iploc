package eu.knoker.iploc.services.shodan.entities;


import lombok.*;

import java.util.Optional;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SslInfo {
    private String[] chain;
    private String[] versions;
    private Cipher cipher;
    private DiffieHellmanParams diffieHellmanParams;

    public SslInfo(com.fooock.shodan.model.banner.SslInfo sslInfo) {
        this.chain = sslInfo.getChain();
        this.versions = sslInfo.getVersions();
        this.cipher = Optional.ofNullable(sslInfo.getCipher()).map(Cipher::new).orElse(null);
        this.diffieHellmanParams = Optional.ofNullable(sslInfo.getDiffieHellmanParams()).map(DiffieHellmanParams::new).orElse(null);
    }
}
