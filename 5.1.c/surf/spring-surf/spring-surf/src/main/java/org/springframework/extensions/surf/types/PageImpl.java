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

package org.springframework.extensions.surf.types;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelPersisterInfo;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;

/**
 * Default page implementation
 * 
 * @author muzquiano
 */
public class PageImpl extends AbstractModelObject implements Page
{
    /** Template ID for the empty default format - cached value */
    private String templateId = null;
    
    /**
     * Instantiates a new page for a given XML document
     * 
     * @param document the document
     */
    public PageImpl(String id, ModelPersisterInfo key, Document document)
    {
        super(id, key, document);
        
        // default page type
        if (getPageTypeId() == null)
        {
            setPageTypeId(DEFAULT_PAGE_TYPE_ID);
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.AbstractModelObject#getTypeName()
     */
    public String getTypeId() 
    {
        return TYPE_ID;
    }    

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#getTemplateId()
     */
    public String getTemplateId()
    {
        if (this.templateId == null)
        {
            this.templateId = getTemplateId(null);
        }
        return this.templateId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#getTemplateId(java.lang.String)
     */
    public String getTemplateId(String formatId)
    {
        Element templateElement = getTemplateElement(formatId);
        if (templateElement != null)
        {
            return templateElement.getStringValue();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#setTemplateId(java.lang.String)
     */
    public void setTemplateId(String templateId)
    {
        setTemplateId(templateId, null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#setTemplateId(java.lang.String, java.lang.String)
     */
    public void setTemplateId(String templateId, String formatId)
    {
        // update cached value
        this.templateId = templateId;
        
        // update XML config
        if (formatId != null && formatId.equals(FrameworkBean.getConfig().getDefaultFormatId()))
        {
            formatId = null;
        }
        
        Document document = getDocument();
        Element templateElement = getTemplateElement(document, formatId);
        if (templateElement == null)
        {
            templateElement = document.getRootElement().addElement(PROP_TEMPLATE_INSTANCE);
            if (formatId != null)
            {
                templateElement.addAttribute(ATTR_FORMAT_ID, formatId);
            }
        }
        templateElement.setText(templateId);
        updateXML(document);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#removeTemplateId(java.lang.String)
     */
    public void removeTemplateId(String formatId)
    {
        if (formatId != null && formatId.equals(FrameworkBean.getConfig().getDefaultFormatId()))
        {
            formatId = null;
        }
        
        if (formatId == null)
        {
            // update cached value
            this.templateId = null;
        }
        
        Element result = null;
        
        Document document = getDocument();
        List<Element> templateElements = document.getRootElement().elements(PROP_TEMPLATE_INSTANCE);
        for (int i = 0; i < templateElements.size(); i++)
        {
            Element templateElement = templateElements.get(i);
            String _formatId = templateElement.attributeValue(ATTR_FORMAT_ID);
            if (formatId == null)
            {
                if (_formatId == null || _formatId.length() == 0)
                {
                    result = templateElement;
                    break;
                }
            }
            else if (formatId.equals(_formatId))
            {
                result = templateElement;
                break;
            }
        }
        
        if (result != null)
        {
            result.getParent().remove(result);
            updateXML(document);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#getTemplates(org.alfresco.web.framework.RequestContext)
     */
    public Map<String, TemplateInstance> getTemplates(RequestContext context)
    {
        Map<String, TemplateInstance> map = new HashMap<String, TemplateInstance>(8, 1.0f);
        
        List templateElements = getDocument().getRootElement().elements(PROP_TEMPLATE_INSTANCE);
        for (int i = 0; i < templateElements.size(); i++)
        {
            Element templateElement = (Element) templateElements.get(i);
            String formatId = templateElement.attributeValue(ATTR_FORMAT_ID);
            if (formatId == null || formatId.length() == 0)
            {
                formatId = FrameworkBean.getConfig().getDefaultFormatId();
            }

            String templateId = templateElement.getStringValue();
            if (templateId != null)
            {
                TemplateInstance template = (TemplateInstance) context.getObjectService().getTemplate(templateId);
                map.put(formatId, template);
            }
        }
        
        return map;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#getTemplate(org.alfresco.web.framework.RequestContext)
     */
    public TemplateInstance getTemplate(RequestContext context)
    {
        TemplateInstance instance = null;
        String templateId = getTemplateId();
        if (templateId != null)
        {
            instance = context.getObjectService().getTemplate(templateId);
        }
        return instance;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#getTemplate(org.alfresco.web.framework.RequestContext, java.lang.String)
     */
    public TemplateInstance getTemplate(RequestContext context, String formatId)
    {
        TemplateInstance instance = null;
        String templateId = getTemplateId(formatId);
        if (templateId != null)
        {
            instance = context.getObjectService().getTemplate(templateId);
        }
        return instance;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#getChildPages(org.alfresco.web.framework.RequestContext)
     */
    public PageImpl[] getChildPages(RequestContext context)
    {
        Map<String, ModelObject> objects = context.getObjectService().findPageAssociations(
                this.getId(), null, PageAssociation.CHILD_ASSOCIATION_TYPE_ID);
        
        PageImpl[] pages = new PageImpl[objects.size()];
        
        int i = 0;
        Iterator it = objects.values().iterator();
        while (it.hasNext())
        {
            PageAssociation pageAssociation = (PageAssociation) it.next();            
            pages[i] = (PageImpl) pageAssociation.getDestPage(context);
            i++;
        }
        
        return pages;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#getPageTypeId()
     */
    public String getPageTypeId()
    {
        return this.getProperty(PROP_PAGE_TYPE_ID);        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#setPageTypeId(java.lang.String)
     */
    public void setPageTypeId(String pageTypeId)
    {
        this.setProperty(PROP_PAGE_TYPE_ID, pageTypeId);        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#getAuthentication()
     */
    public RequiredAuthentication getAuthentication()
    {
        RequiredAuthentication authentication = RequiredAuthentication.none;
        
        String auth = this.getProperty(PROP_AUTHENTICATION);
        if (auth != null)
        {
            try
            {
               authentication = RequiredAuthentication.valueOf(auth.toLowerCase());
            }
            catch (IllegalArgumentException enumErr)
            {
               throw new PlatformRuntimeException(
                     "Invalid page <authentication> element value: " + auth);
            }
        }
        return authentication;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#setAuthentication(java.lang.String)
     */
    public void setAuthentication(String authentication)
    {
        this.setProperty(PROP_AUTHENTICATION, authentication);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Page#getPageType(org.alfresco.web.framework.RequestContext)
     */
    public PageType getPageType(RequestContext context)
    {
        String pageTypeId = getPageTypeId();
        if (pageTypeId != null)
        {
            return context.getObjectService().getPageType(pageTypeId);
        }
        return null;
    }
    
    /**
     * Gets the template element.
     * 
     * @param formatId the optional format id
     * 
     * @return the template element
     */
    protected Element getTemplateElement(String formatId)
    {
        return getTemplateElement(getDocument(), formatId);
    }
    
    /**
     * Gets the template element.
     *
     * @param document the XML Document
     * @param formatId the optional format id
     * 
     * @return the template element
     */
    protected Element getTemplateElement(Document document, String formatId)
    {
        if (formatId != null && formatId.equals(FrameworkBean.getConfig().getDefaultFormatId()))
        {
            formatId = null;
        }
        
        Element result = null;
        
        List<Element> templateElements = document.getRootElement().elements(PROP_TEMPLATE_INSTANCE);
        for (int i = 0; i < templateElements.size(); i++)
        {
            Element templateElement = templateElements.get(i);
            String _formatId = templateElement.attributeValue(ATTR_FORMAT_ID);
            if (formatId == null)
            {
                if (_formatId == null || _formatId.length() == 0)
                {
                    result = templateElement;
                    break;
                }
            }
            else if (formatId.equals(_formatId))
            {
                result = templateElement;
                break;
            }
        }
        return result;
    }
}
