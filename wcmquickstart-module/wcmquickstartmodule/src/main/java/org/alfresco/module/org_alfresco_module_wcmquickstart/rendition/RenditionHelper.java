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
package org.alfresco.module.org_alfresco_module_wcmquickstart.rendition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParserService;
import org.alfresco.repo.action.executer.ActionExecuter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.rendition.RenderCallback;
import org.alfresco.service.cmr.rendition.RenditionDefinition;
import org.alfresco.service.cmr.rendition.RenditionService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Rendition helper class.  Consolidates common rendition functions used in
 * the WCM Quick Start application.
 * 
 * @author Roy Wetherall
 */
public class RenditionHelper implements WebSiteModel
{
    private static final String CMIS_TYPE_DOCUMENT = "cmis:document";

    /** Node service */
    private NodeService nodeService;

    /** Content service */
    private ContentService contentService;

    /** Dictionary service */
    private DictionaryService dictionaryService;

    /** Rendition service */
    private RenditionService renditionService;

    /** Namespace service */
    private NamespaceService namespaceService;

    /** Context parser service */
    private ContextParserService contextParserService;

    /**
     * Set node service
     * @param nodeService	node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set content service
     * @param contentService	content service
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }

    /**
     * Set dictionary service
     * @param dictionaryService	dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Setg rendition service
     * @param renditionService	rendition service
     */
    public void setRenditionService(RenditionService renditionService)
    {
        this.renditionService = renditionService;
    }	

    /**
     * Set namespace service
     * @param namespaceService	namespace service
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * Set the context parser service
     * @param contextParserService	context parser service
     */
    public void setContextParserService(ContextParserService contextParserService)
    {
        this.contextParserService = contextParserService;
    }

    public void createRenditions(NodeRef nodeRef)
    {
        createRenditions(nodeRef, false);
    }

