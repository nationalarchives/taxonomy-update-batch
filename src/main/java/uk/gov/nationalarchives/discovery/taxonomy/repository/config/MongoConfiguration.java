/**
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk
 * <p>
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.repository.config;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "mongo.categories")
@EnableConfigurationProperties
@Component
//TODO JCT ensure index when app starts
public class MongoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MongoConfiguration.class);

    private String hosts;
    private String ports;
    private String database;

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public String getPorts() {
        return ports;
    }

    public void setPorts(String ports) {
        this.ports = ports;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoClient client;
        client = mongoClient();

        MongoDbFactory categoriesMongoDbFactory = new SimpleMongoDbFactory(client, database);
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(categoriesMongoDbFactory),
                new MongoMappingContext());
        // remove _class
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return new MongoTemplate(categoriesMongoDbFactory, converter);
    }

    private MongoClient mongoClient() throws UnknownHostException {
        MongoClient client;
        String[] splitHosts = hosts.split(",");
        String[] splitPorts = ports.split(",");
        if (splitHosts.length > 1) {
            List<ServerAddress> listOfServerAddresses = new ArrayList<>();
            for (int i = 0; i < splitHosts.length; i++) {
                logger.info("mongo categories database: {}:{}/{}", splitHosts[i], splitPorts[i], database);
                listOfServerAddresses.add(new ServerAddress(splitHosts[i], Integer.valueOf(splitPorts[i])));
            }
            client = new MongoClient(listOfServerAddresses);
        } else {
            client = new MongoClient(hosts,
                    Integer.valueOf(ports));
        }
        return client;
    }
}