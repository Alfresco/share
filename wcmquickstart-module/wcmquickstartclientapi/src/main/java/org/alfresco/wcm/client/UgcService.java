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
