package uk.gov.nationalarchives.discovery.taxonomy.domain.repository;

/**
 * Created by jcharlet on 8/1/16.
 */
public class AtomicUpdate {
    private String iaid;
    private String taxonomyId;
    private String taxonomy;
    private AtomicUpdateType type;

    public AtomicUpdate(String iaid, String taxonomyId, String taxonomy, AtomicUpdateType type) {
        this.iaid = iaid;
        this.taxonomyId = taxonomyId;
        this.taxonomy = taxonomy;
        this.type = type;
    }

    public String getIaid() {
        return iaid;
    }

    public String getTaxonomyId() {
        return taxonomyId;
    }

    public String getTaxonomy() {
        return taxonomy;
    }

    public AtomicUpdateType getType() {
        return type;
    }
}
