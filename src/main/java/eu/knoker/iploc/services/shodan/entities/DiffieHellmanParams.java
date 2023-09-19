package eu.knoker.iploc.entities.shodan;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DiffieHellmanParams {
    private int bits;
    private String prime;
    private String publicKey;
    private String generator;
    private String fingerprint;

    public DiffieHellmanParams(com.fooock.shodan.model.banner.DiffieHellmanParams diffieHellmanParams) {
        this.bits = diffieHellmanParams.getBits();
        this.prime = diffieHellmanParams.getPrime();
        this.publicKey = diffieHellmanParams.getPublicKey();
        this.generator = diffieHellmanParams.getGenerator();
        this.fingerprint = diffieHellmanParams.getFingerprint();
    }
}
