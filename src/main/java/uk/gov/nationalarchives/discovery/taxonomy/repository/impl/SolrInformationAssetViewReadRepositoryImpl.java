package uk.gov.nationalarchives.discovery.taxonomy.repository.impl;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.InformationAssetViewFields;
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
        SolrQuery query = createSolrQuery(not(categoryQuery), matchCategoryIdQuery, false, 0, 0);

        QueryResponse queryResponse = querySolrIndex(query);

        return (int) queryResponse.getResults().getNumFound();
    }

    @Override
    public List<String> searchItemsNotMatchingQueryAndWithCategory(String categoryQuery, String categoryId,
                                                                   boolean hasQueryThreshold, Integer offset,
                                                                   Integer pageSize) {
        String matchCategoryIdQuery= InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
        SolrQuery query = createSolrQuery(not(categoryQuery), matchCategoryIdQuery, hasQueryThreshold, offset, pageSize);

        QueryResponse queryResponse = querySolrIndex(query);

        List<String> iaids = new ArrayList<>();
        for (SolrDocument solrDocument : queryResponse.getResults()) {
            iaids.add(solrDocument.get(SOLR_DOCREFERENCE_FIELD).toString());
        }

        return iaids;
    }

    @Override
    public Integer countItemsMatchingQueryAndWithoutCategory(String categoryQuery, String categoryId, Double scoreThreshold) {
        String matchCategoryIdQuery= InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
        SolrQuery query = createSolrQuery(categoryQuery, arrayDoesNotContain(matchCategoryIdQuery), false, 0, 0);

        QueryResponse queryResponse = querySolrIndex(query);

        return (int) queryResponse.getResults().getNumFound();
    }

    @Override
    public List<String> searchItemsMatchingQueryAndWithoutCategory(String categoryQuery, String categoryId, boolean hasQueryThreshold, Integer offset, Integer pageSize) {
        String matchCategoryIdQuery= InformationAssetViewFields.TAXONOMYID + ":" + categoryId;
        SolrQuery query = createSolrQuery(categoryQuery, arrayDoesNotContain(matchCategoryIdQuery), hasQueryThreshold, offset, pageSize);

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

    private SolrQuery createSolrQuery(String categoryQuery, String matchCategoryIdQuery, boolean hasQueryThreshold, Integer offset, Integer pageSize) {
        String solrQuery;
        String solrFilter;
        if (hasQueryThreshold) {
            solrQuery = "*:*";
            solrFilter = textnocasnopunc(categoryQuery) + " " + matchCategoryIdQuery;
        } else {
            solrQuery = textnocasnopunc(categoryQuery);
            solrFilter = matchCategoryIdQuery;
        }

        SolrQuery query = new SolrQuery();
        query.setQuery(solrQuery);
        query.addFilterQuery(solrFilter);
        query.setFields(SOLR_DOCREFERENCE_FIELD);
        query.setRows(pageSize);
        query.setStart(offset);
        return query;
    }
}
