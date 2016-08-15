package uk.gov.nationalarchives.discovery.taxonomy.repository;

import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.AtomicUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.Category;

import java.util.List;

/**
 * Created by jcharlet on 8/1/16.
 */
public interface UpdateRepository {

    void removeCategoryFromIaids(Category category, List<String> iaids);

    void addCategoryToIaids(Category category, List<String> iaids);

    boolean hasPendingUpdates();

    List<AtomicUpdate> getLastUpdates(Integer nbOfItems);
}
