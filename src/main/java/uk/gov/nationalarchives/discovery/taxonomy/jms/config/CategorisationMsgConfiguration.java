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

import javax.jms.ConnectionFactory;

/**
 * Configuration dedicated to the messaging service (Active MQ)
 *
 * @author jcharlet
 *
 */
@Configuration
class CategorisationMsgConfiguration {

    @Value("${spring.activemq.categorise-doc-queue-name}")
    String categoriseDocumentsQueueName;

    @Bean
    MessageListenerAdapter categorisationListenerAdapter(CategoriseDocMessageConsumer categoriseDocMessageConsumer) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(categoriseDocMessageConsumer);
        messageListenerAdapter.setMessageConverter(null);
        return messageListenerAdapter;
    }

    @Bean
    DefaultMessageListenerContainer categorisationContainer(MessageListenerAdapter categorisationListenerAdapter,
                                                            ConnectionFactory connectionFactory) {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setMessageListener(categorisationListenerAdapter);
        container.setConnectionFactory(connectionFactory);
        container.setDestinationName(categoriseDocumentsQueueName);
        return container;
    }

}
