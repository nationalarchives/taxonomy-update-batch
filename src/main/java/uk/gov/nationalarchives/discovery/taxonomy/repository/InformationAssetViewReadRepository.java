package uk.gov.nationalarchives.discovery.taxonomy.repository;

import java.util.List;

/**
 * Created by jcharlet on 8/1/16.
 */
public interface InformationAssetViewReadRepository {
    Integer countItemsNotMatchingQueryAndWithCategory(String categoryQuery, String categoryId, Double scoreThreshold);

    List<String> searchItemsNotMatchingQueryAndWithCategory(String categoryQuery, String categoryId, boolean hasQueryThreshold, Integer offset,
                                                            Integer pageSize);

    Integer countItemsMatchingQueryAndWithoutCategory(String categoryQuery, String categoryId, Double scoreThreshold);

    List<String> searchItemsMatchingQueryAndWithoutCategory(String categoryQuery, String categoryId, boolean hasQueryThreshold, Integer offset,
                                                            Integer pageSize);
}
