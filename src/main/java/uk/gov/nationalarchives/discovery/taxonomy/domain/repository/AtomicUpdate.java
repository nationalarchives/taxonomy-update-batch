package uk.gov.nationalarchives.discovery.taxonomy.domain.repository;

/**
 * Created by jcharlet on 8/1/16.
 */
public class AtomicUpdate {
    private String iaid;
    private String categoryId;
    private String categoryTitle;
    private AtomicUpdateType type;

    public AtomicUpdate(String iaid, String categoryId, String categoryTitle, AtomicUpdateType type) {
        this.iaid = iaid;
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.type = type;
    }

    public String getIaid() {
        return iaid;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public AtomicUpdateType getType() {
        return type;
    }
}
