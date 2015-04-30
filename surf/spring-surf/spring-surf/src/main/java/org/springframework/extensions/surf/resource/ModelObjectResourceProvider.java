/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
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

package org.springframework.extensions.surf.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.exception.ResourceLoaderException;
import org.springframework.extensions.surf.util.XMLUtil;

/**
 * An implementation of ResourceProvider which enables model objects
 * to manage the configuration of named resources.
 * 
 * Named resources can be retrieved from the model object, worked with
 * and referenced programmatically.
 * 
 * Resources are stored as part of the model object configuration.
 * 
 * Examples:
 * 
 * Alfresco Resource
 *   <resource name="abc1" endpoint="alfresco">workspace://SpacesStore/NodeRef</resource>
 *   <resource name="abc2" endpoint="alfresco">workspace/SpacesStore/Company Home/Data Dictionary</resource>
 * 
 * URI (absolute or relative on endpoint)
 *   <resource name="abc3">http://www:8080/a/b/c.gif</resource>
 *   <resource name="abc4" endpoint="alfresco">/a/b/c.gif</resource>
 * 
 * Web Application Files (relative to webapp always)
 *   <resource name="abc5">/a/b/c.gif</resource>
 * 
 * @author muzquiano
 */
public class ModelObjectResourceProvider implements ResourceProvider
{
    protected ModelObject object;
    protected Map<String, Resource> resources;

    /**
     * Instantiates a new model object resource provider.
     * 
     * @param object the object
     */
    public ModelObjectResourceProvider(ModelObject object)
    {
        this.object = object;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#getResource(java.lang.String)
     */
    public Resource getResource(String name)
    {
        return getResourcesMap().get(name);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#getResources()
     */
    public Resource[] getResources()
    {
        Map<String, Resource> map = getResourcesMap();
        return map.values().toArray(new Resource[map.size()]);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#addResource(java.lang.String, java.lang.String)
     */
    public Resource addResource(String name, String resourceId)
    {
        String[] ids = FrameworkBean.getResourceService().getResourceDescriptorIds(resourceId);
        
        return addResource(name, ids[0], ids[1], ids[2]);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#addResource(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized Resource addResource(String name, String protocolId, String endpointId, String objectId)
    {
        Resource resource = getResourcesMap().get(name);
        if (resource == null)
        {
            Element rootElement = getResourcesElement(this.object);

            Element resourceElement = rootElement.addElement("resource");
            resourceElement.addAttribute("name", name);
            
            // protocol id
            resourceElement.addAttribute("protocol", protocolId);
            
            // endpoint id
            resourceElement.addAttribute("endpoint", endpointId);
            
            // object id
            if (objectId != null && !"".equals(objectId))
            {
                XMLUtil.setValue(resourceElement, objectId);
            }
            
            resource = loadResource(protocolId, endpointId, objectId);
            if (resource != null)
            {
                this.resources.put(name, resource);
            }
        }

        return resource;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#updateResource(java.lang.String, org.alfresco.web.framework.resource.Resource)
     */
    public void updateResource(String name, Resource resource)
    {
        Element element = getResourceElement(this.object, name);
        if (element != null)
        {
            List attributes = element.attributes();
            for (int i = 0; i < attributes.size(); i++)
            {
                Attribute attribute = (Attribute) attributes.get(i);
                element.remove(attribute);
            }
            
            element.addAttribute("name", name);

            // protocol
            String protocol = resource.getProtocolId();
            if (protocol != null && !"".equals(protocol))
            {
                element.addAttribute("protocol", protocol);
            }
            
            // endpoint
            String endpoint = resource.getEndpointId();
            if (endpoint != null && !"".equals(endpoint))
            {
                element.addAttribute("endpoint", endpoint);
            }
            
            // object
            String objectId = resource.getObjectId();
            if (objectId != null && !"".equals(objectId))
            {
                XMLUtil.setValue(element, objectId);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#removeResource(java.lang.String)
     */
    public void removeResource(String name)
    {
        Element element = getResourceElement(this.object, name);
        if (element != null)
        {
            Element rootElement = getResourcesElement(this.object);
            rootElement.remove(element);

            // update our cache map
            this.resources.remove(name);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#getResourcesMap()
     */
    public synchronized Map<String, Resource> getResourcesMap()
    {
        if (this.resources == null)
        {
            this.resources = new HashMap<String, Resource>(8, 1.0f);

            Element rootElement = getResourcesElement(this.object);
            List elements = rootElement.elements("resource");
            for (int i = 0; i < elements.size(); i++)
            {
                Element el = (Element) elements.get(i);

                String name = el.attributeValue("name");
                
                String protocolId = el.attributeValue("protocol");
                String endpointId = el.attributeValue("endpoint");
                String objectId = XMLUtil.getValue(el);

                Resource resource = loadResource(protocolId, endpointId, objectId);
                this.resources.put(name, resource);
            }
        }

        return this.resources;
    }

    /**
     * Gets the resources element.
     * 
     * @param object the object
     * 
     * @return the resources element
     */
    protected static Element getResourcesElement(ModelObject object)
    {
        Element result = null;

        List elements = object.getDocument().getRootElement().elements(
                "resources");
        if (elements.size() > 0)
        {
            result = (Element) elements.get(0);
        }
        else
        {
            result = object.getDocument().getRootElement().addElement(
                    "resources");
        }

        return result;
    }

    /**
     * Gets the resource element.
     * 
     * @param object the object
     * @param name the name
     * 
     * @return the resource element
     */
    protected static Element getResourceElement(ModelObject object, String name)
    {
        Element result = null;

        Element rootElement = getResourcesElement(object);

        List elements = rootElement.elements("resource");
        for (int i = 0; i < elements.size(); i++)
        {
            Element el = (Element) elements.get(i);
            String _name = el.attributeValue("name");
            if (_name.equals(name))
            {
                result = el;
                break;
            }
        }
        return result;
    }
    
    /**
     * Helper method for loading resources.  Delegates off to the
     * resources service.
     * 
     * @param protocolId the protocol id
     * @param endpointId the endpoint id
     * @param objectId the object id
     * 
     * @return the resource
     */
    private Resource loadResource(String protocolId, String endpointId, String objectId)
    {
        Resource resource = null;
        
        // load resource object
        ResourceService resourceService = FrameworkUtil.getServiceRegistry().getResourceService();
        try
        {
            resource = resourceService.getResource(protocolId, endpointId, objectId);
        }
        catch (ResourceLoaderException rle)
        {
            rle.printStackTrace();
        }
        
        return resource;        
    }
}