    /**
     * Create the renditions for the given node reference assuming it has content set and 
     * is not a working copy.
     * 
     * @param nodeRef	node reference
     */
    public void createRenditions(NodeRef nodeRef, boolean clearRenditions)
    {
        if (nodeService.exists(nodeRef) == true &&
                nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY) == false)
        {								
            // Check that the node has content
            String mimetype = null;
            ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
            if (reader != null && reader.getSize() != 0)
            {				
                // Get the section 
                NodeRef section = nodeService.getPrimaryParent(nodeRef).getParentRef();

                // Get the mimetype
                mimetype = reader.getMimetype();

                // Get the content type of the node we are dealing with
                QName type = nodeService.getType(nodeRef).getPrefixedQName(namespaceService);

                // Clear all the existing "ws" renditions before we proceed (if required)
                if (clearRenditions == true)
                {
                    List<ChildAssociationRef> renditions = renditionService.getRenditions(nodeRef);
                    for (ChildAssociationRef rendition : renditions)
                    {
                        if (NAMESPACE.equals(rendition.getQName().getNamespaceURI()) == true)
                        {
                            nodeService.deleteNode(rendition.getChildRef());
                        }
                    }
                }

                // Create renditions for the webasset
                createRenditions(section, nodeRef, type, mimetype);
            }
        }
    }

    /**
     * Creates the images renditions based on the rendition definitions defined 
     * in the section configuration.
     * @param section	section node
     * @param nodeRef		image node
     */
    private void createRenditions(NodeRef section, NodeRef nodeRef, QName type, String mimetype)
    {
        // Get the section config to a map
        Map<String, List<String>> renditionConfig = getRenditionConfig(section);

        if (renditionConfig.isEmpty() == false)
        {
            createRenditions(nodeRef, renditionConfig.get(mimetype));				
            createRenditions(nodeRef, getRenditionListForType(renditionConfig, type));
        }

        // Check parents if in herit rendition config
        Boolean inherit = (Boolean)nodeService.getProperty(section, PROP_INHERIT_RENDITION_CONFIG);
        if (Boolean.TRUE.equals(inherit) && TYPE_WEB_SITE.equals(type) == false)
        {
            NodeRef sectionParent = nodeService.getPrimaryParent(section).getParentRef();
            createRenditions(sectionParent, nodeRef, type, mimetype);			
        }
    }

    /**
     * Get rendition list for a given type, traversing the type hierarchy if required.
     * 
     * @param renditionConfig	rendition configuration
     * @param type				node type
     * @return List<String>		list of renditions
     */
    private List<String> getRenditionListForType(Map<String, List<String>> renditionConfig, QName type)
    {
        List<String> result = new ArrayList<String>(7);

        // Only look up types that are sub of content
        if (dictionaryService.isSubClass(type, ContentModel.TYPE_CONTENT) == true)
        {
            // Look up the rendition list (convert to prefix/cmis type)
            String lookupType = getLookupType(type);
            List<String> temp = renditionConfig.get(lookupType);
            if (temp != null)
            {
                result.addAll(temp);
            }

            // Assuming we havn't found a match and that we arn't checking for the content type ...
            if (//result == null && 
                    ContentModel.TYPE_CONTENT.equals(type) == false)
            {
                // ... get the parent type
                TypeDefinition typeDef = dictionaryService.getType(type);
                if (typeDef != null)
                {
                    QName parentType = typeDef.getParentName();
                    if (parentType != null)
                    {
                        // Get the rendition list for the parent type
                        temp = getRenditionListForType(renditionConfig, parentType);
                        if (temp != null)
                        {
                            result.addAll(temp);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets the lookup type string for the rendition configuration, either the prefix string for the Alfresco type
     * or the cmis equivalent for cm:content.  Note that non-sub types of content will not be looked up since
     * we can't rendition non content nodes.
     * @param type		content type
     * @return String	lookup type string
     */
    private String getLookupType(QName type)
    {
        String result = null;

        if (ContentModel.TYPE_CONTENT.equals(type) == true)
        {
            result = CMIS_TYPE_DOCUMENT;
        }
        else
        {
            type = type.getPrefixedQName(namespaceService);
            result = type.getPrefixString();
        }

        return result;
    }

    /**
     * 
     * @param node
     * @param renditions
     */
    private void createRenditions(NodeRef node, List<String> renditions)
    {
        if (renditions != null)
        {
            for (String renditionName : renditions)
            {
                // Figure out what the QName of the rendition is
                QName renditionQName = null;
                try
                {
                    renditionQName = QName.createQName(renditionName, namespaceService);
                }
                catch (InvalidQNameException exception)
                {
                    // Do nothing
                }
                if (renditionQName == null)
                {
                    renditionQName = QName.createQName(WebSiteModel.NAMESPACE, renditionName);
                }

                // Ensure we don't create the initial rendition more than once
                if (renditionService.getRenditionByName(node, renditionQName) == null)
                {
                    // Defaults to content model namespace if none provided

                    // Rendition Definitions are persisted underneath the Data Dictionary for which Group ALL
                    // has Consumer access by default. However, we cannot assume that that access level applies for all deployments. See ALF-7334.
                    final QName finalRenditionQName = renditionQName;
                    RenditionDefinition def = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<RenditionDefinition>()
                            {
                        @Override
                        public RenditionDefinition doWork() throws Exception
                        {
                            return renditionService.loadRenditionDefinition(finalRenditionQName);
                        }
                            }, AuthenticationUtil.getSystemUserName());

                    if (def != null)
                    {
                        // Parse the path template in the context of the current node
                        String pathTemplate = (String)def.getParameterValue(RenditionService.PARAM_DESTINATION_PATH_TEMPLATE);
                        if (pathTemplate != null)
                        {
                            pathTemplate = contextParserService.parse(node, pathTemplate);
                            def.setParameterValue(RenditionService.PARAM_DESTINATION_PATH_TEMPLATE, pathTemplate);
                        }

                        final RenditionDefinition callbackDef = def;
                        // Render
                        RenderCallback renderCallback = new RenderCallback()
                        {

                            @Override
                            public void handleSuccessfulRendition(ChildAssociationRef primaryParentOfNewRendition)
                            {
                                ChildAssociationRef assocRef = (ChildAssociationRef) callbackDef.getParameterValue(ActionExecuter.PARAM_RESULT);
                                NodeRef rendition = assocRef.getChildRef();
                                // We need the rendition to be indexed
                                nodeService.removeAspect(rendition, ContentModel.ASPECT_INDEX_CONTROL);
                            }

                            @Override
                            public void handleFailedRendition(Throwable t)
                            {
                                // Ignore
                            }
                        };

                        renditionService.render(node, def, renderCallback);
                    }
                }
            }
        }
    }

    /**
     * Get the rendition configuration for a section as a map.
     * @param section						section node reference
     * @return Map<String, List<String>> 	map of renditions by mimetype and type
     */
    @SuppressWarnings("unchecked")
    private Map<String, List<String>> getRenditionConfig(NodeRef section)
    {
        Map<String, List<String>> result = new TreeMap<String, List<String>>();

        List<String> values = (List<String>)nodeService.getProperty(section, PROP_RENDITION_CONFIG);
        if (values != null)
        {
            for (String value : values)
            {	       		
                String[] split = value.split("=");
                if (split.length == 2)
                {
                    String key = split[0];
                    if (result.containsKey(key) == true)
                    {
                        List<String> renditions = result.get(key);
                        renditions.add(split[1]);
                    }
                    else
                    {
                        List<String> renditions = new ArrayList<String>(3);
                        renditions.add(split[1]);
                        result.put(key, renditions);
                    }
                }
                else
                {
                    // TODO log invalid value
                }
            }
        }
        return result;
    }
}
