package uk.gov.nationalarchives.discovery.taxonomy.repository.impl;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.CursorMarkParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.Category;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.SearchQueryResultsWithCursor;
import uk.gov.nationalarchives.discovery.taxonomy.repository.InformationAssetViewReadRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by jcharlet on 8/1/16.
 */
@Repository
public class SolrInformationAssetViewReadRepositoryImpl implements InformationAssetViewReadRepository {

    public static final String SOLR_DOCREFERENCE_FIELD = InformationAssetViewFields.id.toString();
    private final SolrClient solrCloudReadServer;

    @Value("${solr.cloud.read.query-page-size}")
    public Integer pageSize;

    @Autowired
    public SolrInformationAssetViewReadRepositoryImpl(SolrClient solrCloudReadServer) {
        this.solrCloudReadServer = solrCloudReadServer;
    }

    @Override
    public Integer countItemsMatchingQueryAndWithoutCategory(String categoryQuery, String categoryId, Double queryThreshold) {
        if (Category.hasThreshold(queryThreshold)) {
            return countItemsMatchingQueryWithoutCategoryAndAboveOrBelowScore(categoryQuery, categoryId, queryThreshold, null);
        }

        SolrQuery query = createSolrQuery(matchQuery(categoryQuery), doesNotMatchCategory(categoryId),
                null, 0, false);

        QueryResponse queryResponse = querySolrIndex(query);

        return (int) queryResponse.getResults().getNumFound();
    }

    private Integer countItemsMatchingQueryWithoutCategoryAndAboveOrBelowScore(
            String categoryQuery, String categoryId, Double aboveScore, Double belowScore) {
        String lastCursorMark = null;
        int nbOfItems = 0;
        while (true) {
            SolrQuery query = createSolrQuery(matchQuery(categoryQuery), doesNotMatchCategory(categoryId)
                    , lastCursorMark, pageSize, Category.hasThreshold(aboveScore));

            QueryResponse queryResponse = querySolrIndex(query);

            List<String> iaids = new ArrayList<>();
            for (SolrDocument solrDocument : queryResponse.getResults()) {
                Double score = Double.valueOf(solrDocument.get("score").toString());
                if (aboveScore != null && score >= aboveScore ||
                        belowScore != null && score < belowScore) {
                    iaids.add(solrDocument.get(SOLR_DOCREFERENCE_FIELD).toString());
                }
            }
            nbOfItems += iaids.size();

            if (queryResponse.getNextCursorMark().equals(lastCursorMark)) {
                return nbOfItems;
            }
            lastCursorMark = queryResponse.getNextCursorMark();
        }
    }



    @Override
    public int countItemsMatchingQueryBelowThresholdAndWithCategory(String categoryQuery, String categoryId, Double queryThreshold) {
        String lastCursorMark = null;
        int nbOfItems = 0;
        while (true) {
            SolrQuery query = createSolrQuery(matchQuery(categoryQuery), matchCategory(categoryId), lastCursorMark,
                    pageSize, true);

            QueryResponse queryResponse = querySolrIndex(query);

            List<String> iaids = new ArrayList<>();
            for (SolrDocument solrDocument : queryResponse.getResults()) {
                Double score = Double.valueOf(solrDocument.get("score").toString());
                if (queryThreshold != null && score < queryThreshold) {
                    iaids.add(solrDocument.get(SOLR_DOCREFERENCE_FIELD).toString());
                }
            }
            nbOfItems += iaids.size();

            if (queryResponse.getNextCursorMark().equals(lastCursorMark)) {
                return nbOfItems;
            }
            lastCursorMark = queryResponse.getNextCursorMark();
        }
    }

    @Override
    public SearchQueryResultsWithCursor searchItemsMatchingQueryAndWithoutCategory(String categoryQuery, String categoryId, Double
            queryThreshold, String cursorMark, Integer pageSize) {
        boolean hasThreshold = Category.hasThreshold(queryThreshold);
        SolrQuery query = createSolrQuery(matchQuery(categoryQuery), doesNotMatchCategory((categoryId)),
                cursorMark, pageSize, hasThreshold);

        QueryResponse queryResponse = querySolrIndex(query);

        List<String> iaids = new ArrayList<>();
        for (SolrDocument solrDocument : queryResponse.getResults()) {
            if (!hasThreshold || Double.valueOf(solrDocument.get("score").toString()) >= queryThreshold) {
                iaids.add(solrDocument.get(SOLR_DOCREFERENCE_FIELD).toString());
            }
        }

        return new SearchQueryResultsWithCursor(iaids, queryResponse.getNextCursorMark());
    }

    @Override
    public SearchQueryResultsWithCursor searchItemsMatchingQueryBelowThresholdAndWithCategory(String categoryQuery, String categoryId, Double
            queryThreshold, String cursorMark, Integer pageSize) {
        SolrQuery query = createSolrQuery(matchQuery(categoryQuery), matchCategory(categoryId), cursorMark, pageSize,
                Category.hasThreshold(queryThreshold));

        QueryResponse queryResponse = querySolrIndex(query);

        List<String> iaids = new ArrayList<>();
        for (SolrDocument solrDocument : queryResponse.getResults()) {
            if (Double.valueOf(solrDocument.get("score").toString()) < queryThreshold) {
                iaids.add(solrDocument.get(SOLR_DOCREFERENCE_FIELD).toString());
            }
        }

        return new SearchQueryResultsWithCursor(iaids, queryResponse.getNextCursorMark());
    }


    @Override
    public Integer countItemsNotMatchingQueryAndWithCategory(String categoryQuery, String categoryId) {
        SolrQuery query = createSolrQuery(doesNotMatchQuery((categoryQuery)), matchCategory(categoryId), null, 0,
                false);

        QueryResponse queryResponse = querySolrIndex(query);

        return (int) queryResponse.getResults().getNumFound();
    }

