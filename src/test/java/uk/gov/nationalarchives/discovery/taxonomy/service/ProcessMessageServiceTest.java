package uk.gov.nationalarchives.discovery.taxonomy.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.Category;
import uk.gov.nationalarchives.discovery.taxonomy.repository.CategoryRepository;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jcharlet on 8/1/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ProcessMessageServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testPublishCategory() {
        //given my service set up with mocks
        ProcessMessageService processMessageService = new ProcessMessageService(categoryRepository);

        Category category = new Category();
        category.setTtl("test category");
        when(categoryRepository.findByCiaid(anyString())).thenReturn(category);

        //when I publish a category
        String categoryId = "C00001";
        processMessageService.publishCategory(categoryId);

        //then I retrieve the category from Mongo
        verify(categoryRepository).findByCiaid(Matchers.eq(categoryId));
    }
}
