/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client.impl;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.UgcService;
import org.alfresco.wcm.client.VisitorFeedback;
import org.alfresco.wcm.client.VisitorFeedbackPage;
import org.alfresco.wcm.client.impl.cache.SimpleCache;
import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyBooleanImpl;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.chemistry.opencmis.commons.spi.ObjectService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UgcServiceCmisImpl implements UgcService
{
    private static Log log = LogFactory.getLog(UgcServiceCmisImpl.class);

    private static String PROP_FEEDBACK_TYPE = "ws:feedbackType";
    private static String PROP_SUBJECT = "ws:feedbackSubject";
    private static String PROP_COMMENT = "ws:feedbackComment";
    private static String PROP_EMAIL = "ws:visitorEmail";
    private static String PROP_WEBSITE = "ws:visitorWebsite";
    private static String PROP_NAME = "ws:visitorName";
    private static String PROP_RATING = "ws:rating";
    private static String PROP_ARTICLE = "ws:relevantAssetRef";
    private static String PROP_COMMENT_FLAGGED = "ws:commentFlagged";

    private final static String COMMON_ASSET_SELECT_CLAUSE = "SELECT d.cmis:objectId, d.cmis:creationDate, " +
    		"f.ws:feedbackType, f.ws:feedbackSubject, f.ws:feedbackComment, " +
            "f.ws:visitorName, f.ws:visitorEmail, f.ws:visitorWebsite, f.ws:rating, f.ws:relevantAssetRef, f.ws:commentFlagged ";

    private final static String COMMON_ASSET_FROM_CLAUSE = "FROM cmis:document AS d "
            + "JOIN ws:visitorFeedback AS f ON d.cmis:objectId = f.cmis:objectId ";

    private final String feedbackByAssetIdQueryPattern = COMMON_ASSET_SELECT_CLAUSE
            + COMMON_ASSET_FROM_CLAUSE + "WHERE f.ws:relevantAssetRef = ''{0}''";

    private AssetFactory assetFactory;
    private SimpleCache<String, String> formIdCache;
    private ObjectId feedbackFolderId;

    public UgcServiceCmisImpl(ObjectId feedbackFolderId)
    {
        super();
        this.feedbackFolderId = feedbackFolderId;
    }

    public void setFormIdCache(SimpleCache<String, String> formIdCache)
    {
        this.formIdCache = formIdCache;
    }

    @Override
    public VisitorFeedback createFeedback()
    {
        VisitorFeedbackImpl feedback = new VisitorFeedbackImpl();
        feedback.setAssetFactory(assetFactory);
        return feedback;
    }

    @Override
    public VisitorFeedbackPage getFeedbackPage(String assetId, int itemsToFetch, long itemsToSkip)
    {
        ItemIterable<QueryResult> results = runQuery(MessageFormat.format(feedbackByAssetIdQueryPattern,
                assetId));
        ItemIterable<QueryResult> page = results.skipTo(itemsToSkip).getPage(itemsToFetch);
        List<VisitorFeedback> foundFeedback = new ArrayList<VisitorFeedback>((int) page.getPageNumItems());
        for (QueryResult queryResult : page)
        {
            foundFeedback.add(buildFeedbackObject(queryResult));
        }
        VisitorFeedbackPage feedbackPage = new VisitorFeedbackPageImpl(foundFeedback, itemsToSkip, 
                results.getTotalNumItems());
        return feedbackPage;
    }

    @Override
    public String postFeedback(VisitorFeedback feedback)
    {
        Map<String, Object> props = new TreeMap<String, Object>();
        props.put(PropertyIds.OBJECT_TYPE_ID, "D:ws:visitorFeedback");
        props.put(PropertyIds.NAME, new SimpleDateFormat("yyyy-MM-dd'_'HHmmssZ").format(new Date()) + "_"
                + feedback.getVisitorName());
        props.put(PROP_ARTICLE, feedback.getAssetId());
        props.put(PROP_FEEDBACK_TYPE, feedback.getFeedbackType());
        props.put(PROP_SUBJECT, feedback.getSubject());
        props.put(PROP_COMMENT, feedback.getComment());
        props.put(PROP_EMAIL, feedback.getVisitorEmail());
        props.put(PROP_NAME, feedback.getVisitorName());
        props.put(PROP_WEBSITE, feedback.getVisitorWebsite());
        if (feedback.getRating() != null)
        {
            props.put(PROP_RATING, feedback.getRating());
        }

        ObjectId id = CmisSessionHelper.getSession().createDocument(props, feedbackFolderId, null,
                VersioningState.MINOR, null, null, null);
        return id.getId();
    }
    
    @Override
    public void reportFeedback(String feedbackId)
    {
    	// Get the session, repository id and object service
    	Session session = CmisSessionHelper.getSession();
        String repoId = session.getRepositoryInfo().getId();
        ObjectService objectService = session.getBinding().getObjectService();        

        // Create a comment flagged property with value true
        PropertiesImpl props = new PropertiesImpl();
        props.addProperty(new PropertyBooleanImpl(PROP_COMMENT_FLAGGED, true));
               
        // Update the comment flagged property in the repository
        objectService.updateProperties(repoId, new Holder<String>(feedbackId), null, props, null);
    }    

    @Override
    public String postFeedback(String assetId, String visitorName, String visitorEmailAddress,
            String visitorWebsite, String comment)
    {
        VisitorFeedback feedback = buildFeedbackObject(assetId, visitorName, visitorEmailAddress,
                visitorWebsite, COMMENT_TYPE, null, comment, false, null);
        return postFeedback(feedback);
    }

    @Override
    public String postFeedback(String assetId, String visitorName, String visitorEmailAddress,
            String visitorWebsite, String type, String subject, String comment, int rating)
    {
        VisitorFeedback feedback = buildFeedbackObject(assetId, visitorName, visitorEmailAddress,
                visitorWebsite, type, subject, comment, false, rating);
        return postFeedback(feedback);
    }

    @Override
    public String getFormId()
    {
        String id = UUID.randomUUID().toString();
        formIdCache.put(id, id);
        return id;
    }

    @Override
    public boolean validateFormId(String formId)
    {
        boolean isValid = false;
        if (formIdCache.contains(formId))
        {
            isValid = true;
            formIdCache.remove(formId);
        }
        return isValid;
    }

    private VisitorFeedbackImpl buildFeedbackObject(String assetId, String visitorName,
            String visitorEmailAddress, String visitorWebsite, String type, String subject, String comment,
            boolean commentFlagged, Integer rating)
    {
        VisitorFeedbackImpl feedback = (VisitorFeedbackImpl) createFeedback();
        feedback.setAssetId(assetId);
        feedback.setFeedbackType(type);
        feedback.setSubject(sanitizeText(subject));
        feedback.setComment(sanitizeText(comment));
        feedback.setVisitorEmail(sanitizeText(visitorEmailAddress));
        feedback.setVisitorName(sanitizeText(visitorName));
        feedback.setVisitorWebsite(sanitizeText(visitorWebsite));
        feedback.setCommentFlagged(commentFlagged);
        feedback.setRating(rating);
        return feedback;
    }
    
    protected String sanitizeText(String text)
    {
        return text == null ? null : text.replaceAll("[<>]", "");
    }
    
    private VisitorFeedbackImpl buildFeedbackObject(QueryResult queryResult)
    {
        BigInteger rating = (BigInteger) queryResult.getPropertyValueById(PROP_RATING);
        VisitorFeedbackImpl feedback = buildFeedbackObject(
                (String) queryResult.getPropertyValueById(PROP_ARTICLE), 
                (String) queryResult.getPropertyValueById(PROP_NAME),
                (String) queryResult.getPropertyValueById(PROP_EMAIL), 
                (String) queryResult.getPropertyValueById(PROP_WEBSITE), 
                (String) queryResult.getPropertyValueById(PROP_FEEDBACK_TYPE), 
                (String) queryResult.getPropertyValueById(PROP_SUBJECT), 
                (String) queryResult.getPropertyValueById(PROP_COMMENT), 
                (Boolean) queryResult.getPropertyValueById(PROP_COMMENT_FLAGGED), 
                rating == null ? null : rating.intValue());
        
        feedback.setId((String) queryResult.getPropertyValueById(PropertyIds.OBJECT_ID));
        feedback.setPostTime(((Calendar) queryResult.getPropertyValueById(PropertyIds.CREATION_DATE)).getTime());
        return feedback;
    }

    private ItemIterable<QueryResult> runQuery(String query)
    {
        if (log.isDebugEnabled())
        {
            log.debug("About to run CMIS query: " + query);
        }
        Session session = CmisSessionHelper.getSession();
        return session.query(query, false);
    }

}
