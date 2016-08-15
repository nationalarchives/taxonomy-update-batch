package uk.gov.nationalarchives.discovery.taxonomy.service.command;

import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.Category;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.SearchQueryResultsWithCursor;
import uk.gov.nationalarchives.discovery.taxonomy.repository.InformationAssetViewReadRepository;
import uk.gov.nationalarchives.discovery.taxonomy.repository.UpdateRepository;
import uk.gov.nationalarchives.discovery.taxonomy.service.ProcessMessageService;

import java.util.List;

/**
 * Created by jcharlet on 8/15/16.
 */
public class AddCategoryToUncategorisedIAViewsCommand extends AbstractUpdateIAViewsCommand {

    private ProcessMessageService processMessageService;

    public AddCategoryToUncategorisedIAViewsCommand(ProcessMessageService processMessageService, UpdateRepository updateRepository, InformationAssetViewReadRepository iaViewReadRepository) {
        super(updateRepository, iaViewReadRepository, "add");
        this.processMessageService = processMessageService;
    }

    @Override
    public int countItemsToUpdate(Category category) {
        return iaViewReadRepository.countItemsMatchingQueryAndWithoutCategory(category.getQry(), category
                .getCiaid(), category.getSc());
    }

    @Override
    public SearchQueryResultsWithCursor searchItemsToUpdate(Category category, String lastCursorMark, int pageSize) {
        return iaViewReadRepository.searchItemsMatchingQueryAndWithoutCategory(category.getQry(),
                category.getCiaid(), hasThreshold(category.getSc()), lastCursorMark, pageSize);
    }

    @Override
    public void updateItems(Category category, List<String> iaids) {
        updateRepository.addCategoryToIaids(category, iaids);
    }
}
