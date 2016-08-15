package uk.gov.nationalarchives.discovery.taxonomy.repository.impl;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.CursorMarkParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyException;
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

    @Autowired
    public SolrInformationAssetViewReadRepositoryImpl(SolrClient solrCloudReadServer) {
        this.solrCloudReadServer = solrCloudReadServer;
    }

    @Override
    public Integer countItemsNotMatchingQueryAndWithCategory(String categoryQuery, String categoryId, Double scoreThreshold) {
        String matchCategoryIdQuery= InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
        SolrQuery query = createSolrQuery(not(categoryQuery), matchCategoryIdQuery, false, null, 0);

        QueryResponse queryResponse = querySolrIndex(query);

        return (int) queryResponse.getResults().getNumFound();
    }

    @Override
    public SearchQueryResultsWithCursor searchItemsNotMatchingQueryAndWithCategory(String categoryQuery, String categoryId,
                                                                                   boolean hasQueryThreshold, String cursorMark,
                                                                   Integer pageSize) {
        String matchCategoryIdQuery= InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
        SolrQuery query = createSolrQuery(not(categoryQuery), matchCategoryIdQuery, hasQueryThreshold, cursorMark, pageSize);

        QueryResponse queryResponse = querySolrIndex(query);

        List<String> iaids = new ArrayList<>();
        for (SolrDocument solrDocument : queryResponse.getResults()) {
            iaids.add(solrDocument.get(SOLR_DOCREFERENCE_FIELD).toString());
        }

        return new SearchQueryResultsWithCursor(iaids, queryResponse.getNextCursorMark());
    }

    @Override
    public Integer countItemsMatchingQueryAndWithoutCategory(String categoryQuery, String categoryId, Double scoreThreshold) {
        String matchCategoryIdQuery= InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
        SolrQuery query = createSolrQuery(categoryQuery, arrayDoesNotContain(matchCategoryIdQuery), false, null, 0);

        QueryResponse queryResponse = querySolrIndex(query);

        return (int) queryResponse.getResults().getNumFound();
    }

    @Override
    public SearchQueryResultsWithCursor searchItemsMatchingQueryAndWithoutCategory(String categoryQuery, String categoryId, boolean
            hasQueryThreshold, String cursorMark, Integer pageSize) {
        String matchCategoryIdQuery= InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
        SolrQuery query = createSolrQuery(categoryQuery, arrayDoesNotContain(matchCategoryIdQuery), hasQueryThreshold, cursorMark, pageSize);

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

    private SolrQuery createSolrQuery(String categoryQuery, String matchCategoryIdQuery, boolean hasQueryThreshold, String cursorMark, Integer pageSize) {
        String solrQuery;
        String solrFilter;
        if (hasQueryThreshold) {
            solrQuery = "*:*";
            solrFilter = textnocasnopunc(categoryQuery) + " " + matchCategoryIdQuery;
        } else {
            solrQuery = textnocasnopunc(categoryQuery);
            solrFilter = matchCategoryIdQuery;
        }


        if (cursorMark == null) {
            cursorMark = CursorMarkParams.CURSOR_MARK_START;
        }

        SolrQuery query = new SolrQuery();
        query.setQuery(solrQuery);
        query.addFilterQuery(solrFilter);
        query.setFields(SOLR_DOCREFERENCE_FIELD);
        query.setRows(pageSize);
        query.set(CursorMarkParams.CURSOR_MARK_PARAM, cursorMark);
        query.setSort(SolrQuery.SortClause.asc(InformationAssetViewFields.DOCREFERENCE.toString()));
        return query;
    }
}
