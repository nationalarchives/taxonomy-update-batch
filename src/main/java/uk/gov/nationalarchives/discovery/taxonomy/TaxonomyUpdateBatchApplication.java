package uk.gov.nationalarchives.discovery.taxonomy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("application.yml")
public class TaxonomyUpdateBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaxonomyUpdateBatchApplication.class, args);
	}
}
