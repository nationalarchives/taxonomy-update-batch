/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.domain.jms;

import java.util.List;

/**
 * Holder for taxonomy queue messages: stores messageId, list of elements to
 * process, list of elements in error
 * 
 * @author jcharlet
 *
 */
public class PublishCategoriesMessage {
    private String messageId;
    private List<String> listOfCategoryIds;

    public PublishCategoriesMessage(String messageId, List<String> listOfCategoryIds) {
        this.messageId = messageId;
        this.listOfCategoryIds = listOfCategoryIds;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public List<String> getListOfCategoryIds() {
        return listOfCategoryIds;
    }

    public void setListOfCategoryIds(List<String> listOfCategoryIds) {
        this.listOfCategoryIds = listOfCategoryIds;
    }
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PublishCategoriesMessage{");
        sb.append("messageId='").append(messageId).append('\'');
        sb.append(", listOfCategoryIds=").append(listOfCategoryIds);
        sb.append('}');
        return sb.toString();
    }
}