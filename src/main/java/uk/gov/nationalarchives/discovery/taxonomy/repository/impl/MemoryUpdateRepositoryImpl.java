package uk.gov.nationalarchives.discovery.taxonomy.repository.impl;

import org.springframework.stereotype.Component;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.AtomicUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.AtomicUpdateType;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.Category;
import uk.gov.nationalarchives.discovery.taxonomy.repository.UpdateRepository;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by jcharlet on 8/1/16.
 */
@Component
public class MemoryUpdateRepositoryImpl implements UpdateRepository {

    private static final int QUEUE_MAX_SIZE = 10000;
    private static ConcurrentLinkedQueue<AtomicUpdate> updateQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void removeCategoryFromIaids(Category category, List<String> iaids) {
        waitForQueueToHaveAvailableSpace();
        for (String iaid : iaids) {
            updateQueue.add(new AtomicUpdate(iaid, category.getCiaid(),category.getTtl(), AtomicUpdateType.REMOVE));
        }
    }

    @Override
    public void addCategoryToIaids(Category category, List<String> iaids) {
        waitForQueueToHaveAvailableSpace();
        for (String iaid : iaids) {
            updateQueue.add(new AtomicUpdate(iaid, category.getCiaid(),category.getTtl(), AtomicUpdateType.ADD));
        }
    }

    private void waitForQueueToHaveAvailableSpace() {
        while (updateQueue.size() >= QUEUE_MAX_SIZE){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}