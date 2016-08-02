package uk.gov.nationalarchives.discovery.taxonomy.repository;

import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.AtomicUpdate;

import java.util.List;

/**
 * Created by jcharlet on 8/1/16.
 */
public interface InformationAssetViewWriteRepository {
    void bulkUpdate(List<AtomicUpdate> listOfUpdates);
}
