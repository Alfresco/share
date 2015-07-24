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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Scriptable;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigElement.TypeDescriptor;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Chrome;
import org.springframework.extensions.surf.types.Component;
import org.springframework.extensions.surf.types.Configuration;
import org.springframework.extensions.surf.types.ContentAssociation;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.PageAssociation;
import org.springframework.extensions.surf.types.PageType;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.surf.types.TemplateType;
import org.springframework.extensions.surf.types.Theme;
import org.springframework.extensions.surf.util.DataUtil;
import org.springframework.extensions.surf.util.EncodingUtil;
import org.springframework.extensions.surf.util.ParameterCheck;
import org.springframework.extensions.webscripts.Description.RequiredCache;
import org.springframework.extensions.webscripts.Description.RequiredTransactionParameters;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.User;

/**
 * <p>A read-only root-scoped Java object for working with the Web Framework 
 * and the Web Framework object model.
 * </p><p>
 * Using this object, you can query the Web Framework object model,
 * perform read and write operations and configure your web application.
 * </p><p>
 * Among the things that you can work against in the Web Framework
 * object model are components, page, templates, configurations and associations.
 * </p>
 * @author muzquiano
 */
public final class ScriptSiteData extends ScriptBase
{
    private static final long serialVersionUID = 3903403611285316447L;

    private static Log logger = LogFactory.getLog(ScriptSiteData.class);
    
    private static final String WEBSCRIPTS_REGISTRY = "webscripts.registry";

    /**
     * The <code>ApplicationContext</code> is required by the <code>findWebScripts</code> method.</p>
     */
    private ApplicationContext applicationContext;
    
    /**
     * Constructs a new ScriptSite object around the provided request context
     * 
     * @param context            The RenderContext instance for the current request
     * @param applicationContext Required by the <code>findWebScripts</code> method.
     */
    public ScriptSiteData(RequestContext context, ApplicationContext applicationContext)
    {
        super(context);
        this.applicationContext = applicationContext;
    }
    
    // no properties
    public ScriptableMap buildProperties()
    {
        return null;
    }
    

    // --------------------------------------------------------------
    // JavaScript Properties
    //
    
    /**
     * Provides access to the root page for the web application.  If no
     * root page is defined, null will be returned.
     * 
     * @return  The root page to the web application.
     */
    public ScriptModelObject getRootPage()
    {
        ModelObject modelObject = context.getRootPage();
        return ScriptHelper.toScriptModelObject(context, modelObject);
    }
    
    /**
     * Provides access to the site configuration object for the web
     * application.  If a site configuration is not defined, null
     * will be returned.
     * 
     * @return The configuration object for the site
     */
    public ScriptModelObject getSiteConfiguration()
    {
        ModelObject modelObject = context.getSiteConfiguration();
        return ScriptHelper.toScriptModelObject(context, modelObject);
    }
        
    // --------------------------------------------------------------
    // JavaScript Functions
    
    public String[] getObjectTypeIds()
    {
        return this.getConfig().getTypeIds();
    }
    
    public String getObjectTypeName(String objectTypeId)
    {
        String objectTypeName = null;
        
        TypeDescriptor typeDescriptor = this.getConfig().getTypeDescriptor(objectTypeId);
        if (typeDescriptor != null)
        {
            objectTypeName = typeDescriptor.getName();
        }
        
        return objectTypeName;
    }

    public String getObjectTypeDescription(String objectTypeId)
    {
        String objectTypeDescription = null;
        
        TypeDescriptor typeDescriptor = this.getConfig().getTypeDescriptor(objectTypeId);
        if (typeDescriptor != null)
        {
            objectTypeDescription = typeDescriptor.getDescription();
        }
        
        return objectTypeDescription;
    }
    
    /**
     * @return  An array of all objects of the given type
     */
    public Object[] getObjects(String objectTypeId)
    {
        return ScriptHelper.toScriptModelObjectArray(context, getObjectService().findObjects(objectTypeId));
    }
    
