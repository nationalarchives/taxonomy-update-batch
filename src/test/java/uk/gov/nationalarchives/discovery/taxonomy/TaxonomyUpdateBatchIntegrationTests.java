package uk.gov.nationalarchives.discovery.taxonomy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TaxonomyUpdateBatchIntegrationTests {

    public static final String TEST_CATEGORY = "TEST";
    public static final String TEST_QUERY = "test";
    public static final String TEST_NEW_QUERY = "testing";
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Test
    public void contextLoads() {
    }

}
