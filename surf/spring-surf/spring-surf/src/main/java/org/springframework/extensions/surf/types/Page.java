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

import java.util.Map;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;

/**
 * Interface for a Page object type
 * 
 * @author muzquiano
 */
public interface Page extends ModelObject
{
    // type
    public static String TYPE_ID = "page";
    
    // properties
    public static String PROP_TEMPLATE_INSTANCE = "template-instance";
    public static String ATTR_FORMAT_ID = "format-id";
    public static String PROP_PAGE_TYPE_ID = "page-type-id";
    public static String PROP_AUTHENTICATION = "authentication";
    public static String DEFAULT_PAGE_TYPE_ID = "generic";
        
    /**
     * Gets the template id.
     * 
     * @return the template id
     */
    public String getTemplateId();

    /**
     * Gets the template id.
     * 
     * @param formatId the format id
     * 
     * @return the template id
     */
    public String getTemplateId(String formatId);

    /**
     * Sets the template id.
     * 
     * @param templateId the new template id
     */
    public void setTemplateId(String templateId);

    /**
     * Sets the template id.
     * 
     * @param templateId the template id
     * @param formatId the format id
     */
    public void setTemplateId(String templateId, String formatId);

    /**
     * Removes the template id.
     * 
     * @param formatId the format id
     */
    public void removeTemplateId(String formatId);

    /**
     * Gets the templates.
     * 
     * @param context the context
     * 
     * @return the templates
     */
    public Map<String, TemplateInstance> getTemplates(RequestContext context);

    /**
     * Gets the template.
     * 
     * @param context the context
     * 
     * @return the template
     */
    public TemplateInstance getTemplate(RequestContext context);

    /**
     * Gets the template.
     * 
     * @param context the context
     * @param formatId the format id
     * 
     * @return the template
     */
    public TemplateInstance getTemplate(RequestContext context, String formatId);

    /**
     * Gets the child pages.
     * 
     * @param context the context
     * 
     * @return the child pages
     */
    public Page[] getChildPages(RequestContext context);
    
    /**
     * Gets the page type id.
     * 
     * @return the page type id
     */
    public String getPageTypeId();
    
    /**
     * Sets the page type id.
     * 
     * @param pageTypeId the new page type id
     */
    public void setPageTypeId(String pageTypeId);
    
    /**
     * @return the Authentication required for this page
     */
    public RequiredAuthentication getAuthentication();

    /**
     * @param authentication    the authentication level to set
     */
    public void setAuthentication(String authentication);

    /**
     * Gets the page type.
     * 
     * @param context the context
     * 
     * @return the page type
     */
    public PageType getPageType(RequestContext context);
}
