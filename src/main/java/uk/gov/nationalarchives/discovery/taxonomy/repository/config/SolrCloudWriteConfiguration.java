package uk.gov.nationalarchives.discovery.taxonomy.repository.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by jcharlet on 8/1/16.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "solr.cloud.write")
public class SolrCloudWriteConfiguration {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * bean to make query and update requests to solr server
     *
     * @return the solrServer bean
     */
    public
    @Bean
    SolrClient solrCloudWriteServer() {
        logger.info("Solr Cloud Write: {}", host);

        SolrClient server = new HttpSolrClient(host);
        return server;
    }
}