    /**
     * @return  An array of all Chrome instances in the web application
     */
    public Object[] getChrome()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getObjectService().findChrome());
    }
    
    /**
     * @return  An array of all Component instances in the web application
     */
    public Object[] getComponents()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getObjectService().findComponents());
    }

    /**
     * @return  An array of all ComponentType instances in the web application
     */    
    public Object[] getComponentTypes()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getObjectService().findComponentTypes());
    }

    /**
     * @return  An array of all Configuration instances in the web application
     */    
    public Object[] getConfigurations()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getObjectService().findConfigurations());
    }

    /**
     * @return  An array of all ContentAssociation instances in the web application
     */    
    public Object[] getContentAssociations()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getObjectService().findContentAssociations());
    }

    /**
     * @return  An array of all Page instances in the web application
     */    
    public Object[] getPages()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getObjectService().findPages());
    }

    /**
     * @return  An array of all PageType instances in the web application
     */    
    public Object[] getPageTypes()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getObjectService().findPageTypes());
    }
    
    /**
     * @return  An array of all PageAssociation instances in the web application
     */    
    public Object[] getPageAssociations()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getObjectService().findPageAssociations());
    }

    /**
     * @return  An array of all Template instances in the web application
     */    
    public Object[] getTemplates()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getObjectService().findTemplates());
    }

    /**
     * @return  An array of all TemplateType instances in the web application
     */    
    public Object[] getTemplateTypes()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getObjectService().findTemplateTypes());
    }
    
    /**
     * @return  An array of all Theme instances in the web application
     */    
    public Object[] getThemes()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getObjectService().findThemes());
    }
        
    /**
     * @return A map of all instances of the given type.  The map is keyed 
     *          on object id
     */
    public Scriptable getObjectsMap(String objectTypeId)
    {
        return ScriptHelper.toScriptableMap(context, getObjectService().findObjects(objectTypeId));
    }    
    
    /**
     * @return A map of all Chrome instances.  The map is keyed
     *          on object id
     */
    public Scriptable getChromeMap()
    {
        return ScriptHelper.toScriptableMap(context, getObjectService().findChrome());
    }    
    
    /**
     * @return A map of all Component instances.  The map is keyed
     *          on object id
     */
    public Scriptable getComponentsMap()
    {
        return ScriptHelper.toScriptableMap(context, getObjectService().findComponents());
    }

    /**
     * @return A map of all ComponentType instances.  The map is keyed
     *          on object id
     */    
    public Scriptable getComponentTypesMap()
    {
        return ScriptHelper.toScriptableMap(context, getObjectService().findComponentTypes());
    }

    /**
     * @return A map of all Configuration instances.  The map is keyed
     *          on object id
     */    
    public Scriptable getConfigurationsMap()
    {
        return ScriptHelper.toScriptableMap(context, getObjectService().findConfigurations());
    }

    /**
     * @return A map of all Content Association instances.  The map is keyed
     *          on object id
     */    
    public Scriptable getContentAssociationsMap()
    {
        return ScriptHelper.toScriptableMap(context, getObjectService().findContentAssociations());
    }

    /**
     * @return A map of all Page instances.  The map is keyed
     *          on object id
     */    
    public Scriptable getPagesMap()
    {
        return ScriptHelper.toScriptableMap(context, getObjectService().findPages());
    }

    /**
     * @return A map of all PageAssociation instances.  The map is keyed
     *          on object id
     */    
    public Scriptable getPageAssociationsMap()
    {
        return ScriptHelper.toScriptableMap(context, getObjectService().findPageAssociations());
    }

    /**
     * @return A map of all Template instances.  The map is keyed
     *          on object id
     */
    public Scriptable getTemplatesMap()
    {
        return ScriptHelper.toScriptableMap(context, getObjectService().findTemplates());
    }

    /**
     * @return A map of all TemplateType instances.  The map is keyed
     *          on object id
     */    
    public Scriptable getTemplateTypesMap()
    {
        return ScriptHelper.toScriptableMap(context, getObjectService().findTemplateTypes());
    }
    
    /**
     * @return A map of all Theme instances.  The map is keyed on
     *          on object id
     */    
    public Scriptable getThemesMap()
    {
        return ScriptHelper.toScriptableMap(context, getObjectService().findThemes());
    }    

    /**
     * Creates a new object for the given type id
     * 
     * @param objectTypeId String
     * @return A ScriptModelObject representing the new instance
     */
    public ScriptModelObject newObject(String objectTypeId)
    {
        ModelObject modelObject = getObjectService().newObject(objectTypeId);
        return ScriptHelper.toScriptModelObject(context, modelObject);
    }

    /**
     * Creates a new object for the given type id
     * 
     * @param objectTypeId String
     * @param objectId String
     * @return A ScriptModelObject representing the new instance
     */
    public ScriptModelObject newObject(String objectTypeId, String objectId)
    {
        ModelObject modelObject = getObjectService().newObject(objectTypeId, objectId);
        return ScriptHelper.toScriptModelObject(context, modelObject);
    }
    
    /**
     * Creates a new Chrome instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @return A ScriptModelObject representing the new instance
     */
    public ScriptModelObject newChrome()
    {
        Chrome chrome = (Chrome) getObjectService().newChrome();
        return ScriptHelper.toScriptModelObject(context, chrome);
    }    
    
    /**
     * Creates a new Component instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator. The scope, region and sourceId parameters should be
     * explicitly set before the component is persisted!
     * 
     * @return A ScriptModelObject representing the new instance
     */
    public ScriptModelObject newComponent()
    {
        Component component = (Component) getObjectService().newComponent();
        return ScriptHelper.toScriptModelObject(context, component);
    }

    /**
     * Creates a new Component instance of the given component type
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator. The scope, region and sourceId parameters should be
     * explicitly set before the component is persisted!
     * 
     * @param componentTypeId   The id of the ComponentType which describes 
     *                          the type of this component
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newComponent(String componentTypeId)
    {
        ParameterCheck.mandatoryString("componentTypeId", componentTypeId);

        Component component = (Component) getObjectService().newComponent();
        component.setComponentTypeId(componentTypeId);
        return ScriptHelper.toScriptModelObject(context, component);
    }

    /**
     * Creates a new Component instance of the given component type. The ID is
     * generated from the supplied scope, region and sourceId parameters.
     * 
     * @param scope         Scope - one of "global", "template" or "page"
     * @param regionId      The id of the region to bind too
     * @param sourceId      The source ID for the given scope
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newComponent(String scope, String regionId, String sourceId)
    {
        ParameterCheck.mandatoryString("scope", scope);
        ParameterCheck.mandatoryString("regionId", regionId);
        ParameterCheck.mandatoryString("sourceId", sourceId);
        
        Component component = (Component) getObjectService().newComponent(scope, regionId, sourceId);
        component.setScope(scope);
        component.setRegionId(regionId);
        component.setSourceId(sourceId);
        return ScriptHelper.toScriptModelObject(context, component);
    }
    
    /**
     * Creates a new Component instance of the given component type. The ID is
     * generated from the supplied scope, region and sourceId parameters.
     * 
     * @param componentTypeId   The id of the ComponentType which describes 
     *                          the type of this component.
     * @param scope         Scope - one of "global", "template" or "page"
     * @param regionId      The id of the region to bind too
     * @param sourceId      The source ID for the given scope
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newComponent(String componentTypeId, String scope, String regionId, String sourceId)
    {
        ParameterCheck.mandatoryString("componentTypeId", componentTypeId);
        ParameterCheck.mandatoryString("scope", scope);
        ParameterCheck.mandatoryString("regionId", regionId);
        ParameterCheck.mandatoryString("sourceId", sourceId);
        
        Component component = (Component) getObjectService().newComponent(scope, regionId, sourceId);
        component.setComponentTypeId(componentTypeId);
        component.setScope(scope);
        component.setRegionId(regionId);
        component.setSourceId(sourceId);
        return ScriptHelper.toScriptModelObject(context, component);
    }

    /**
     * Creates a new ComponentType instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @return A ScriptModelObject representing the new instance
     */
    public ScriptModelObject newComponentType()
    {
        ModelObject modelObject = getObjectService().newComponentType();
        return ScriptHelper.toScriptModelObject(context, modelObject);
    }

    /**
     * Creates a new Configuration instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newConfiguration()
    {
        Configuration configuration = (Configuration) getObjectService().newConfiguration();
        return ScriptHelper.toScriptModelObject(context, configuration);
    }

    /**
     * Creates a new Configuration instance that is bound to the given sourceId.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     *
     * @param sourceId The value to assign to the sourceId property
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newConfiguration(String sourceId)
    {
        ParameterCheck.mandatoryString("sourceId", sourceId);

        Configuration configuration = (Configuration) getObjectService().newConfiguration();
        configuration.setSourceId(sourceId);
        return ScriptHelper.toScriptModelObject(context, configuration);
    }
    
    /**
     * Creates a new ContentAssociation instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     *
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newContentAssociation()
    {
        ContentAssociation association = (ContentAssociation) getObjectService().newContentAssociation();
        return ScriptHelper.toScriptModelObject(context, association);
    }    

    /**
     * Creates a new Page instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newPage()
    {
        Page page = (Page) getObjectService().newPage();
        return ScriptHelper.toScriptModelObject(context, page);
    }

    /**
     * Creates a new Page instance with the specified ID.
     * 
     * @param id  The id of the page instance
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newPage(String id)
    {
        ParameterCheck.mandatoryString("id", id);
        Page page = (Page) getObjectService().newPage(id);
        return ScriptHelper.toScriptModelObject(context, page);
    }
    
    /**
     * Creates a new Page instance with the specified ID.
     * 
     * @param id  The id of the page instance
     * @param title The title of the page instance
     * @param description The description of the page instance
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newPage(String id, String title, String description)
    {
        ParameterCheck.mandatoryString("id", id);
        ParameterCheck.mandatoryString("title", title);
        ParameterCheck.mandatoryString("description", description);
        
        Page page = (Page) getObjectService().newPage(id);
        page.setTitle(title);
        page.setDescription(description);
        return ScriptHelper.toScriptModelObject(context, page);
    }
    
    /**
     * Creates a new Page instance with the specified ID.
     * 
     * @param id  The id of the page instance
     * @param title The title of the page instance
     * @param titleId Message bundle key used to look up the title of the page instance
     * @param description The description of the page instance
     * @param descriptionId Message bundle key used to look up the description of the page instance
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newPage(String id, String title, String titleId, String description, String descriptionId)
    {
        ParameterCheck.mandatoryString("id", id);
        ParameterCheck.mandatoryString("title", title);
        ParameterCheck.mandatoryString("description", description);
        
        Page page = (Page) getObjectService().newPage(id);
        page.setTitle(title);
        page.setDescription(description);
        page.setTitleId(titleId);
        page.setDescriptionId(descriptionId);
        return ScriptHelper.toScriptModelObject(context, page);
    }
    
    /**
     * Creates a new PageAssociation instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     *
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newPageAssociation()
    {
        PageAssociation association = (PageAssociation) getObjectService().newPageAssociation();
        return ScriptHelper.toScriptModelObject(context, association);
    }

    /**
     * Creates a new PageType instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     *
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newPageType(String objectId)
    {
        PageType pageType = (PageType) getObjectService().newPageType(objectId);
        return ScriptHelper.toScriptModelObject(context, pageType);
    }
    
    /**
     * Creates a new Template instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newTemplate()
    {
        TemplateInstance template = (TemplateInstance) getObjectService().newTemplate();
        return ScriptHelper.toScriptModelObject(context, template);
    }

    /**
     * Creates a new Template instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @param templateTypeId  The id of the TemplateType object that describes
     *                      the type of this template
     *                      
     * @return A ScriptModelObject representing the new instance
     */
    public ScriptModelObject newTemplate(String templateTypeId)
    {
        ParameterCheck.mandatoryString("templateTypeId", templateTypeId);
        TemplateInstance template = (TemplateInstance) getObjectService().newTemplate();
        template.setTemplateTypeId(templateTypeId);
        return ScriptHelper.toScriptModelObject(context, template);
    }

    /**
     * Creates a new Template instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @param templateTypeId   The id of the TemplateType which describes 
     *                         the type of this template.
     * @param title The name of the Template instance
     * @param description The description of the Template instance
     *  
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newTemplate(String templateTypeId, String title, String description)
    {
        ParameterCheck.mandatoryString("templateTypeId", templateTypeId);
        ParameterCheck.mandatoryString("title", title);
        ParameterCheck.mandatoryString("description", description);
        
        TemplateInstance template = (TemplateInstance) getObjectService().newTemplate();
        template.setTemplateTypeId(templateTypeId);
        template.setTitle(title);
        template.setDescription(description);
        return ScriptHelper.toScriptModelObject(context, template);
    }
    
    /**
     * Creates a new Template instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @param templateTypeId   The id of the TemplateType which describes 
     *                         the type of this template.
     * @param title The name of the Template instance
     * @param titleId Message bundle key used to look up the title of the Template instance
     * @param description The description of the Template instance
     * @param descriptionId Message bundle key used to look up the description of the Template instance
     *  
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newTemplate(String templateTypeId, String title, String titleId, String description, String descriptionId)
    {
        ParameterCheck.mandatoryString("templateTypeId", templateTypeId);
        ParameterCheck.mandatoryString("title", title);
        ParameterCheck.mandatoryString("description", description);
        
        TemplateInstance template = (TemplateInstance) getObjectService().newTemplate();
        template.setTemplateTypeId(templateTypeId);
        template.setTitle(title);
        template.setTitleId(titleId);
        template.setDescription(description);
        template.setDescriptionId(descriptionId);
        return ScriptHelper.toScriptModelObject(context, template);
    }
    
    /**
     * Creates a new TemplateType instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @param objectId   The id of the TemplateType 
     *  
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newTemplateType(String objectId)
    {
        TemplateType templateType = (TemplateType) getObjectService().newTemplateType(objectId);
        return ScriptHelper.toScriptModelObject(context, templateType);
    }

    /**
     * Creates a new Theme instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @param objectId   The id of the Theme 
     *  
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newTheme(String objectId)
    {
        Theme theme = (Theme) getObjectService().newTheme(objectId);
        return ScriptHelper.toScriptModelObject(context, theme);
    }    
    
    /**
     * Creates model objects based on a given preset id. The preset is looked up and
     * processed by the PresetManager bean. The various objects found in the preset
     * will be generated using the supplied name/value map of tokens.
     * 
     * @param presetId  ID of the preset to generate
     * @param tokens    Token name/value map
     * @return true on success, false otherwise
     */
    public boolean newPreset(String presetId, Scriptable tokens) throws ModelObjectPersisterException
    {
        ParameterCheck.mandatoryString("presetId", presetId);
        Map<String, String> t = null;
        Object val = ScriptValueConverter.unwrapValue(tokens);
        if (val instanceof Map)
        {
            t = (Map)val;
        }
        return FrameworkUtil.getServiceRegistry().getPresetsManager().constructPreset(presetId, t);
    }

    /**
     * Searches for Component instances within the Web Application that 
     * match the provided constraints.  If a constraint is set to null, 
     * it is not considered as part of the search.
     * 
     * @param scope     The value of the "scope" property of the instance
     * @param regionId  The value of the "region" property of the instance
     * @param sourceId  The value of the "sourceId" property of the instance
     * @param componentTypeId   The value of the "componentTypeId" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          Component results of the search
     */
    public Object[] findComponents(String scope, String regionId, String sourceId, String componentTypeId)
    {
        Map<String, ModelObject> objects = getObjectService().findComponents(scope, regionId, sourceId, componentTypeId);
        return ScriptHelper.toScriptModelObjectArray(context, objects);
    }
    
    /**
     * Searches for webscript components with the given family name.
     * 
     * @param family        the family
     * 
     * @return An array of webscripts that match the given family name
     */
    public Object[] findWebScripts(String family)
    {
        List<Object> values = new ArrayList<Object>(16);
        
        Registry registry = (Registry) applicationContext.getBean(WEBSCRIPTS_REGISTRY);
        for (WebScript webscript : registry.getWebScripts())
        {
            if (family != null)
            {
            	Set<String> familys = webscript.getDescription().getFamilys();
                if (familys != null && familys.contains(family))
                {
                    values.add(new ScriptWebScript(webscript.getDescription()));
                }
            }
            else
            {
                values.add(new ScriptWebScript(webscript.getDescription()));
            }
        }
        
        return values.toArray(new Object[values.size()]);
    }
    
    public static class ScriptWebScript
    {
        private Description description;
        
        ScriptWebScript(Description description)
        {
            this.description = description;
        }
        
        public ArgumentTypeDescription[] getArguments()
        {
            return description.getArguments();
        }

        public String getDefaultFormat()
        {
            return description.getDefaultFormat();
        }

        public String getDescDocument()
        {
            try
            {
                return DataUtil.copyToString(description.getDescDocument(), "UTF-8", true);
            }
            catch (IOException e)
            {
                return "";
            }
        }

        public String getDescPath()
        {
            return description.getDescPath();
        }

        public Map<String, Serializable> getExtensions()
        {
            return description.getExtensions();
        }

        public String[] getFamilys()
        {
            List<String> familys = new ArrayList<String>(4);
            for (String f : description.getFamilys())
            {
                familys.add(f);
            }
            return familys.toArray(new String[familys.size()]);
        }

        public String getFormatStyle()
        {
            return description.getFormatStyle().toString();
        }

        public String getKind()
        {
            return description.getKind();
        }

        public String getLifecycle()
        {
            return description.getLifecycle().toString();
        }

        public String getMethod()
        {
            return description.getMethod();
        }

        public boolean getMultipartProcessing()
        {
            return description.getMultipartProcessing();
        }

        public NegotiatedFormat[] getNegotiatedFormats()
        {
            return description.getNegotiatedFormats();
        }

        public Path getPackage()
        {
            return description.getPackage();
        }

        public TypeDescription[] getRequestTypes()
        {
            return description.getRequestTypes();
        }

        public String getRequiredAuthentication()
        {
            return description.getRequiredAuthentication().toString();
        }

        public RequiredCache getRequiredCache()
        {
            return description.getRequiredCache();
        }

        public String getRequiredTransaction()
        {
            return description.getRequiredTransaction().toString();
        }

        public RequiredTransactionParameters getRequiredTransactionParameters()
        {
            return description.getRequiredTransactionParameters();
        }

        public TypeDescription[] getResponseTypes()
        {
            return description.getResponseTypes();
        }

        public String getRunAs()
        {
            return description.getRunAs();
        }

        public String getScriptPath()
        {
            return description.getScriptPath();
        }

        public String getStorePath()
        {
            return description.getStorePath();
        }

        public String[] getURIs()
        {
            return description.getURIs();
        }

        public String getDescription()
        {
            return description.getDescription();
        }

        public String getId()
        {
            return description.getId();
        }

        public String getShortName()
        {
            return description.getShortName();
        }
    }

    /**
     * Searches for PageAssociation instances within the Web Application that 
     * are of association type 'child' and which match the specified 
     * constraints. If a constraint is set to null, it is not considered as 
     * part of the search.
     * 
     * @param sourceId   The value of the "sourceId" property of the instance
     * @param destId  The value of the "destId" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          PageAssociation results of the search
     */    
    public Object[] findChildPageAssociations(String sourceId, String destId)
    {
        return findPageAssociations(sourceId, destId, "child");
    }

    /**
     * Searches for PageAssociation instances within the Web Application that 
     * are of association type 'child' and which match the specified
     * constraints.  If a constraint is set to null, it is not considered as
     * part of the search.
     * 
     * @param sourceId   The value of the "sourceId" property of the instance
     * @param destId  The value of the "destId" property of the instance
     * @param associationType   The value of the "associationType" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          PageAssociation results of the search
     */        
    public Object[] findPageAssociations(String sourceId, String destId, String associationType)
    {
        Map<String, ModelObject> objects = getObjectService().findPageAssociations(
                sourceId, destId, associationType);
        return ScriptHelper.toScriptModelObjectArray(context, objects);
    }
    
    /**
     * Searches for child pages of the given page.
     * 
     * This is a shortcut method - the alternative is to look up associations directly and then look up
     * their corresponding page objects
     * 
     * @param sourceId String
     * @return Object[]
     */
    public Object[] findChildPages(String sourceId)
    {
        Map<String, ModelObject> pageAssociations = getObjectService().findPageAssociations(
                sourceId, null, "child");

        ArrayList<Page> list = new ArrayList<Page>(16);
        
        Iterator it = pageAssociations.values().iterator();
        while (it.hasNext())
        {
            PageAssociation pageAssociation = (PageAssociation) it.next();
            Page page = pageAssociation.getDestPage(context);
            if (page != null)
            {
                list.add(page);
            }
            else
            {
                // debug to framework logger
                logger.debug("Unable to find page object for page association id: " + pageAssociation.getId());
            }
        }
        
        Page[] pages = list.toArray(new Page[list.size()]);
        return ScriptHelper.toScriptModelObjectArray(context, pages);
    }    

    /**
     * Searches for parent pages of the given page.
     * 
     * This is a shortcut method - the alternative is to look up associations directly and then look up
     * their corresponding page objects
     * 
     * @param pageId String
     * @return Object[]
     */
    public Object[] findParentPages(String pageId)
    {
        Map<String, ModelObject> pageAssociations = getObjectService().findPageAssociations(
                null, pageId, "child");

        ArrayList<Page> list = new ArrayList<Page>(16);
        
        Iterator it = pageAssociations.values().iterator();
        while (it.hasNext())
        {
            PageAssociation pageAssociation = (PageAssociation) it.next();
            Page page = pageAssociation.getSourcePage(context);
            if (page != null)
            {
                list.add(page);
            }
            else
            {
                // debug to framework logger
                logger.debug("Unable to find page object for page association id: " + pageAssociation.getId());
            }
        }
        
        Page[] pages = list.toArray(new Page[list.size()]);
        return ScriptHelper.toScriptModelObjectArray(context, pages);
    }    
    
    /**
     * Searches for ContentAssociation instances within the Web Application that 
     * match the specified constraints.  If a constraint is set to null, 
     * it is not considered as part of the search.
     * 
     * @param sourceId   The value of the "sourceId" property of the instance
     * @param sourceType  The value of "sourceType" property of the instance
     * @param destId  The value of the "destId" property of the instance
     * @param assocType  The value of the "assocType" property of the instance
     * @param formatId  The value of the "formatId" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          ContentAssociation results of the search
     */        
    public Object[] findContentAssociations(String sourceId, String sourceType, String destId, String assocType, String formatId)
    {
        Map<String, ModelObject> objects = getObjectService().findContentAssociations(
                sourceId, sourceType, destId, assocType, formatId);
        return ScriptHelper.toScriptModelObjectArray(context, objects);
    }
    
    /**
     * Provides a map of ScriptModelObjects that wrap Component instances.
     * The map is keyed by Component object id.
     * 
     * @param scope      The value of the "source" property of the instance 
     * @param regionId   The value of the "regionId" property of the instance
     * @param sourceId   The value of the "sourceId" property of the instance
     * @param componentTypeId  The value of the "componentTypeId" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          Component results of the search
     */            
    public Scriptable findComponentsMap(String scope, String regionId, String sourceId, String componentTypeId)
    {
        Map<String, ModelObject> objects = getObjectService().findComponents(scope, regionId, sourceId, componentTypeId);
        return ScriptHelper.toScriptableMap(context, objects);
    }

    /**
     * Provides a map of ScriptModelObjects that wrap PageAssociation instances.
     * The map is keyed by PageAssociation object id.
     * 
     * @param sourceId   The value of the "sourceId" property of the instance
     * @param destId  The value of the "destId" property of the instance
     * @param associationType  The value of the "associationType" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          PageAssociation results of the search
     */            
    public Scriptable findPageAssociationsMap(String sourceId, String destId, String associationType)
    {
        Map<String, ModelObject> objects = getObjectService().findPageAssociations(
                sourceId, destId, associationType);
        return ScriptHelper.toScriptableMap(context, objects);
    }

    /**
     * Provides a map of ScriptModelObjects that wrap ContentAssociation instances.
     * The map is keyed by ContentAssociation object id.
     * 
     * @param sourceId   The value of the "sourceId" property of the instance
     * @param sourceType  The value of the "sourceType" property of the instance
     * @param destId  The value of the "destId" property of the instance
     * @param assocType  The value of the "assocType" property of the instance
     * @param formatId  The value of the "formatId" property of the instance 
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          ContentAssociation results of the search
     */            
    public Scriptable findContentAssociationsMap(String sourceId, String sourceType, String destId, String assocType, String formatId)
    {
        Map<String, ModelObject> objects = getObjectService().findContentAssociations(
                sourceId, sourceType, destId, assocType, formatId);
        return ScriptHelper.toScriptableMap(context, objects);
    }

    /**
     * Provides a map of ScriptModelObjects that wrap Template instances.
     * The map is keyed by format id.
     * 
     * @param pageId  The value of the "pageId" property of the instance 
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          Template instance results of the search
     */                    
    public Scriptable findTemplatesMap(String pageId)
    {
        Page page = getObjectService().getPage(pageId);
        if (page != null)
        {
            Map<String, TemplateInstance> templatesMap = page.getTemplates(context);
            
            ScriptableMap<String, Serializable> map = new ScriptableLinkedHashMap<String, Serializable>(templatesMap.size());
            Iterator it = templatesMap.keySet().iterator();
            while (it.hasNext())
            {
                String formatId = (String) it.next();
                TemplateInstance template = templatesMap.get(formatId);
                
                ScriptModelObject scriptModelObject = ScriptHelper.toScriptModelObject(context, template);
                map.put(formatId, scriptModelObject);
            }
            return map; 
        }
        return null;
    }

    /**
     * Looks up Configuration instances and returns the first instance
     * that is found for the matching constraints.
     * 
     * @param sourceId The value of the "sourceId" property
     * 
     * @return A ScriptModelObject instance that wraps the Configuration instance
     */
    public ScriptModelObject findConfiguration(String sourceId)
    {
        ScriptModelObject scriptModelObject = null;
        
        Map<String, ModelObject> objects = getObjectService().findConfigurations(sourceId);
        if (objects.size() > 0)
        {
            ModelObject object = (ModelObject) objects.values().iterator().next();
            scriptModelObject = ScriptHelper.toScriptModelObject(context, object);
        }
        
        return scriptModelObject;
    }

    /**
     * Looks up Template instances and returns the first instance
     * that is found for the matching constraints.
     * 
     * @param pageId The value of the "pageId" property
     * 
     * @return A ScriptModelObject instance that wraps the Template instance
     */
    public ScriptModelObject findTemplate(String pageId)
    {
        return findTemplate(pageId, null);
    }

    /**
     * Looks up Template instances and returns the first instance
     * that is found for the matching constraints.
     * 
     * @param pageId The value of the "pageId" property
     * @param formatId The value of the "formatId" property
     * 
     * @return A ScriptModelObject instance that wraps the Template instance
     */
    public ScriptModelObject findTemplate(String pageId, String formatId)
    {
        Page page = (Page) getObjectService().getPage(pageId);
        if (page != null)
        {
            TemplateInstance t = page.getTemplate(context, formatId);
            if (t != null)
            {
                return ScriptHelper.toScriptModelObject(context, t);
            }
        }
        return null;
    }

    /**
     * Looks up the given Page and unbinds any Template instances that
     * are bound to the page (keyed by formatId)
     * 
     * If you would like to remove the default Template instance,
     * set formatId to null
     * 
     * @param pageId    The id of the Page
     * @param formatId  The format
     * @throws ModelObjectPersisterException 
     */
    public void removeTemplate(String pageId, String formatId) throws ModelObjectPersisterException
    {
        Page page = (Page) getObjectService().getPage(pageId);
        if (page != null)
        {
            page.removeTemplateId(formatId);
            getObjectService().saveObject(page);
        }
    }

    
    // Create and Remove Associations

    public void bindComponent(String componentId, String scope, String regionId, String sourceId) throws ModelObjectPersisterException
    {
        getObjectService().bindComponent(componentId, scope, regionId, sourceId);
    }
    
    public void bindComponent(ScriptModelObject componentObject, String scope, String regionId, String sourceId) throws ModelObjectPersisterException
    {
        Component component = (Component) componentObject.getModelObject();
        getObjectService().bindComponent(component, scope, regionId, sourceId);
    }

    public void unbindComponent(String componentId)
    {
        getObjectService().unbindComponent(componentId);
    }
    
    public void unbindComponent(String scope, String regionId, String sourceId)
    {
        getObjectService().unbindComponent(scope, regionId, sourceId);
    }

    public void associateTemplate(String templateId, String pageId) throws ModelObjectPersisterException
    {
        associateTemplate(templateId, pageId, null);
    }

    public void associateTemplate(String templateId, String pageId,
            String formatId) throws ModelObjectPersisterException
    {
        getObjectService().associateTemplate(templateId, pageId, formatId);
    }

    public void unassociateTemplate(String pageId)  throws ModelObjectPersisterException
    {
        unassociateTemplate(pageId, null);
    }

    public void unassociateTemplate(String pageId, String formatId) throws ModelObjectPersisterException
    {
        getObjectService().unassociateTemplate(pageId, formatId);
    }

    public void associatePage(String sourceId, String destId) throws ModelObjectPersisterException
    {
        getObjectService().associatePage(sourceId, destId);
    }

    public void unassociatePage(String sourceId, String destId)
    {
        getObjectService().unassociatePage(sourceId, destId);
    }

    public void associateContent(String contentId, String templateId, String assocType,
            String formatId)  throws ModelObjectPersisterException
    {
        if (assocType == null)
        {
            assocType = "template";
        }
        
        getObjectService().associateContent(contentId, "instance", templateId, assocType, 
            formatId);
    }

    public void unassociateContent(String contentId, String templateId,
            String formatId)
    {
        getObjectService().unassociateContent(contentId, "instance", templateId, 
                null, formatId);
    }

    public void associateContentType(String contentTypeId, String templateId, String assocType,
            String formatId)  throws ModelObjectPersisterException
    {
        if (assocType == null)
        {
            assocType = "template";
        }

        getObjectService().associateContent(contentTypeId, "type", templateId, assocType,
                formatId);
    }

    public void unassociateContentType(String contentTypeId, String templateId,
            String formatId)
    {
        getObjectService().unassociateContent(contentTypeId, "type", templateId,
                null, formatId);
    }

    // helper methods
    public String encode(String input)
    {
        return EncodingUtil.encode(input);
    }

    public String encode(String input, String encoding)
    {
        return EncodingUtil.encode(input, encoding);
    }

    public String decode(String input)
    {
        return EncodingUtil.decode(input);
    }

    public String decode(String input, String encoding)
    {
        return EncodingUtil.decode(input, encoding);
    }    
    
    public void logout()
    {
        AuthenticationUtil.logout(ServletUtil.getRequest(), null);
    }
    
    /**
     * Reloads the current user into session
     */
    public void reloadUser()
    {
        HttpServletRequest request = ServletUtil.getRequest();
        try
        {
            FrameworkUtil.getServiceRegistry().getUserFactory().initialiseUser(context, request, true);
        }
        catch (UserFactoryException ufe)
        {
            logger.warn("Unable to reload current user into session");
        }
    }
    
    
    // returns the credential vault for the current user
    public ScriptCredentialVault getCredentialVault()
    {
        CredentialVault vault = this.context.getCredentialVault();
        User user = this.context.getUser();
        
        return new ScriptCredentialVault(vault, user);
    }
        
    public ScriptModelObject getChrome(String objectId)
    {
        ModelObject obj = getObjectService().getChrome(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }

    public ScriptModelObject getComponent(String objectId)
    {
        ModelObject obj = getObjectService().getComponent(objectId);
        
        // It's possible that the id supplied is not for a Component but for a SubComponent (if so
        // it will contain a "#"). If we haven't been able to retrieve a Component with the id supplied
        // then remove all characters from the id from the "#" onwards and try again...
        if (obj == null && objectId.contains("#"))
        {
            obj = getObjectService().getComponent(objectId.substring(0, objectId.lastIndexOf("#")));
        }
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    public ScriptModelObject getComponent(String scope, String regionId, String sourceId)
    {
        ModelObject obj = getObjectService().getComponent(scope, regionId, sourceId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    public ScriptModelObject getComponentType(String objectId)
    {
        ModelObject obj = getObjectService().getComponentType(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    public ScriptModelObject getConfiguration(String objectId)
    {
        ModelObject obj = getObjectService().getConfiguration(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }

    public ScriptModelObject getContentAssociation(String objectId)
    {
        ModelObject obj = getObjectService().getContentAssociation(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }

    public ScriptModelObject getPage(String objectId)
    {
        ModelObject obj = getObjectService().getPage(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }

    public ScriptModelObject getPageType(String objectId)
    {
        ModelObject obj = getObjectService().getPageType(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    public ScriptModelObject getPageAssociation(String objectId)
    {
        ModelObject obj = getObjectService().getPageAssociation(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    public ScriptModelObject getTemplate(String objectId)
    {
        ModelObject obj = getObjectService().getTemplate(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }

    public ScriptModelObject getTemplateType(String objectId)
    {
        ModelObject obj = getObjectService().getTemplateType(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    public ScriptModelObject getTheme(String objectId)
    {
        ModelObject obj = getObjectService().getTheme(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    /**
     * Constructs a GUID
     * 
     * @return String
     */
    public String newGUID()
    {
        return new org.springframework.extensions.webscripts.GUID().toString();
    }       
    
    public String[] getFormatIds()
    {
        return this.getConfig().getFormatIds();
    }
    
    public String getFormatTitle(String formatId)
    {
        return this.getConfig().getFormatDescriptor(formatId).getName();
    }

    public String getFormatDescription(String formatId)
    {
        return this.getConfig().getFormatDescriptor(formatId).getDescription();
    }
    
    /**
     * Finds an object with the supplied id that matches the supplied value.
     * 
     * @param o The object to search in
     * @param targetAttributeKey The key to search for
     * @param targetAttributeValue The value that the target key should have
     * 
     * @return freemarker node model
     */
    @ScriptMethod
    (
            help="Finds an object with the supplied id that matches the supplied value.",
            output="Freemarker node model"
    )
    public Object findObject(Object o, String targetAttributeKey, String targetAttributeValue)
    {
        return o = ScriptWidgetUtils.findObject(o, targetAttributeKey, targetAttributeValue);
    }
    
    /**
     * Deletes an object with the supplied id that matches the supplied value.
     * 
     * @param o The object to search in
     * @param targetAttributeKey The key to search for
     * @param targetAttributeValue The value that the target key should have
     * 
     * @return freemarker node model
     */
    @ScriptMethod
    (
            help="Deletes an object with the supplied id that matches the supplied value.",
            output="Freemarker node model"
    )
    public Object deleteObjectFromArray(Object o, String targetAttributeKey, String targetAttributeValue)
    {
        return o = ScriptWidgetUtils.deleteObjectFromArray(o, targetAttributeKey, targetAttributeValue);
    }
    
    /**
     * Finds the location of the supplied Dojo package. This should be used rather than explicitly
     * specifying full packages so that Surf configuration can be used to control versions of Dojos
     * and the packages that it uses.
     * @param name The name of the package to find (e.g. "dijit")
     * @return A relative path to the requested Dojo package or a failure string.
     */
    @SuppressWarnings("unchecked")
    @ScriptMethod
    (
            help="Finds the location of the supplied Dojo package as configured in Surf",
            output="String"
    )
    public String getDojoPackageLocation(String name)
    {
        String location = "-UNKNOWN-DOJO-PACKAGE-";
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        ScriptConfigModel config = rc.getExtendedScriptConfigModel(null);
        WebFrameworkConfigElement wfce = null;
        Map<String, ConfigElement> configs = (Map<String, ConfigElement>)config.getScoped().get("WebFramework");
        if (configs != null)
        {
            wfce = (WebFrameworkConfigElement) configs.get("web-framework");
        }
        else
        {
            wfce = this.getConfig();
        }
        Map<String, String> dojoPackages = wfce.getDojoPackages();
        if (dojoPackages != null)
        {
            String dojoPackage = dojoPackages.get(name);
            if (dojoPackage != null)
            {
                location = wfce.getDojoBaseUrl() + dojoPackage;
            }
        }
        return location;
    }
}
