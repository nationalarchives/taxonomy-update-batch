package uk.gov.nationalarchives.discovery.taxonomy.service.command;

import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.Category;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.SearchQueryResultsWithCursor;
import uk.gov.nationalarchives.discovery.taxonomy.repository.InformationAssetViewReadRepository;
import uk.gov.nationalarchives.discovery.taxonomy.repository.UpdateRepository;

import java.util.List;

/**
 * Created by jcharlet on 8/15/16.
 */
public class RemoveCategoryFromCategorisedIAViewsCommand extends AbstractUpdateIAViewsCommand {

    public RemoveCategoryFromCategorisedIAViewsCommand(UpdateRepository updateRepository, InformationAssetViewReadRepository iaViewReadRepository) {
        super(updateRepository, iaViewReadRepository, "remove");
    }

    @Override
    public int countItemsToUpdate(Category category) {
        return iaViewReadRepository.countItemsNotMatchingQueryAndWithCategory(category.getQry(), category
                .getCiaid(), category.getSc());
    }

    @Override
    public SearchQueryResultsWithCursor searchItemsToUpdate(Category category, String lastCursorMark, int pageSize) {
        return iaViewReadRepository.searchItemsNotMatchingQueryAndWithCategory(category.getQry(),
                category.getCiaid(), hasThreshold(category.getSc()), lastCursorMark, pageSize);
    }

    @Override
    public void updateItems(Category category, List<String> iaids) {
        updateRepository.removeCategoryFromIaids(category, iaids);
    }
}
