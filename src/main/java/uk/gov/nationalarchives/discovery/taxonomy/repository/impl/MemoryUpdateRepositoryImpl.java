package uk.gov.nationalarchives.discovery.taxonomy.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.AtomicUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.AtomicUpdateType;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.Category;
import uk.gov.nationalarchives.discovery.taxonomy.repository.UpdateRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by jcharlet on 8/1/16.
 */
@Repository
public class MemoryUpdateRepositoryImpl implements UpdateRepository {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int QUEUE_MAX_SIZE = 10000;
    private static BlockingQueue<AtomicUpdate> updateQueue = new LinkedBlockingDeque<>(QUEUE_MAX_SIZE);

    @Override
    public void removeCategoryFromIaids(Category category, List<String> iaids) {
        for (String iaid : iaids) {
            try {
                updateQueue.put(new AtomicUpdate(iaid, category.getCiaid(), category.getCiaid() + " " + category.getTtl(),
                        AtomicUpdateType.remove));
            } catch (InterruptedException e) {
                logger.error("error while adding category to remove",e);
            }
        }
    }

    @Override
    public void addCategoryToIaids(Category category, List<String> iaids) {
        logger.debug("adding {} updates", iaids.size());
        for (String iaid : iaids) {
            try {
                updateQueue.put(new AtomicUpdate(iaid, category.getCiaid(), category.getCiaid() + " " + category.getTtl(),
                        AtomicUpdateType.add));
            } catch (InterruptedException e) {
                logger.error("error while adding category to remove", e);
            }
        }
    }

    @Override
    public List<AtomicUpdate> getLastUpdates(Integer nbOfItems) {
        List<AtomicUpdate> updates = new ArrayList<>();
        int nbOfItemsPolled=0;
        while(!updateQueue.isEmpty() && nbOfItemsPolled<nbOfItems){
            updates.add(updateQueue.poll());
            nbOfItemsPolled++;
        }
        if(updates.size()!=0){
            logger.debug("retrieving {} updates", updates.size());
        }
        return updates;
    }

}
