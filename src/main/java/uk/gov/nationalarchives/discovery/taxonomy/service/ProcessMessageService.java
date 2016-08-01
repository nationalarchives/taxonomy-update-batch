package uk.gov.nationalarchives.discovery.taxonomy.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.Category;
import uk.gov.nationalarchives.discovery.taxonomy.repository.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.repository.InformationAssetViewReadRepository;
import uk.gov.nationalarchives.discovery.taxonomy.repository.UpdateRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcharlet on 8/1/16.
 */
@Component
public class ProcessMessageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CategoryRepository categoryRepository;
    private final InformationAssetViewReadRepository iaViewReadRepository;
    private final UpdateRepository updateRepository;
    public static final Integer PAGE_SIZE = 10;

    @Autowired
    public ProcessMessageService(CategoryRepository categoryRepository, InformationAssetViewReadRepository iaViewReadRepository, UpdateRepository updateRepository) {
        this.categoryRepository = categoryRepository;
        this.iaViewReadRepository = iaViewReadRepository;
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
    }

    private void removeCategoryFromCategorisedIAViews(Category category) {
        Integer offset = 0;
        Integer nbOfIaViews = iaViewReadRepository.countItemsNotMatchingQueryAndWithCategory(category.getQry(), category
                .getCiaid(), category.getSc());
        while (offset < nbOfIaViews) {
            List<String> iaids = iaViewReadRepository.searchItemsNotMatchingQueryAndWithCategory(category.getQry(),
                    category.getCiaid(), category.getSc() != null, offset, PAGE_SIZE);

            updateRepository.removeCategoryFromIaids(category, iaids);
            offset += PAGE_SIZE;
        }
    }

    private void addCategoryToUncategorisedIAViews(Category category) {
        Integer offset = 0;
        Integer nbOfIaViews = iaViewReadRepository.countItemsMatchingQueryAndWithoutCategory(category.getQry(), category
                .getCiaid(), category.getSc());
        while (offset < nbOfIaViews) {
            List<String> iaids = iaViewReadRepository.searchItemsMatchingQueryAndWithoutCategory(category.getQry(),
                    category.getCiaid(), category.getSc() != null, offset, PAGE_SIZE);

            updateRepository.addCategoryToIaids(category, iaids);
            offset += PAGE_SIZE;
        }
    }

    public List<String> categoriseDocuments(List<String> documents) {
        List<String> listOfCategoryIdsInError = new ArrayList<>();
        logger.info("Categorising documents {}", StringUtils.join(", ", documents));
        //TODO to implement
        return listOfCategoryIdsInError;
    }
}
