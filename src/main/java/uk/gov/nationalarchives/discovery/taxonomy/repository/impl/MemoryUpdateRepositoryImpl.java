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
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by jcharlet on 8/1/16.
 */
@Repository
public class MemoryUpdateRepositoryImpl implements UpdateRepository {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int QUEUE_MAX_SIZE = 10000;
    private static ConcurrentLinkedQueue<AtomicUpdate> updateQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void removeCategoryFromIaids(Category category, List<String> iaids) {
        waitForQueueToHaveAvailableSpace();
        for (String iaid : iaids) {
            updateQueue.add(new AtomicUpdate(iaid, category.getCiaid(),category.getTtl(), AtomicUpdateType.remove));
        }
    }

    @Override
    public void addCategoryToIaids(Category category, List<String> iaids) {
        waitForQueueToHaveAvailableSpace();
        for (String iaid : iaids) {
            updateQueue.add(new AtomicUpdate(iaid, category.getCiaid(),category.getTtl(), AtomicUpdateType.add));
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
        return updates;
    }

    //FIXME use BlockedQueue insteadQ
    private void waitForQueueToHaveAvailableSpace() {
        while (updateQueue.size() >= QUEUE_MAX_SIZE){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("an error occured while waiting to have available space",e);
            }
        }
    }

}
