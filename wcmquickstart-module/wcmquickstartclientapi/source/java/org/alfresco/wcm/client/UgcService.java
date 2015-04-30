/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.wcm.client;

public interface UgcService
{
    public final static String COMMENT_TYPE = "Comment";
    public final static String CONTACT_REQUEST_TYPE = "Contact Request";
    
    String postFeedback(String assetId, String visitorNickname, String visitorEmailAddress,
            String visitorWebsite, String comment);

    String postFeedback(String assetId, String visitorNickname, String visitorEmailAddress,
            String visitorWebsite, String feedbackType, String subject, String comment, int rating);

    String postFeedback(VisitorFeedback feedback);

    VisitorFeedback createFeedback();

    VisitorFeedbackPage getFeedbackPage(String assetId, int itemsToFetch, long itemsToSkip);

	void reportFeedback(String feedbackId);
	
	String getFormId();
	
	boolean validateFormId(String formId);
}
