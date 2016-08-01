package uk.gov.nationalarchives.discovery.taxonomy.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.Category;
import uk.gov.nationalarchives.discovery.taxonomy.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcharlet on 8/1/16.
 */
@Component
public class ProcessMessageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProcessMessageService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void publishCategory(String categoryId){
        Category category = categoryRepository.findByCiaid(categoryId);
        logger.info("Publishing category {}", category.getTtl());
    }

    public List<String> categoriseDocuments(List<String> documents){
        List<String> listOfCategoryIdsInError = new ArrayList<>();
        logger.info("Categorising documents {}", StringUtils.join(", ", documents));
        return listOfCategoryIdsInError;
    }
}
