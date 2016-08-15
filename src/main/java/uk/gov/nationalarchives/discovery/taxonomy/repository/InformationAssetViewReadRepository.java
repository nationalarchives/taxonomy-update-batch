package uk.gov.nationalarchives.discovery.taxonomy.repository;

import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.SearchQueryResultsWithCursor;

/**
 * Created by jcharlet on 8/1/16.
 */
public interface InformationAssetViewReadRepository {
    Integer countItemsNotMatchingQueryAndWithCategory(String categoryQuery, String categoryId, Double scoreThreshold);

    SearchQueryResultsWithCursor searchItemsNotMatchingQueryAndWithCategory(String categoryQuery, String categoryId, boolean hasQueryThreshold, String cursorMark,
                                                            Integer pageSize);

    Integer countItemsMatchingQueryAndWithoutCategory(String categoryQuery, String categoryId, Double scoreThreshold);

    SearchQueryResultsWithCursor searchItemsMatchingQueryAndWithoutCategory(String categoryQuery, String categoryId, boolean hasQueryThreshold, String cursorMark,
                                                                            Integer pageSize);
}
