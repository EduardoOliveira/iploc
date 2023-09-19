package eu.knoker.iploc.entities;

public enum EnrichmentStatus {
    ENRICHED("ENRICHED"),
    ENRICHMENT_FAILED("ENRICHMENT_FAILED"),
    ENRICHMENT_MISSING("ENRICHMENT_MISSING"),
    NOT_ENRICHED("NOT_ENRICHED");

    public final String label;

    private EnrichmentStatus(String label) {
        this.label = label;
    }

}
