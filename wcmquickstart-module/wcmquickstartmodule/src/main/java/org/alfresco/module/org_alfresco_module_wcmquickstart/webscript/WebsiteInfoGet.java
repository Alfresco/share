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
package org.alfresco.module.org_alfresco_module_wcmquickstart.webscript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Website Info GET implementation
 * 
 * @author Brian Remmington
 */
public class WebsiteInfoGet extends DeclarativeWebScript implements WebSiteModel
{
    private static final Log log = LogFactory.getLog(WebsiteInfoGet.class);

    private static final String PARAM_WEBSITE_ID = "websiteid";

    private NodeService nodeService;
    private SiteHelper siteHelper;

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setSiteHelper(SiteHelper siteHelper)
    {
        this.siteHelper = siteHelper;
    }

    /**
     * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest,
     *      org.springframework.extensions.webscripts.Status,
     *      org.springframework.extensions.webscripts.Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // Get the website id
        String websiteId = req.getParameter(PARAM_WEBSITE_ID);
        if (log.isDebugEnabled())
        {
            log.debug("Finding feedback folder for website " + websiteId);
        }

        if (websiteId == null || websiteId.length() == 0)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No websiteid parameter specified.");
        } else if (NodeRef.isNodeRef(websiteId) == false)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "websiteid is not a valid Alfresco node reference. ( " + websiteId + ")");
        }
        NodeRef websiteNode = new NodeRef(websiteId);
        if (!nodeService.exists(websiteNode))
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "A node with the specified id does not exist in this repository. ( " + websiteId + ")");
        }

        //Find the root section of the site
        String rootSectionId = websiteId;
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(
                websiteNode, ContentModel.ASSOC_CONTAINS, QName.createQName(
                        NamespaceService.CONTENT_MODEL_1_0_URI, "root"));
        if (!childAssocs.isEmpty())
        {
            rootSectionId = childAssocs.get(0).getChildRef().toString();
        }
        
        
        //Now find the folder to use for visitor feedback...
        
        // If we fail to find/create an appropriate datalist folder then we'll
        // fallback to returning the id of the website itself
        String feedbackFolderId = websiteId;

        // Look up the tree to see if we're in a Share site (we really should be...)
        SiteInfo siteInfo = siteHelper.getRelevantShareSite(websiteNode);
        
        if (siteInfo == null)
        {
            if (log.isWarnEnabled())
            {
                log.warn("A website node appears to have been created outside of a Share site. "
                        + "This is unsupported. Node id = " + websiteId);
            }
        } 
        else
        {
            feedbackFolderId = siteHelper.getFeedbackList((String)nodeService.getProperty(websiteNode, ContentModel.PROP_NAME), 
                    siteInfo.getShortName(), true).toString();
        }

        // Put the collection data in the model and pass to the view
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("rootSectionId", rootSectionId);
        model.put("feedbackFolderId", feedbackFolderId);
        return model;
    }
}
