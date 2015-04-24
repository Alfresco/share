/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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

package org.alfresco.module.org_alfresco_module_wcmquickstart.benchmark;

import static org.alfresco.model.ContentModel.ASSOC_CONTAINS;
import static org.alfresco.model.ContentModel.TYPE_FOLDER;

import java.io.File;
import java.util.Random;

import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator;
import org.alfresco.repo.nodelocator.NodeLocatorService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.springframework.beans.factory.InitializingBean;
/**
 * @author Nick Smith
 * @since 4.0
 *
 */
public class WebSiteRandomGenerator implements WebSiteGenerator, InitializingBean
{
    public static final String WEB_SITE_HOME_NAME = "Alfresco Quick Start";
    private final Random random = new Random();
    
    private TextGenerator textGenerator;
    private WebSiteBuilder siteBuilder;
    private AcpGenerator acpGenerator;
    private NodeLocatorService nodeLocatorService;
    private SiteService siteService;
    private NodeService nodeService;
    
    private String shareSiteName = null;
    private int minSectionCount = 0;
    private int maxSectionCount = 8;
    private int minSectionDepth = 0;
    private int maxSectionDepth = 4;

    // THis is set in afterPropertiesSet().
    private NodeRef webHome;

    public File generateWebSiteAcp(String location) throws Exception
    {
        return acpGenerator.generateACP(webHome, location);
    }
    
    public NodeRef generateWebSite(String siteName)
    {
        if(webHome ==null)
        {
            this.webHome = findOrCreateWebHome();
        }
        Tree<String> siteStructure = generateRandomSiteStructure();
        return siteBuilder.buildSiteStructure(webHome, siteName, siteStructure);
    }

    /**
     * Recursively generate a {@link Tree} of randomly generated Section names with a maximum depth specified by <code>maxDepth</code>.
     * The number of children each Section has is normally distributed over the range <code>minSectionCount-maxSectionCount</code> 
     * @param parent
     * @param maxDepth
     * @param siteStructure
     */
    private Tree<String> generateRandomSiteStructure()
    {
        String rootName = textGenerator.generateName();
        Tree<String> siteStructure = new Tree<String>(rootName);
        int childCount = getRandom(minSectionCount, maxSectionCount);
        for (int i = 0; i < childCount; i++)
        {
            int sectionDepth = getRandom(minSectionDepth, maxSectionDepth);
            generateRandomSectionStructure(rootName, sectionDepth, siteStructure);
        }
        return siteStructure;
    }

    /**
     * Recursively generate a {@link Tree} of randomly generated Section names with a maximum depth specified by <code>maxDepth</code>.
     * The number of children each Section has is normally distributed over the range <code>minSectionCount-maxSectionCount</code> 
     * @param parent
     * @param maxDepth
     * @param siteStructure
     */
    private void generateRandomSectionStructure(String parent, int maxDepth, Tree<String> siteStructure)
    {
        String child = textGenerator.generateName();
        siteStructure.appendChild(parent, child);
        if(maxDepth>0)
        {
            int childCount = getRandom(minSectionCount, maxSectionCount);
            for (int i = 0; i < childCount; i++)
            {
                generateRandomSectionStructure(child, maxDepth-1, siteStructure);
            }
        }
    }

    private int getRandom(int min, int max)
    {
        double rand = random.nextGaussian();
        double range =  (double) min - max;
        return (int) Math.round(rand * range) + min;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        ParameterCheck.mandatory("nodeLocatorService", nodeLocatorService);
        ParameterCheck.mandatory("siteService", siteService);
    }

    private NodeRef findOrCreateWebHome()
    {
        NodeRef webParent = findOrCreateWebParent();
        NodeRef webHomeNode = nodeService.getChildByName(webParent, ASSOC_CONTAINS, WEB_SITE_HOME_NAME);
        if(webHomeNode == null)
        {
            String localName = QName.createValidLocalName(WEB_SITE_HOME_NAME);
            QName assocName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, localName);
            webHomeNode = nodeService.createNode(webParent,
                    ASSOC_CONTAINS,
                    assocName,
                    TYPE_FOLDER).getChildRef();
        }
        return webHomeNode;
    }

    private NodeRef findOrCreateWebParent()
    {
        if(shareSiteName !=null && shareSiteName.isEmpty()==false )
        {
            return getOrCreateSiteDocumentLibrary();
        }
        return getCompanyHome();
    }

    private NodeRef getCompanyHome()
    {
        return nodeLocatorService.getNode(CompanyHomeNodeLocator.NAME, null, null);
    }

    private NodeRef getOrCreateSiteDocumentLibrary()
    {
        SiteInfo siteInfo = siteService.getSite(shareSiteName);
        if(siteInfo == null)
        {
            siteInfo = siteService.createSite(
                    "sitePreset",
                    shareSiteName,
                    shareSiteName, "",
                    SiteVisibility.PUBLIC);
        }
        NodeRef docLibrary = siteService.getContainer(siteInfo.getShortName(), SiteService.DOCUMENT_LIBRARY);
        if(docLibrary == null)
        {
            docLibrary = siteService.createContainer(
                    siteInfo.getShortName(),
                    SiteService.DOCUMENT_LIBRARY,
                    TYPE_FOLDER, null);
        }
        return docLibrary;
    }

    /**
     * @param siteBuilder the siteBuilder to set
     */
    public void setWebSiteBuilder(WebSiteBuilder siteBuilder)
    {
        this.siteBuilder = siteBuilder;
    }
    
 
    /**
     * @param nodeService the nodeService to set
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * @param nodeLocatorService the nodeLocatorService to set
     */
    public void setNodeLocatorService(NodeLocatorService nodeLocatorService)
    {
        this.nodeLocatorService = nodeLocatorService;
    }
    
    /**
     * @param siteService the siteService to set
     */
    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }
    
    /**
     * @param textGenerator the textGenerator to set
     */
    public void setTextGenerator(TextGenerator textGenerator)
    {
        this.textGenerator = textGenerator;
    }
    
    /**
     * @param shareSiteName the shareSiteName to set
     */
    public void setShareSiteName(String shareSiteName)
    {
        this.shareSiteName = shareSiteName;
    }
    
    /**
     * @param minSectionCount the minSectionCount to set
     */
    public void setMinSectionCount(int minSectionCount)
    {
        this.minSectionCount = minSectionCount;
    }
    
    /**
     * @param maxSectionCount the maxSectionCount to set
     */
    public void setMaxSectionCount(int maxSectionCount)
    {
        this.maxSectionCount = maxSectionCount;
    }
    
    /**
     * @param minSectionDepth the minSectionDepth to set
     */
    public void setMinSectionDepth(int minSectionDepth)
    {
        this.minSectionDepth = minSectionDepth;
    }
    
    /**
     * @param maxSectionDepth the maxSectionDepth to set
     */
    public void setMaxSectionDepth(int maxSectionDepth)
    {
        this.maxSectionDepth = maxSectionDepth;
    }
}