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

package org.springframework.extensions.webscripts;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.exception.ResourceLoaderException;
import org.springframework.extensions.surf.resource.Resource;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.site.ThemeUtil;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.surf.types.Theme;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.connector.User;

/**
 * A root-scoped Java object that wraps the Render Context object.
 * 
 * @author muzquiano
 */
@ScriptClass 
(
        help="Render context for the current rendering object",
        types=
        {
                ScriptClassType.JavaScriptRootObject,
                ScriptClassType.TemplateRootObject
        }
)
public final class ScriptRenderContext extends ScriptBase
{
    private ScriptResource scriptResource = null;
    private ScriptModelObject scriptPageObject = null;
    private ScriptModelObject scriptTemplateObject = null;
    private ScriptModelObject scriptThemeObject = null;
    private ScriptModelObject scriptRootPageObject = null;
    private ScriptUser scriptUser = null;
    private ScriptLinkBuilder scriptLinkBuilder = null;
    
    final private RequestContext renderContext;
    
    protected ScriptableMap<String, Serializable> parameters;
    protected ScriptableMap<String, Serializable> attributes;
    protected ScriptableMap<String, Serializable> headers;
    
    
    /**
     * Constructs a new ScriptRequestContext object.
     * 
     * @param context   The RequestContext instance for the current request
     */
    public ScriptRenderContext(RequestContext context)
    {
        super(context);
        
        this.renderContext = context;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebFrameworkScriptBase#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
            this.properties = new ScriptableWrappedMap(context.getValuesMap());
            this.properties.putAll(context.getParameters());
        }
        
