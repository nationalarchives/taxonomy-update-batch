/**
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk
 * <p>
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.AtomicUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.repository.InformationAssetViewWriteRepository;
import uk.gov.nationalarchives.discovery.taxonomy.repository.UpdateRepository;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class UpdateSolrCloudScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(UpdateSolrCloudScheduledTask.class);

    private final UpdateRepository updateRepository;
    private final InformationAssetViewWriteRepository informationAssetViewWriteRepository;

    @Value("${scheduler.update-solr.bulk-update-size}")
    Integer bulkUpdateSize;


    @Autowired
    public UpdateSolrCloudScheduledTask(UpdateRepository updateRepository, InformationAssetViewWriteRepository informationAssetViewWriteRepository) {
        super();
        this.updateRepository = updateRepository;
        this.informationAssetViewWriteRepository = informationAssetViewWriteRepository;
    }

    @PostConstruct
    private void initBatch() {
    }

    @SuppressWarnings("unchecked")
    @Scheduled(fixedRateString = "${scheduler.update-solr.rate-between-updates}")
    public void updateSolrCloudWithLatestUpdatesOnCategories() {
        //FIXME when I try to shutdown the application (with intellij), it fails because of updates to Solr
        List<AtomicUpdate> listOfUpdates = updateRepository.getLastUpdates(bulkUpdateSize);
        if (!CollectionUtils.isEmpty(listOfUpdates)) {
            logger.info("updating {} documents",listOfUpdates.size());
            informationAssetViewWriteRepository.bulkUpdate(listOfUpdates);
        }
    }

}
