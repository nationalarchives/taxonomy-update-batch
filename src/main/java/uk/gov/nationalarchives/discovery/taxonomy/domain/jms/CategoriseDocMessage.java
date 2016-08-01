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
public class CategoriseDocMessage {
    private String messageId;
    private List<String> listOfDocReferences;

    public CategoriseDocMessage(String messageId, List<String> listOfDocReferences) {
	super();
	this.messageId = messageId;
	this.listOfDocReferences = listOfDocReferences;
    }

    public String getMessageId() {
	return messageId;
    }

    public void setMessageId(String messageId) {
	this.messageId = messageId;
    }

    public List<String> getListOfDocReferences() {
	return listOfDocReferences;
    }

    public void setListOfDocReferences(List<String> listOfDocReferences) {
	this.listOfDocReferences = listOfDocReferences;
    }

}