        return this.properties;
    }
    
    public void setValue(String key, Object value)
    {
        this.renderContext.setValue(key, (Serializable)ScriptValueConverter.unwrapValue(value));
    }

    
    // --------------------------------------------------------------
    // JavaScript Properties
    
    @ScriptMethod 
    (
            help="Gets the id of the currently rendering content resource",
            output="The id of the content resource or null if none"
    )
    public String getContentId()
    {
        return context.getCurrentObjectId();
    }
    
    @ScriptMethod 
    (
            help="Gets the currently rendering content resource",
            output="The currently rendering ScriptResource or null if none"
    )
    public synchronized ScriptResource getResource()
    {
        if (scriptResource == null)
        {
            Resource resource = context.getCurrentObject();
            if (resource != null)
            {
                scriptResource = new ScriptResource(context, resource);
            }
        }
        
        return scriptResource;
    }
    
    @ScriptMethod 
    (
            help="Gets the requested format id"
    )
    public String getFormatId()
    {
        return context.getFormatId();
    }
    
    @ScriptMethod 
    (
            help="Gets the id of the request context"
    )
    public String getId()
    {
        return context.getId();
    }
    
    @ScriptMethod 
    (
            help="Gets the id of the currently rendering page.\nReturns null if a page is not being rendered."
    )
    public String getPageId()
    {
        return context.getPageId();
    }    
    
    @ScriptMethod 
    (
            help="Gets the currently rendering page.",
            output="The ScriptModelObject for the Page or null if a page is not being rendered."
    )
    public ScriptModelObject getPage()
    {
        if (scriptPageObject == null)
        {
            Page page = context.getPage();
            if (page != null)
            {
                scriptPageObject = new ScriptModelObject(this.context, page);
            }
        }
        
        return scriptPageObject;
    }
    
    @ScriptMethod 
    (
            help="Gets the id of the currently rendering template.\nReturns null if a template is not being rendered."
    )
    public String getTemplateId()
    {
        return context.getTemplateId();
    }    
    
    @ScriptMethod 
    (
            help="Gets the currently rendering template.",
            output="The ScriptModelObject for the Template or null if a template is not being rendered."
    )
    public ScriptModelObject getTemplate()
    {
        if (scriptTemplateObject == null)
        {
            TemplateInstance template = context.getTemplate();
            if (template != null)
            {
                scriptTemplateObject = new ScriptModelObject(this.context, template);
            }
        }
        
        return scriptTemplateObject;
    }
    
    @ScriptMethod 
    (
            help="Gets the currently rendering theme id."
    )
    public String getThemeId()
    {
        return context.getThemeId();
    }
    
    @ScriptMethod 
    (
            help="Gets the currently rendering theme.",
            output="The ScriptModelObject for the Theme or null if a theme is not being rendered."            
    )
    public ScriptModelObject getTheme()
    {
        if (scriptThemeObject == null)
        {
            Theme theme = context.getTheme();
            if (theme != null)
            {
                scriptThemeObject = new ScriptModelObject(this.context, theme);
            }
        }
        
        return scriptThemeObject;
    }
    
    @ScriptMethod 
    (
            help="Sets the theme for the current user session by providing the theme id."
    )
    public void setThemeId(String themeId)
    {
        ThemeUtil.setCurrentThemeId(renderContext, themeId);
    }
    
    @ScriptMethod 
    (
            help="Gets the site configuration.",
            output="The ScriptModelObject for the site configuration or null if a site configuration is not set up."                
    )
    public ScriptModelObject getSiteConfiguration()
    {
        return new ScriptModelObject(renderContext, renderContext.getSiteConfiguration());
    }
    
    @ScriptMethod 
    (
            help="Gets the user object.",
            output="The ScriptUser for the current user or null if a user is not logged in."                
    )
    public ScriptUser getUser()
    {
        if (scriptUser == null)
        {
            User user = context.getUser();
            if (user != null)
            {
                scriptUser = new ScriptUser(context, user);
            }
        }
        
        return scriptUser;
    } 
    
    @ScriptMethod 
    (
            help="Returns whether the current user is authenticated."
    )
    public boolean getAuthenticated()
    {
        HttpServletRequest request = ServletUtil.getRequest();
        return AuthenticationUtil.isAuthenticated(request);           
    }
    
    @ScriptMethod 
    (
            help="Returns whether the current user is externally authenticated."
    )
    public boolean getExternalAuthentication()
    {
        HttpServletRequest request = ServletUtil.getRequest();
        return AuthenticationUtil.isExternalAuthentication(request);
    }
    
    @ScriptMethod 
    (
            help="Returns the ScriptLinkBuilder instance for the current request."
    )
    public ScriptLinkBuilder getLinkBuilder()
    {
        if (scriptLinkBuilder == null)
        {
            scriptLinkBuilder = new ScriptLinkBuilder(context);         
        }
        return scriptLinkBuilder;
    }    
    
    public String getWebsiteTitle()
    {
        return context.getWebsiteTitle();
    }
    
    public String getUri()
    {
        return context.getUri();
    }
    
    @ScriptMethod 
    (
            help="Gets the root page for the site.",
            output="The ScriptModelObject for the root page."
    )
    public ScriptModelObject getRootPage()
    {
        if (scriptRootPageObject == null)
        {
            Page rootPage = context.getRootPage();
            if (rootPage != null)
            {
                scriptRootPageObject = new ScriptModelObject(this.context, rootPage);
            }
        }
        
        return scriptRootPageObject;        
    }
    
    public String getPreviewWebappId()
    {
        return context.getServiceRegistry().getObjectPersistenceService().getPersistenceContext().getWebappId();
    }
    
    public String getPreviewStoreId()
    {
        return context.getServiceRegistry().getObjectPersistenceService().getPersistenceContext().getStoreId();
    }

    public String getPreviewUserId()
    {
        return context.getServiceRegistry().getObjectPersistenceService().getPersistenceContext().getUserId();
    }

    public String getFrameworkTitle()
    {
        return WebFrameworkConstants.FRAMEWORK_TITLE;
    }
    
    public String getFrameworkVersion()
    {
        return WebFrameworkConstants.FRAMEWORK_VERSION;
    }
    
    public ScriptResource loadResource(String resourceId)
    {
        ScriptResource scriptResource = null;
        
        try
        {
            Resource resource = context.getServiceRegistry().getResourceService().getResource(resourceId);
            if (resource != null)
            {
                scriptResource = new ScriptResource(context, resource);
            }
        }
        catch (ResourceLoaderException rle)
        {
            rle.printStackTrace();
        }
        
        return scriptResource;
    }

    public ScriptResource loadResource(String protocolId, String endpointId, String objectId)
    {
        ScriptResource scriptResource = null;
        
        try
        {
            Resource resource = context.getServiceRegistry().getResourceService().getResource(protocolId, endpointId, objectId);
            if (resource != null)
            {
                scriptResource = new ScriptResource(context, resource);
            }
        }
        catch (ResourceLoaderException rle)
        {
            rle.printStackTrace();
        }
        
        return scriptResource;
    }
    
    @ScriptMethod 
    (
            help="A key/value map of parameters in the incoming request."
    )
    public ScriptableMap getParameters()
    {
        if (this.parameters == null)
        {
            this.parameters = new ScriptableWrappedMap(context.getParameters());
        }
        
        return this.parameters;
    }

    public ScriptableMap getAttributes()
    {
        if (this.attributes == null)
        {
            this.attributes = new ScriptableWrappedMap(context.getAttributes());
        }
        
        return this.attributes;
    }

    public ScriptableMap getHeaders()
    {
        if (this.headers == null)
        {
            this.headers = new ScriptableWrappedMap(context.getHeaders());
        }
        
        return this.headers;
    }
    
}
