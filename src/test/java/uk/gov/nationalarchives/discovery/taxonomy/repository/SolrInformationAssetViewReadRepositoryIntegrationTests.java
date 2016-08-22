package uk.gov.nationalarchives.discovery.taxonomy.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.Category;
import uk.gov.nationalarchives.discovery.taxonomy.service.ProcessMessageService;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SolrInformationAssetViewReadRepositoryIntegrationTests {

    public static final String TEST_CATEGORY = "TEST";
    public static final String TEST_QUERY = "test";
    public static final String TEST_NEW_QUERY = "testing";
    private static boolean setUpIsDone = false;
    @Autowired
    InformationAssetViewReadRepository informationAssetViewReadRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProcessMessageService processMessageService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void initTestCagegory() {
        if (setUpIsDone) {
            return;
        }
        Category testCategory = new Category();
        testCategory.set_id(TEST_CATEGORY);
        testCategory.setCiaid(TEST_CATEGORY);
        testCategory.setTtl(TEST_CATEGORY);
        testCategory.setQry(TEST_QUERY);
        testCategory.setSc(0.0);
        categoryRepository.save(testCategory);

        processMessageService.publishCategory(testCategory.getCiaid());
        setUpIsDone = true;
    }

    @Test
    public void testCountItemsToCategoriseWithNewQuery() {
        //GIVEN test category
        String categoryId = TEST_CATEGORY;
        //and new category query (no threshold)
        String categoryQuery = TEST_NEW_QUERY;
        double queryThreshold = 0.0;

        //WHEN I count items that match category but have not it
        Integer nbOfItemsToUpdate = informationAssetViewReadRepository.countItemsMatchingQueryAndWithoutCategory(
                categoryQuery, categoryId, queryThreshold);

        //THEN the nb of items is as expected
        logger.info("{} items found", nbOfItemsToUpdate);
        assertThat(nbOfItemsToUpdate).isBetween(12000, 13000);
    }


    @Test
    public void testCountItemsToUncategoriseWithCategory() {
        //GIVEN test category
        String categoryId = TEST_CATEGORY;
        //and new category query
        String categoryQuery = TEST_NEW_QUERY;

        //WHEN I count items that match category but have not it
        Integer nbOfItemsToUpdate = informationAssetViewReadRepository.countItemsNotMatchingQueryAndWithCategory(
                categoryQuery, categoryId);

        //THEN the nb of items is as expected
        logger.info("testCountItemsToUncategoriseWithCategory: {} items found", nbOfItemsToUpdate);
        assertThat(nbOfItemsToUpdate).isBetween(23000, 26000);
    }


    @Test
    public void testCountItemsToCategoriseWithoutCategoryAboveThreshold() {
        //GIVEN test category
        String categoryId = TEST_CATEGORY;
        //and new category query and threshold
        String categoryQuery = TEST_NEW_QUERY;
        double queryThreshold = 1.8;

        //WHEN I count items that match category but have not it
        Integer nbOfItemsToUpdate = informationAssetViewReadRepository.countItemsMatchingQueryAndWithoutCategory(
                categoryQuery, categoryId, queryThreshold);

        //THEN the nb of items is as expected
        logger.info("testCountItemsToCategoriseWithoutCategoryAboveThreshold: {} items found", nbOfItemsToUpdate);
        assertThat(nbOfItemsToUpdate).isBetween(500, 2000);
    }

    @Test
    public void testCountItemsToCategoriseWithThreshold() {
        //GIVEN test category with its query
        String categoryId = TEST_CATEGORY;
        String categoryQuery = TEST_QUERY;
        //and new threshold
        double queryThreshold = 1.8;

        //WHEN I count items that match category but have not it
        Integer nbOfItemsToUpdate = informationAssetViewReadRepository.countItemsMatchingQueryAndWithoutCategory(
                categoryQuery, categoryId, queryThreshold);

        //THEN the nb of items is as expected
        logger.info("{} items found", nbOfItemsToUpdate);
        assertThat(nbOfItemsToUpdate).isEqualTo(0);
    }


    @Test
    public void testCountItemsToUnCategoriseWithCategoryBelowThreshold() {
        //GIVEN test category with its query
        String categoryId = TEST_CATEGORY;
        String categoryQuery = TEST_QUERY;
        //and new threshold
        double queryThreshold = 1.8;

        //WHEN I count items that match category, have the category, but are below threshold
        Integer nbOfItemsToUpdate = informationAssetViewReadRepository.countItemsMatchingQueryBelowThresholdAndWithCategory(
                categoryQuery, categoryId, queryThreshold);

        //THEN the nb of items is as expected
        logger.info("testCountItemsToCategoriseWithoutCategoryAboveThreshold: {} items found", nbOfItemsToUpdate);
        assertThat(nbOfItemsToUpdate).isBetween(24000, 26000);
    }
}
