package uk.gov.nationalarchives.discovery.taxonomy.service.command;

import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.Category;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.SearchQueryResultsWithCursor;
import uk.gov.nationalarchives.discovery.taxonomy.repository.InformationAssetViewReadRepository;
import uk.gov.nationalarchives.discovery.taxonomy.repository.UpdateRepository;

import java.util.List;

/**
 * Created by jcharlet on 8/15/16.
 */
public abstract class AbstractUpdateIAViewsCommand {
    protected final UpdateRepository updateRepository;
    protected final InformationAssetViewReadRepository iaViewReadRepository;
    protected final String commandName;

    protected AbstractUpdateIAViewsCommand(UpdateRepository updateRepository, InformationAssetViewReadRepository iaViewReadRepository, String commandName) {
        this.updateRepository = updateRepository;
        this.iaViewReadRepository = iaViewReadRepository;
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public abstract int countItemsToUpdate(Category category);

    public abstract SearchQueryResultsWithCursor searchItemsToUpdate(Category category, String lastCursorMark, int pageSize);

    public abstract void updateItems(Category category, List<String> iaids);
}
