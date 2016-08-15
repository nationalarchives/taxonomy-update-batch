package uk.gov.nationalarchives.discovery.taxonomy.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.Category;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.SearchQueryResultsWithCursor;
import uk.gov.nationalarchives.discovery.taxonomy.domain.service.ProgressInformation;
import uk.gov.nationalarchives.discovery.taxonomy.repository.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.repository.InformationAssetViewReadRepository;
import uk.gov.nationalarchives.discovery.taxonomy.repository.InformationAssetViewWriteRepository;
import uk.gov.nationalarchives.discovery.taxonomy.repository.UpdateRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcharlet on 8/1/16.
 */
@Service
public class ProcessMessageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CategoryRepository categoryRepository;
    private final InformationAssetViewReadRepository iaViewReadRepository;
    private final InformationAssetViewWriteRepository informationAssetViewWriteRepository;
    private final UpdateRepository updateRepository;
    @Value("${solr.cloud.read.query-page-size}")
    public Integer pageSize;


    @Autowired
    public ProcessMessageService(CategoryRepository categoryRepository, InformationAssetViewReadRepository iaViewReadRepository, InformationAssetViewWriteRepository informationAssetViewWriteRepository, UpdateRepository updateRepository) {
        this.categoryRepository = categoryRepository;
        this.iaViewReadRepository = iaViewReadRepository;
        this.informationAssetViewWriteRepository = informationAssetViewWriteRepository;
        this.updateRepository = updateRepository;
    }

    public void publishCategory(String categoryId) {
        Category category = categoryRepository.findByCiaid(categoryId);
        if (category == null) {
            throw new TaxonomyException(TaxonomyErrorType.CAT_NOT_FOUND,
                    new StringBuilder("category with id ").append(categoryId).append(" was not found in database")
                            .toString());
        }
        logger.info("Publishing category {}", category.getTtl());
        addCategoryToUncategorisedIAViews(category);
        removeCategoryFromCategorisedIAViews(category);

        //FIXME since queries and updates are bounded, I could run updates as async tasks, rather than have a
        // scheduled task, would make things look simpler
        try {
            while (updateRepository.hasPendingUpdates()) {
                Thread.sleep(1000);
            }
            Thread.sleep(2000);
            informationAssetViewWriteRepository.commit();
        } catch (InterruptedException e) {
            logger.error("process was interrupted", e);
        }
    }

    private void removeCategoryFromCategorisedIAViews(Category category) {
        Integer nbOfIaViews = iaViewReadRepository.countItemsNotMatchingQueryAndWithCategory(category.getQry(), category
                .getCiaid(), category.getSc());
        if (nbOfIaViews == 0) {
            logger.info("category '{}' has no IAVIews to remove", category.getTtl());
            return;
        }
        logger.info("removing category '{}' to {} IAVIews", category.getTtl(), nbOfIaViews);

        ProgressInformation progressInformation = new ProgressInformation(nbOfIaViews);

        String lastCursorMark = null;
        while (true) {
            logger.debug("processed {} IAVIews, cursor:{}", progressInformation.getNbOfProcessedItems(), lastCursorMark);
            SearchQueryResultsWithCursor searchQueryResultsWithCursor = iaViewReadRepository.searchItemsNotMatchingQueryAndWithCategory(category.getQry(),
                    category.getCiaid(), hasThreshold(category.getSc()), lastCursorMark, pageSize);

            if (searchQueryResultsWithCursor.getCursor().equals(lastCursorMark)) {
                logger.debug("completed updates, updated {}", progressInformation.getNbOfProcessedItems());
                break;
            }

            List<String> iaids = searchQueryResultsWithCursor.getResults();
            lastCursorMark = searchQueryResultsWithCursor.getCursor();

            updateRepository.removeCategoryFromIaids(category, iaids);

            updateAndLogProgress(progressInformation, iaids.size());
        }

        logger.info("finished removing category '{}'", category.getTtl());
    }

    private boolean hasThreshold(Double score) {
        return !(new Double(0).equals(score));
    }

    private void updateAndLogProgress(ProgressInformation progressInformation, int newNbOfProcessedItems) {
        progressInformation.addToNbOfProcessedItems(newNbOfProcessedItems);
        int percentageOfProcessedItems = progressInformation.getNbOfProcessedItems() * 100 / progressInformation.getTotalNbOfIaViews();
        if (percentageOfProcessedItems > (progressInformation.getPercentageOfProcessedItems() + 10) && percentageOfProcessedItems < 100) {
            logger.info("processed {} % so far", percentageOfProcessedItems);
            progressInformation.setPercentageOfProcessedItems((percentageOfProcessedItems / 10) * 10);
        }
    }

    //FIXME duplicated code with removeCategoryFromCategorisedIAViews
    private void addCategoryToUncategorisedIAViews(Category category) {
        Integer nbOfIaViews = iaViewReadRepository.countItemsMatchingQueryAndWithoutCategory(category.getQry(), category
                .getCiaid(), category.getSc());
        if (nbOfIaViews == 0) {
            logger.info("category '{}' has no IAVIews to add", category.getTtl());
            return;
        }
        logger.info("adding category '{}' to {} IAVIews", category.getTtl(), nbOfIaViews);

        ProgressInformation progressInformation = new ProgressInformation(nbOfIaViews);

        String lastCursorMark = null;
        while (true) {
            logger.debug("processed {} IAVIews, cursor:{}", progressInformation.getNbOfProcessedItems(), lastCursorMark);
            SearchQueryResultsWithCursor searchQueryResultsWithCursor = iaViewReadRepository.searchItemsMatchingQueryAndWithoutCategory(category.getQry(),
                    category.getCiaid(), hasThreshold(category.getSc()), lastCursorMark, pageSize);

            if (searchQueryResultsWithCursor.getCursor().equals(lastCursorMark)) {
                logger.debug("completed updates, updated {}", progressInformation.getNbOfProcessedItems());
                break;
            }

            List<String> iaids = searchQueryResultsWithCursor.getResults();
            lastCursorMark = searchQueryResultsWithCursor.getCursor();

            updateRepository.addCategoryToIaids(category, iaids);

            updateAndLogProgress(progressInformation, iaids.size());
        }

        logger.info("finished adding category '{}'", category.getTtl());
    }

    public List<String> categoriseDocuments(List<String> documents) {
        List<String> listOfCategoryIdsInError = new ArrayList<>();
        logger.info("Categorising documents {}", StringUtils.join(", ", documents));
        //TODO implement categoriseDocuments
        return listOfCategoryIdsInError;
    }
}
