/**
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk
 * <p>
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.jms;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.domain.jms.PublishCategoriesMessage;
import uk.gov.nationalarchives.discovery.taxonomy.service.ProcessMessageService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Consumer dedicated to handling all Categorisation requests sent to activeMQ
 * dedicated queue
 *
 * @author jcharlet
 *
 */
@Component
public class PublishCategoriesMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PublishCategoriesMessageConsumer.class);
    private final ProcessMessageService processMessageService;

    @Autowired
    public PublishCategoriesMessageConsumer(ProcessMessageService processMessageService) {
        this.processMessageService = processMessageService;
    }

    public void handleMessage(Message message) {
        if (isTextMessageInvalid(message)) {
            logger.error("message is invalid and was not processed: {}", message.toString());
            return;
        }

        PublishCategoriesMessage publishCategoriesMessage = getPublishCategoryMessageFromMessage(message);

        logger.info("received Publish Categories message: {}, categories: {}",
                publishCategoriesMessage.getMessageId(),
                ArrayUtils.toString(publishCategoriesMessage.getListOfCategoryIds()));

        List<String> listOfCategoryIdsInError = new ArrayList<>();
        for (String categoryId : publishCategoriesMessage.getListOfCategoryIds()) {
            try{
                processMessageService.publishCategory(categoryId);
            }catch (TaxonomyException exeption){
                listOfCategoryIdsInError.add(categoryId);
            }
        }


        if (!CollectionUtils.isEmpty(listOfCategoryIdsInError)) {
            logger.warn("completed treatment for message: {} with {} errors", publishCategoriesMessage.getMessageId(),
                    listOfCategoryIdsInError.size());
            logger.error("CATEGORIES THAT COULD NOT BE PUBLISHED: {}",
                    Arrays.toString(listOfCategoryIdsInError.toArray()));
        } else {
            logger.info("completed treatment for message: {}", publishCategoriesMessage.getMessageId());
        }
    }


    protected boolean isTextMessageInvalid(Message message) {
        return !(message instanceof TextMessage);
    }

    protected PublishCategoriesMessage getPublishCategoryMessageFromMessage(Message message) {
        List<String> categories = getListOfCategoriesFromMessage((TextMessage) message);
        String jmsMessageId = getJMSMessageIdFromMessage(message);
        return new PublishCategoriesMessage(jmsMessageId,
                categories);
    }

    protected String getJMSMessageIdFromMessage(Message message) {
        String messageId;
        try {
            messageId = message.getJMSMessageID();
        } catch (JMSException e) {
            throw new TaxonomyException(TaxonomyErrorType.JMS_EXCEPTION, e);
        }
        return messageId;
    }

    protected List<String> getListOfCategoriesFromMessage(TextMessage message) {
        String listOfCategoriesString;
        try {
            listOfCategoriesString = message.getText();
        } catch (JMSException e) {
            throw new TaxonomyException(TaxonomyErrorType.JMS_EXCEPTION, e);
        }
        return Arrays.asList(listOfCategoriesString.split(";"));
    }

}