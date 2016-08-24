package uk.gov.nationalarchives.discovery.taxonomy.repository;

import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.SearchQueryResultsWithCursor;

import java.util.List;

/**
 * Created by jcharlet on 8/1/16.
 */
public interface InformationAssetViewReadRepository {
    Integer countItemsMatchingQueryAndWithoutCategory(String categoryQuery, String categoryId, Double queryThreshold);

    SearchQueryResultsWithCursor searchItemsMatchingQueryAndWithoutCategory(String categoryQuery, String categoryId, Double queryThreshold, String cursorMark,
                                                                            Integer pageSize);

    Integer countItemsNotMatchingQueryAndWithCategory(String categoryQuery, String categoryId);

    SearchQueryResultsWithCursor searchItemsNotMatchingQueryAndWithCategory(String categoryQuery, String categoryId, String cursorMark,
                                                                            Integer pageSize);

    SearchQueryResultsWithCursor searchItemsMatchingQueryBelowThresholdAndWithCategory(String categoryQuery, String categoryId, Double
            queryThreshold, String cursorMark, Integer pageSize);

    int countItemsMatchingQueryBelowThresholdAndWithCategory(String categoryQuery, String categoryId, Double queryThreshold);

    List<String> searchItemsMatchingQueryWithoutCategoryAndFilterByDocIds(String categoryQuery, String categoryId, Double queryThreshold, List<String> documentIds);

    List<String> searchItemsNotMatchingQueryWithCategoryAndFilterByDocIds(String categoryQuery, String categoryId, Double queryThreshold, List<String> documentIds);

    List<String> searchItemsMatchingQueryBelowThresholdWithCategoryAndFilterByDocIds(String categoryQuery, String categoryId, Double queryThreshold, List<String> documentIds);

}
