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

import java.util.Date;

public interface VisitorFeedback
{
    String getId();
    Date getPostTime();
    String getAssetId();
    Asset getAsset();
    String getVisitorName();
    String getVisitorEmail();
    String getVisitorWebsite();
    String getComment();
    boolean isCommentFlagged();
    String getSubject();
    Integer getRating();
    String getFeedbackType();
    
    void setAssetId(String assetId);
    void setAsset(Asset asset);
    void setVisitorName(String name);
    void setVisitorEmail(String email);
    void setVisitorWebsite(String website);
    void setComment(String comment);
    void setCommentFlagged(boolean flagged);
    void setSubject(String subject);
    void setRating(Integer rating);
    void setFeedbackType(String type);
	String getSuccessPage();
}
