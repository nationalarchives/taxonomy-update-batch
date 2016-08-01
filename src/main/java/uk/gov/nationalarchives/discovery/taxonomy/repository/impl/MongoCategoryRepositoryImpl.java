/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 * 
 */
package uk.gov.nationalarchives.discovery.taxonomy.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import uk.gov.nationalarchives.discovery.taxonomy.domain.repository.Category;
import uk.gov.nationalarchives.discovery.taxonomy.repository.CategoryRepository;

/**
 * Implementation of CategoryRepository using MongoTemplate<br/>
 * The reason is that Spring does not allow to use separate database names or
 * locations while the categories collection is on a mongo database separate
 * from other collections. So we cannot use MongoRepositories here and have to
 * rewrite everything
 * 
 * @author jcharlet
 *
 */
@Repository
public class MongoCategoryRepositoryImpl implements CategoryRepository {

    private static final String TTL_FIELD = "ttl";
    private static final String CIAID_FIELD = "ciaid";
    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoCategoryRepositoryImpl(MongoTemplate mongoTemplate) {
	super();
	this.mongoTemplate = mongoTemplate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.
     * CategoryRepository#findByCiaid(java.lang.String)
     */
    @Override
    public Category findByCiaid(String ciaid) {
	return mongoTemplate.findOne(new Query(Criteria.where(CIAID_FIELD).is(ciaid)), Category.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.
     * CategoryRepository#findByTtl(java.lang.String)
     */
    @Override
    public Category findByTtl(String ttl) {
	return mongoTemplate.findOne(new Query(Criteria.where(TTL_FIELD).is(ttl)), Category.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.
     * CategoryRepository#count()
     */
    @Override
    public Long count() {
	return mongoTemplate.count(new Query(), Category.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.
     * CategoryRepository#findAll()
     */
    @Override
    public Iterable<Category> findAll() {
	return mongoTemplate.findAll(Category.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.
     * CategoryRepository
     * #save(uk.gov.nationalarchives.discovery.taxonomy.common.
     * domain.repository.mongo.Category)
     */
    @Override
    public void save(Category category) {
	mongoTemplate.save(category);
    }

}
