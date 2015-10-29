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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import static org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel.*;
import static org.alfresco.model.ContentModel.*;

/**
 * Class for randomly generating web site structures.
 * @author Nick Smith
 * @since 4.0
 *
 */
public class WebSiteBuilder
{
    private NodeService nodeService;
    
    public NodeRef buildSiteStructure(NodeRef parent, String siteName, Tree<String> siteStructure)
    {
        NodeRef website = makeWebSite(parent, siteName);
        String rootName = siteStructure.getRoot();
        NodeRef webRoot = makeWebRoot(website, rootName);
        generateSectionTree(webRoot, rootName, siteStructure);
        return website;
    }

    public NodeRef makeWebSite(NodeRef parent, String siteName)
    {
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        ArrayList<String> siteSectionConfigs = new ArrayList<String>(1);
        siteSectionConfigs.add("isEditorial=false");
        properties.put(PROP_SECTION_CONFIG, siteSectionConfigs);
        return createNode(parent, siteName, TYPE_WEB_SITE, properties);
    }

    public NodeRef makeSection(NodeRef parent, String sectionName)
    {
        return createNode(parent, sectionName, TYPE_SECTION, null);
    }

    public NodeRef makeWebRoot(NodeRef website, String rootName)
    {
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();

        ArrayList<String> values = new ArrayList<String>(3);
        values.add("ws:image=ws:smallThumbnail");
        values.add("ws:image=ws:imagePreview");
        values.add("application/pdf=ws:swfPreview");
        properties.put(PROP_RENDITION_CONFIG, values);

        properties.put(PROP_TITLE, "Home");
        properties.put(PROP_DESCRIPTION, rootName);

        List<String> rootSectionConfigs = new ArrayList<String>(3);
        rootSectionConfigs.add("ws:indexPage=sectionpage1");
        rootSectionConfigs.add("ws:article=articlepage1");
        rootSectionConfigs.add("cmis:document=baseTemplate");

        properties.put(PROP_SECTION_CONFIG, (Serializable)rootSectionConfigs);

        properties.put(PROP_INHERIT_RENDITION_CONFIG, true);

        properties.put(ASPECT_HAS_ANCESTORS, null);

        return createNode(website, rootName, TYPE_WEB_ROOT, properties);
    }
    
    private NodeRef createNode(NodeRef parent, String name, QName type, Map<QName, Serializable> properties)
    {
        if(properties == null)
        {
            properties = new HashMap<QName, Serializable>();
        }
        properties.put(PROP_NAME, name);
        String localName = QName.createValidLocalName(name);
        QName assocName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, localName);
        return nodeService.createNode(parent,
                ASSOC_CONTAINS,
                assocName,
                type,
                properties).getChildRef();
    }

    /**
     * Recursively generates a tree of Sections in the repository.
     * @param parent
     * @param parentName
     * @param siteStructure a Tree of section names.
     */
    private void generateSectionTree(NodeRef parent, String parentName, Tree<String> siteStructure)
    {
        for (String sectionName : siteStructure.getChildren(parentName))
        {
            NodeRef section = makeSection(parent, sectionName);
            generateSectionTree(section, sectionName, siteStructure);
        }
    }

    /**
     * @param nodeService the nodeService to set
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
}