    @Override
    public SearchQueryResultsWithCursor searchItemsNotMatchingQueryAndWithCategory(String categoryQuery, String categoryId,
                                                                                   String cursorMark,
                                                                                   Integer pageSize) {
        SolrQuery query = createSolrQuery(doesNotMatchQuery((categoryQuery)), matchCategory(categoryId), cursorMark,
                pageSize, false);

        QueryResponse queryResponse = querySolrIndex(query);

        List<String> iaids = new ArrayList<>();
        for (SolrDocument solrDocument : queryResponse.getResults()) {
            iaids.add(solrDocument.get(SOLR_DOCREFERENCE_FIELD).toString());
        }

        return new SearchQueryResultsWithCursor(iaids, queryResponse.getNextCursorMark());
    }


    @Override
    public List<String> searchItemsMatchingQueryWithoutCategoryAndFilterByDocIds(String categoryQuery, String categoryId, Double queryThreshold, List<String> documentIds) {

        //FIXME take into account with / without threshold

        boolean hasQueryThreshold = Category.hasThreshold(queryThreshold);

        String filterQuery = doesNotMatchCategory(categoryId) + " " + matchDocumentIds(documentIds);
        SolrQuery query = createSolrQuery(matchQuery(categoryQuery), filterQuery, null,
                pageSize, hasQueryThreshold);

        QueryResponse queryResponse = querySolrIndex(query);

        List<String> iaids = new ArrayList<>();
        for (SolrDocument solrDocument : queryResponse.getResults()) {
            iaids.add(solrDocument.get(SOLR_DOCREFERENCE_FIELD).toString());
        }
        return iaids;
    }

    @Override
    public List<String> searchItemsNotMatchingQueryWithCategoryAndFilterByDocIds(String categoryQuery, String categoryId, Double queryThreshold, List<String> documentIds) {

        //FIXME to refactor nicely
        boolean hasQueryThreshold = Category.hasThreshold(queryThreshold);

        String filterQuery = matchCategory(categoryId) + " " + matchDocumentIds(documentIds);
        SolrQuery query = createSolrQuery(doesNotMatchQuery(categoryQuery), filterQuery, null,
                pageSize, hasQueryThreshold);

        QueryResponse queryResponse = querySolrIndex(query);

        List<String> iaids = new ArrayList<>();
        for (SolrDocument solrDocument : queryResponse.getResults()) {
            iaids.add(solrDocument.get(SOLR_DOCREFERENCE_FIELD).toString());
        }
        return iaids;
    }

    @Override
    public List<String> searchItemsMatchingQueryBelowThresholdWithCategoryAndFilterByDocIds(String categoryQuery, String categoryId, Double queryThreshold, List<String> documentIds) {

        //FIXME to refactor nicely

        boolean hasQueryThreshold = Category.hasThreshold(queryThreshold);

        String filterQuery = doesNotMatchCategory(categoryId) + " " + matchDocumentIds(documentIds);
        SolrQuery query = createSolrQuery(matchQuery(categoryQuery), filterQuery, null,
                pageSize, hasQueryThreshold);

        QueryResponse queryResponse = querySolrIndex(query);

        List<String> iaids = new ArrayList<>();
        for (SolrDocument solrDocument : queryResponse.getResults()) {
            iaids.add(solrDocument.get(SOLR_DOCREFERENCE_FIELD).toString());
        }
        return iaids;
    }

    private QueryResponse querySolrIndex(SolrQuery query) {
        QueryResponse queryResponse;
        try {
            queryResponse = solrCloudReadServer.query(query, SolrRequest.METHOD.POST);
        } catch (Exception e) {
            throw new TaxonomyException(TaxonomyErrorType.SOLR_READ_EXCEPTION, e);
        }
        return queryResponse;
    }


    private String matchDocumentIds(List<String> documentIds) {
        StringJoiner stringJoiner = new StringJoiner(" OR ", "DOCREFERENCE:(", ")");
        for (String documentId : documentIds) {
            stringJoiner.add(documentId);
        }

        return stringJoiner.toString();
    }

    private String matchQuery(String query) {
        return "textnocasnopunc:(" + query + ")";
    }

    private String doesNotMatchQuery(String query) {
        return "NOT(" + matchQuery(query) + ")";
    }

    private String matchCategory(String categoryId) {
        return InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
    }

    private String doesNotMatchCategory(String query) {
        return "-(" + matchCategory(query) + ")";
    }

    private SolrQuery createSolrQuery(String categoryQuery, String filterQuery,
                                      String cursorMark, Integer pageSize, boolean hasQueryThreshold) {
        String solrQuery;
        String solrFilter;
        if (!hasQueryThreshold) {
            solrQuery = "*:*";
            solrFilter = categoryQuery + " " + filterQuery;
        } else {
            solrQuery = categoryQuery;
            solrFilter = filterQuery;
        }


        if (cursorMark == null) {
            cursorMark = CursorMarkParams.CURSOR_MARK_START;
        }

        SolrQuery query = new SolrQuery();
        query.setQuery(solrQuery);
        query.addFilterQuery(solrFilter);

        query.addField(SOLR_DOCREFERENCE_FIELD);
        if (hasQueryThreshold) {
            query.addField(InformationAssetViewFields.score.toString());
        }

        query.setRows(pageSize);
        query.set(CursorMarkParams.CURSOR_MARK_PARAM, cursorMark);
        query.setSort(SolrQuery.SortClause.asc(InformationAssetViewFields.DOCREFERENCE.toString()));
        return query;
    }


}
