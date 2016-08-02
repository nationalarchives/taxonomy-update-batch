package uk.gov.nationalarchives.discovery.taxonomy.repository.impl;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.AtomicUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.AtomicUpdateType;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.repository.InformationAssetViewWriteRepository;

import java.io.IOException;
import java.util.*;

/**
 * Created by jcharlet on 8/2/16.
 */
@Repository
public class SolrInformationAssetViewWriteRepositoryImpl implements InformationAssetViewWriteRepository {
    private final SolrClient solrCloudWriteServer;

    @Value("${solr.cloud.write.commit-within}")
    Integer timeInMs;

    @Autowired
    public SolrInformationAssetViewWriteRepositoryImpl(SolrClient solrCloudWriteServer) {
        this.solrCloudWriteServer = solrCloudWriteServer;
    }

    @Override
    public void bulkUpdate(List<AtomicUpdate> listOfUpdates) {
        List<SolrInputDocument> solrInputDocuments = new ArrayList<>();
        for (AtomicUpdate update : listOfUpdates) {
            SolrInputDocument documentForAtomicUpdate = createDocumentForAtomicUpdate(
                    update.getIaid(), update.getCategoryId(), update.getCategoryTitle(), update.getType());
            solrInputDocuments.add(documentForAtomicUpdate);
        }

        try {
            solrCloudWriteServer.add(solrInputDocuments, timeInMs);
        } catch (SolrServerException | IOException e) {
            throw new TaxonomyException(TaxonomyErrorType.SOLR_WRITE_EXCEPTION, e);
        }
    }

    private SolrInputDocument createDocumentForAtomicUpdate(String docReference, String categoryId, String
            categoryTitle, AtomicUpdateType type) {
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.addField(InformationAssetViewFields.DOCREFERENCE.toString(), docReference);

        Map<String, List<String>> partialUpdateOnTaxonomyTitle = new HashMap<>();
        partialUpdateOnTaxonomyTitle.put(type.name(), Arrays.asList(categoryTitle));
        solrInputDocument.addField(InformationAssetViewFields.TAXONOMY.toString(), partialUpdateOnTaxonomyTitle);

        Map<String, List<String>> partialUpdateOnTaxonomyId = new HashMap<>();
        partialUpdateOnTaxonomyId.put(type.name(), Arrays.asList(categoryId));
        solrInputDocument.addField(InformationAssetViewFields.TAXONOMYID.toString(), partialUpdateOnTaxonomyId);
        return solrInputDocument;
    }
}
