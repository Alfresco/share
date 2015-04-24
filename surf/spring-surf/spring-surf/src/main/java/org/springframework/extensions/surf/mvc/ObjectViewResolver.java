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

package org.springframework.extensions.surf.mvc;

import java.util.Locale;
import java.util.Map;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.resource.Resource;
import org.springframework.extensions.surf.resource.ResourceLoader;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.ContentAssociation;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.webscripts.ScriptResource;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Resolves a URI into an object view
 * 
 * @author muzquiano
 */
public class ObjectViewResolver extends AbstractWebFrameworkViewResolver 
{
    private static final String URI_PREFIX_OBJECT = "obj";
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#canHandle(java.lang.String, java.util.Locale)
     */
    protected boolean canHandle(String viewName, Locale locale) 
    {
        boolean canHandle = false;
        
        RequestContext context = ThreadLocalRequestContext.getRequestContext();
        
        if (viewName.startsWith(URI_PREFIX_OBJECT + "/") || viewName.equals(URI_PREFIX_OBJECT))
        {
            String objectId = (String) context.getParameter("o");
            if (objectId != null)
            {
                // check if we have a resource loader for this resource
                String[] ids = getWebFrameworkResourceService().getResourceDescriptorIds(objectId);
                ResourceLoader resourceLoader = getWebFrameworkResourceService().getResourceLoader(ids[0], ids[1]);
                
                canHandle = (resourceLoader != null);
            }
        }
        
        return canHandle;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#buildView(java.lang.String)
     */
    protected AbstractUrlBasedView buildView(String viewName) throws Exception 
    {
        AbstractUrlBasedView view = null;

        if (viewName.startsWith(URI_PREFIX_OBJECT + "/") || URI_PREFIX_OBJECT.equals(viewName))
        {
            // request context
            RequestContext context = ThreadLocalRequestContext.getRequestContext();        

            // object id
            String resourceId = context.getParameter("o");
            
            // load the object and map into context
            Resource resource = getWebFrameworkResourceService().getResource(resourceId);
            if (resource != null)
            {
                context.setCurrentObject(resource);

                // lookup a template for this object type id
                String objectTypeId = resource.getObjectTypeId();
                
                // Look up which template to use to display this content
                // this must also take into account the current format
                Map<String, ModelObject> objects = getModelObjectService().findContentAssociations(objectTypeId, null, null, null, null);
                if (objects.size() > 0)
                {
                    ContentAssociation association = (ContentAssociation) objects.values().iterator().next();
                    ModelObject o = association.getObject(context);
                    if (o != null)
                    {
                        if (o instanceof TemplateInstance)
                        {
                            // create a TemplateView
                            view = new TemplateView(getWebframeworkConfigElement(), 
                                                    getModelObjectService(), 
                                                    getWebFrameworkResourceService(), 
                                                    getWebFrameworkRenderService(),
                                                    getTemplatesContainer());
                            view.setUrl(o.getId());
                        }
                        if (o instanceof Page)
                        {
                            // create a PageView
                            view = new PageView(getWebframeworkConfigElement(), 
                                                getModelObjectService(), 
                                                getWebFrameworkResourceService(), 
                                                getWebFrameworkRenderService(),
                                                getTemplatesContainer());
                            view.setUrl(o.getId());
                        }
                    }
                }
                else
                {
                    // some data that we can use in the reporting page
                    context.setValue("resource", new ScriptResource(context, resource));

                    // show system page: content association missing
                    view = new SystemPageView(getWebframeworkConfigElement(), 
                                              getModelObjectService(), 
                                              getWebFrameworkResourceService(), 
                                              getWebFrameworkRenderService(),
                                              getTemplatesContainer());
                    view.setUrl(WebFrameworkConstants.SYSTEM_PAGE_CONTENT_ASSOCIATION_MISSING);
                }
            }
        }
        
        return view;
    }
}
