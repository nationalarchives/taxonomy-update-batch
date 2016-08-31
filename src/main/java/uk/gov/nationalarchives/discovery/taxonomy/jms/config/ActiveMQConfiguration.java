/**
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk
 * <p>
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.jms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import uk.gov.nationalarchives.discovery.taxonomy.jms.CategoriseDocMessageConsumer;
import uk.gov.nationalarchives.discovery.taxonomy.jms.PublishCategoriesMessageConsumer;

import javax.jms.ConnectionFactory;

/**
 * Configuration dedicated to the messaging service (Active MQ)
 *
 * @author jcharlet
 *
 */
@Configuration
class ActiveMQConfiguration {

    @Value("${spring.activemq.categorise-doc-queue-name}")
    String categoriseDocumentsQueueName;
    @Value("${spring.activemq.publish-categories-queue-name}")
    String publishCategoriesQueueName;

    @Bean
    MessageListenerAdapter categorisationListenerAdapter(CategoriseDocMessageConsumer categoriseDocMessageConsumer) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(categoriseDocMessageConsumer);
        messageListenerAdapter.setMessageConverter(null);
        return messageListenerAdapter;
    }

    //FIXME categorisationContainer prevents the application to shut down quickly, it stays alive until there are no
    // documents to categorise/categories to publish
    @Bean
    DefaultMessageListenerContainer categorisationContainer(MessageListenerAdapter categorisationListenerAdapter,
                                                            ConnectionFactory connectionFactory) {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setMessageListener(categorisationListenerAdapter);
        container.setConnectionFactory(connectionFactory);
        container.setDestinationName(categoriseDocumentsQueueName);
        return container;
    }

    @Bean
    MessageListenerAdapter publishCategoriesListenerAdapter(PublishCategoriesMessageConsumer publishCategoriesMessageConsumer) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(publishCategoriesMessageConsumer);
        messageListenerAdapter.setMessageConverter(null);
        return messageListenerAdapter;
    }

    @Bean
    DefaultMessageListenerContainer publishCategoriesContainer(MessageListenerAdapter publishCategoriesListenerAdapter,
                                                            ConnectionFactory connectionFactory) {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setMessageListener(publishCategoriesListenerAdapter);
        container.setConnectionFactory(connectionFactory);
        container.setDestinationName(publishCategoriesQueueName);
        return container;
    }

}
