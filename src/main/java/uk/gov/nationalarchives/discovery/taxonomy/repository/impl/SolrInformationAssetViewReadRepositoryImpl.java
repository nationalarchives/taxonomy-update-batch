package uk.gov.nationalarchives.discovery.taxonomy.repository.impl;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
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

        String matchCategoryIdQuery= InformationAssetViewFields.TAXONOMYID + ":" + categoryId;

        SolrQuery query = createSolrQuery(textnocasnopunc(categoryQuery), arrayDoesNotContain(matchCategoryIdQuery),
                null, 0, false);

        QueryResponse queryResponse = querySolrIndex(query);

        return (int) queryResponse.getResults().getNumFound();
    }

    private Integer countItemsMatchingQueryWithoutCategoryAndAboveOrBelowScore(
            String categoryQuery, String categoryId, Double aboveScore, Double belowScore) {
        String lastCursorMark = null;
        int nbOfItems = 0;
        while (true) {
            String matchCategoryIdQuery = InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
            SolrQuery query = createSolrQuery(textnocasnopunc(categoryQuery), arrayDoesNotContain
                    (matchCategoryIdQuery), lastCursorMark, pageSize, Category.hasThreshold(aboveScore));

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
            String matchCategoryIdQuery = InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
            SolrQuery query = createSolrQuery(textnocasnopunc(categoryQuery), matchCategoryIdQuery, lastCursorMark,
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
        String matchCategoryIdQuery= InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
        boolean hasThreshold = Category.hasThreshold(queryThreshold);
        SolrQuery query = createSolrQuery(textnocasnopunc(categoryQuery), arrayDoesNotContain(matchCategoryIdQuery),
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
        String matchCategoryIdQuery= InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
        SolrQuery query = createSolrQuery(textnocasnopunc(categoryQuery), matchCategoryIdQuery, cursorMark, pageSize,
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
        String matchCategoryIdQuery = InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
        SolrQuery query = createSolrQuery(not(textnocasnopunc(categoryQuery)), matchCategoryIdQuery, null, 0, false);

        QueryResponse queryResponse = querySolrIndex(query);

        return (int) queryResponse.getResults().getNumFound();
    }

    @Override
    public SearchQueryResultsWithCursor searchItemsNotMatchingQueryAndWithCategory(String categoryQuery, String categoryId,
                                                                                   String cursorMark,
                                                                                   Integer pageSize) {
        String matchCategoryIdQuery= InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
        SolrQuery query = createSolrQuery(not(textnocasnopunc(categoryQuery)), matchCategoryIdQuery, cursorMark,
                pageSize, false);

        QueryResponse queryResponse = querySolrIndex(query);

        List<String> iaids = new ArrayList<>();
        for (SolrDocument solrDocument : queryResponse.getResults()) {
            iaids.add(solrDocument.get(SOLR_DOCREFERENCE_FIELD).toString());
        }

        return new SearchQueryResultsWithCursor(iaids, queryResponse.getNextCursorMark());
    }

    private QueryResponse querySolrIndex(SolrQuery query) {
        QueryResponse queryResponse;
        try {
            queryResponse = solrCloudReadServer.query(query);
        } catch (Exception e) {
            throw new TaxonomyException(TaxonomyErrorType.SOLR_READ_EXCEPTION, e);
        }
        return queryResponse;
    }

    private String not(String query) {
        return "NOT(" + query + ")";
    }
    private String arrayDoesNotContain(String query) {
        return "-(" + query + ")";
    }

    private String textnocasnopunc(String query){
        return "textnocasnopunc:(" + query + ")";
    }

    private SolrQuery createSolrQuery(String categoryQuery, String matchCategoryIdQuery,
                                      String cursorMark, Integer pageSize, boolean hasQueryThreshold) {
        String solrQuery;
        String solrFilter;
        if (!hasQueryThreshold) {
            solrQuery = "*:*";
            solrFilter = categoryQuery + " " + matchCategoryIdQuery;
        } else {
            solrQuery = categoryQuery;
            solrFilter = matchCategoryIdQuery;
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
