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
