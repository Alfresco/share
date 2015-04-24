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

package org.springframework.extensions.surf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.extensions.surf.exception.ModelObjectPersisterException;
import org.springframework.extensions.surf.render.RenderUtil;
import org.springframework.extensions.surf.resource.Resource;
import org.springframework.extensions.surf.resource.ResourceProvider;
import org.springframework.extensions.surf.types.Chrome;
import org.springframework.extensions.surf.types.Component;
import org.springframework.extensions.surf.types.ComponentType;
import org.springframework.extensions.surf.types.Configuration;
import org.springframework.extensions.surf.types.ContentAssociation;
import org.springframework.extensions.surf.types.SurfBug;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.PageAssociation;
import org.springframework.extensions.surf.types.PageType;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.surf.types.TemplateType;
import org.springframework.extensions.surf.types.Theme;

/**
 * Default implementation of the model.
 *
 * @author muzquiano
 * @author kevinr
 * @author David Draper
 */
public class ModelObjectService
{
    private ObjectPersistenceService objectPersistenceService = null;

    /**
     * Sets the object persistence service
     *
     * @param service object persistence service
     */
    public void setObjectPersistenceService(ObjectPersistenceService service)
    {
        this.objectPersistenceService = service;
    }

    /**
     * Gets the object persistence service
     *
     * @return object persistence service
     */
    public ObjectPersistenceService getObjectPersistenceService()
    {
        return this.objectPersistenceService;
    }

    /**
     * Gets the Chrome instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the chrome
     */
    public Chrome getChrome(String objectId)
    {
        Chrome chrome = (Chrome) getObject(Chrome.TYPE_ID, objectId);
        return chrome;
    }

    /**
     * <p>Gets the SurfBug instance for the id provided. The instance of SurfBug that
     * will be used for debugging should be specified by through the <{@code}surfbug> config
     * element in the Spring Surf configuration. The default SurfBug instance is
     * defined in "default-surfbug.xml" but is can be overridden by application config.</p>
     * 
     * @param objectId The id of the SurfBug to locate.
     * @return The requested SurfBug instance if it can be located.
     */
    public SurfBug getSurfBug(String objectId)
    {
        SurfBug surfbug = (SurfBug) getObject(SurfBug.TYPE_ID, objectId);
        return surfbug;
    }
    
    /**
     * Gets the component instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the component
     */
    public Component getComponent(String objectId)
    {
        Component component = (Component) getObject(Component.TYPE_ID, objectId);
        return component;
    }

    /**
     * Gets the component instance by its binding
     *
     * @param scope the scope
     * @param regionId the region id
     * @param sourceId the source id
     *
     * @return the component
     */
    public Component getComponent(String scope, String regionId, String sourceId)
    {
        String componentId = RenderUtil.generateComponentId(scope, regionId, sourceId);
        Component component = getComponent(componentId);
        return component;
    }

    /**
     * Gets the Component Type instance with the given id
     *
     * @param objectId the object id
     *
     * @return the component type
     */
    public ComponentType getComponentType(String objectId)
    {
        return (ComponentType) getObject(ComponentType.TYPE_ID, objectId);
    }

    /**
     * Gets the Configuration instance with the given id
     *
     * @param objectId the object id
     *
     * @return the configuration
     */
    public Configuration getConfiguration(String objectId)
    {
        return (Configuration) getObject(Configuration.TYPE_ID, objectId);
    }

    /**
     * Gets the Content Association instance with the given id
     *
     * @param objectId the object id
     *
     * @return the content association
     */
    public ContentAssociation getContentAssociation(String objectId)
    {
        return (ContentAssociation) getObject(ContentAssociation.TYPE_ID, objectId);
    }

    /**
     * Gets the Page instance with the given id
     *
     * @param objectId the object id
     *
     * @return the page
     */
    public Page getPage(String objectId)
    {
        return (Page) getObject(Page.TYPE_ID, objectId);
    }

    /**
     * Gets the Page Type instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the page type
     */
    public PageType getPageType(String objectId)
    {
        return (PageType) getObject(PageType.TYPE_ID, objectId);
    }

    /**
     * Gets the Page Association instance with the given id
     *
     * @param objectId the object id
     *
     * @return the page association
     */
    public PageAssociation getPageAssociation(String objectId)
    {
        return (PageAssociation) getObject(PageAssociation.TYPE_ID, objectId);
    }

    /**
     * Gets the Template instance with the given id
     *
     * @param objectId the object id
     *
     * @return the template
     */
    public TemplateInstance getTemplate(String objectId)
    {
        return (TemplateInstance) getObject(TemplateInstance.TYPE_ID, objectId);
    }

    /**
     * Gets the Template Type instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the template type
     */
    public TemplateType getTemplateType(String objectId)
    {
        return (TemplateType) getObject(TemplateType.TYPE_ID, objectId);
    }

    /**
     * Gets the Theme instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the theme
     */
    public Theme getTheme(String objectId)
    {
        return (Theme) getObject(Theme.TYPE_ID, objectId);
    }

    /**
     * Creates a new Chrome instance.
     *
     * @return the chrome
     */
    public Chrome newChrome()
    {
        return (Chrome) newObject(Chrome.TYPE_ID);
    }

    /**
     * Creates a new Chrome instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the chrome
     */
    public Chrome newChrome(String objectId)
    {
        return (Chrome) newObject(Chrome.TYPE_ID, objectId);
    }

    /**
     * Creates a new Component instance.
     *
     * @return the component
     */
    public Component newComponent()
    {
        return (Component) newObject(Component.TYPE_ID);
    }

    /**
     * Creates a new Component instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the component
     */
    public Component newComponent(String objectId)
    {
        return (Component) newObject(Component.TYPE_ID, objectId);
    }

    /**
     * Creates a new Component instance with the given binding properties.
     *
     * @param scope    the scope
     * @param regionId the region id
     * @param sourceId the source id
     *
     * @return the object
     */
    public Component newComponent(String scope, String regionId, String sourceId)
    {
        String componentId = RenderUtil.generateComponentId(scope, regionId, sourceId);

        Component component = newComponent(componentId);
        component.setScope(scope);
        component.setRegionId(regionId);
        component.setSourceId(sourceId);

        return component;
    }

    /**
     * Creates a new Component Type instance.
     *
     * @return the component type
     */
    public ComponentType newComponentType()
    {
        return (ComponentType) newObject(ComponentType.TYPE_ID);
    }

    /**
     * Creates a new Component Type instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the component type
     */
    public ComponentType newComponentType(String objectId)
    {
        return (ComponentType) newObject(ComponentType.TYPE_ID, objectId);
    }

    /**
     * Creates a new Configuration instance.
     *
     * @return the configuration
     */
    public Configuration newConfiguration()
    {
        return (Configuration) newObject(Configuration.TYPE_ID);
    }

    /**
     * Creates a new Configuration instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the configuration
     */
    public Configuration newConfiguration(String objectId)
    {
        return (Configuration) newObject(Configuration.TYPE_ID, objectId);
    }

    /**
     * Creates a new Content Association instance.
     *
     * @return the content association
     */
    public ContentAssociation newContentAssociation()
    {
        return (ContentAssociation) newObject(ContentAssociation.TYPE_ID);
    }

    /**
     * Creates a new Content Association instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the content association
     */
    public ContentAssociation newContentAssociation(String objectId)
    {
        return (ContentAssociation) newObject(ContentAssociation.TYPE_ID, objectId);
    }

    /**
     * Creates a new Page instance.
     *
     * @return the page
     */
    public Page newPage()
    {
        return (Page) newObject(Page.TYPE_ID);
    }

    /**
     * Creates a new Page instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the page
     */
    public Page newPage(String objectId)
    {
        return (Page) newObject(Page.TYPE_ID, objectId);
    }

    /**
     * Creates a new Page Type instance.
     *
     * @return the page type
     */
    public PageType newPageType()
    {
        return (PageType) newObject(PageType.TYPE_ID);
    }

    /**
     * Creates a new Page Type instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the page type
     */
    public PageType newPageType(String objectId)
    {
        return (PageType) newObject(PageType.TYPE_ID, objectId);
    }

    /**
     * Creates a new Page Association instance.
     *
     * @return the page association
     */
    public PageAssociation newPageAssociation()
    {
        return (PageAssociation) newObject(PageAssociation.TYPE_ID);
    }

    /**
     * Creates a new Page Association instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the page association
     */
    public PageAssociation newPageAssociation(String objectId)
    {
        return (PageAssociation) newObject(PageAssociation.TYPE_ID, objectId);
    }

    /**
     * Creates a new Template instance.
     *
     * @return the template instance
     */
    public TemplateInstance newTemplate()
    {
        return (TemplateInstance) newObject(TemplateInstance.TYPE_ID);
    }

    /**
     * Creates a new Template instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the template instance
     */
    public TemplateInstance newTemplate(String objectId)
    {
        return (TemplateInstance) newObject(TemplateInstance.TYPE_ID, objectId);
    }

    /**
     * Creates a new Template Type instance.
     *
     * @return the template type
     */
    public TemplateType newTemplateType()
    {
        return (TemplateType) newObject(TemplateType.TYPE_ID);
    }

    /**
     * Creates a new Template Type instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the template type
     */
    public TemplateType newTemplateType(String objectId)
    {
        return (TemplateType) newObject(TemplateType.TYPE_ID, objectId);
    }

    /**
     * Creates a new Theme instance.
     *
     * @return the theme
     */
    public Theme newTheme()
    {
        return (Theme) newObject(Theme.TYPE_ID);
    }

    /**
     * Creates a new Theme instance with the given id.
     *
     * @param objectId the object id
     *
     * @return the theme
     */
    public Theme newTheme(String objectId)
    {
        return (Theme) newObject(Theme.TYPE_ID, objectId);
    }

    /**
     * Saves the given object.
     *
     * @param object the object
     *
     * @return true, if successful
     * @throws ModelObjectPersisterException 
     */
    public boolean saveObject(ModelObject object) throws ModelObjectPersisterException
    {
        return getObjectPersistenceService().saveObject(object);
    }
    
    /**
     * Saves the given objects.
     *
     * @param objects the objects
     *
     * @return true, if successful
     * @throws ModelObjectPersisterException 
     */
    public boolean saveObjects(List<ModelObject> objects) throws ModelObjectPersisterException
    {
        return getObjectPersistenceService().saveObjects(objects);
    }

    /**
     * Gets an object instance of the given type and with the given id.
     *
     * @param objectTypeId the object type id
     * @param objectId the object id
     *
     * @return the object
     */
    public ModelObject getObject(String objectTypeId, String objectId)
    {
        if (objectTypeId == null)
        {
            throw new IllegalArgumentException("ObjectTypeId is mandatory.");
        }
        if (objectId == null)
        {
            throw new IllegalArgumentException("ObjectId is mandatory.");
        }
        ObjectPersistenceService ops = getObjectPersistenceService();
        ModelObject object = ops.getObject(objectTypeId, objectId);
        return object;
    }

    /**
     * Removes the given object.
     *
     * @param object the object
     *
     * @return true, if successful
     */
    public boolean removeObject(ModelObject object)
    {
        return getObjectPersistenceService().removeObject(object);
    }

    /**
     * Removes the object with the given object type and object id.
     *
     * @param objectTypeId the object type id
     * @param objectId the object id
     *
     * @return true, if successful
     */
    public boolean removeObject(String objectTypeId, String objectId)
    {
        return getObjectPersistenceService().removeObject(objectTypeId, objectId);
    }

    /**
     * Creates a new object instance with the given object type id.
     *
     * @param objectTypeId the object type id
     *
     * @return the model object
     */
    public ModelObject newObject(String objectTypeId)
    {
        return getObjectPersistenceService().newObject(objectTypeId);
    }

    /**
     * Creates new object instance with teh given object type id and object id.
     *
     * @param objectTypeId the object type id
     * @param objectId the object id
     *
     * @return the model object
     */
    public ModelObject newObject(String objectTypeId, String objectId)
    {
        return getObjectPersistenceService().newObject(objectTypeId, objectId);
    }

    /**
     * Returns a map with all object instances of the given type.
     *
     * @param objectTypeId the object type id
     *
     * @return the map
     */
    public Map<String, ModelObject> getAllObjects(String objectTypeId)
    {
        return getObjectPersistenceService().getAllObjects(objectTypeId);
    }

    /**
     * Returns a map with all object instances of the given type filtered
     * by the given object ID filter pattern. This mechanism can be used for all
     * model object types that persist themselves with a deterministically generated
     * path from their respective object IDs, such as pages and components.
     * <p>
     * Object ID patterns support wildcard '*' only i.e. "page.*.dashboard"
     *
     * @param objectTypeId  the object type id
     * @param filter        the object id filter pattern
     *
     * @return the map
     */
    public Map<String, ModelObject> getAllObjects(String objectTypeId, String objectIdPattern)
    {
        return getObjectPersistenceService().getAllObjects(Component.TYPE_ID, objectIdPattern);
    }

    /**
     * Returns a map of Chrome instances
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findChrome()
    {
        return findChrome(null);
    }

    /**
     * Returns a filtered map of Chrome instances.
     *
     * @param chromeType the chrome type
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findChrome(String chromeType)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap, Chrome.PROP_CHROME_TYPE, chromeType);

        // do the lookup
        return findObjects(Chrome.TYPE_ID, propertyConstraintMap);
    }

    /**
     * Returns a map of Configuration instances.
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findConfigurations()
    {
        return findConfigurations(null);
    }

    /**
     * Returns a filtered map of Configuration instances.
     *
     * @param sourceId the source id
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findConfigurations(String sourceId)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        if (sourceId != null)
        {
            addPropertyConstraint(propertyConstraintMap, Configuration.PROP_SOURCE_ID, sourceId);
        }

        // do the lookup
        return findObjects(Configuration.TYPE_ID, propertyConstraintMap);
    }

    /**
     * Returns a map of Page Association instances.
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findPageAssociations()
    {
        return findPageAssociations(null, null, null);
    }

    /**
     * Returns a filtered map of Page Association instances.
     *
     * @param sourceId the source id
     * @param destId the dest id
     * @param associationType the association type
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findPageAssociations(String sourceId,
            String destId, String associationType)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();

        // source id is an optional parameter
        if (sourceId != null)
        {
            addPropertyConstraint(propertyConstraintMap,
                    PageAssociation.PROP_SOURCE_ID, sourceId);
        }

        // dest id is an optional parameter
        if (destId != null)
        {
            addPropertyConstraint(propertyConstraintMap,
                    PageAssociation.PROP_DEST_ID, destId);
        }

        addPropertyConstraint(propertyConstraintMap,
                PageAssociation.PROP_ASSOC_TYPE, associationType);

        // do the lookup
        // this returns an unsorted map
        Map<String, ModelObject> map = findObjects(PageAssociation.TYPE_ID, propertyConstraintMap);

        // Now sort the map by the "order-id" field on the page association
        // Get a list of the entries in the map
        List<Map.Entry<String, ModelObject>> list = new Vector<Map.Entry<String, ModelObject>>(map.entrySet());

        // Sort the list using an anonymous inner class implementing Comparator for the compare method
        java.util.Collections.sort(list, new Comparator<Map.Entry<String, ModelObject>>() {
            public int compare(Map.Entry<String, ModelObject> entry, Map.Entry<String, ModelObject> entry1)
            {
                String sortOrder = entry.getValue().getProperty(PageAssociation.PROP_ORDER_ID);
                String sortOrder1 = entry1.getValue().getProperty(PageAssociation.PROP_ORDER_ID);

                if (sortOrder == null && sortOrder1 == null)
                {
                    return 0;
                }
                if (sortOrder == null)
                {
                    return -1;
                }
                if (sortOrder1 == null)
                {
                    return 1;
                }
                return sortOrder.compareTo(sortOrder1);
            }
        });
        
        // Add the entries now in sorted order
        map = new LinkedHashMap<String, ModelObject>(list.size());
        for (Map.Entry<String, ModelObject> entry: list)
        {
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    /**
     * Returns a map of Content Association instances.
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findContentAssociations()
    {
        return findContentAssociations(null, null, null, null, null);
    }

    /**
     * Returns a filtered map of Content Association instances.
     *
     * @param sourceId the source id
     * @param sourceType the source type
     * @param destId the dest id
     * @param assocType the assoc type
     * @param formatId the format id
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findContentAssociations(
            String sourceId, String sourceType, String destId, String assocType,
            String formatId)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap,
                ContentAssociation.PROP_SOURCE_ID, sourceId);
        addPropertyConstraint(propertyConstraintMap,
                ContentAssociation.PROP_SOURCE_TYPE, sourceType);
        addPropertyConstraint(propertyConstraintMap,
                ContentAssociation.PROP_DEST_ID, destId);
        addPropertyConstraint(propertyConstraintMap,
                ContentAssociation.PROP_ASSOC_TYPE, assocType);
        addPropertyConstraint(propertyConstraintMap,
                ContentAssociation.PROP_FORMAT_ID, formatId);

        // do the lookup
        return findObjects(ContentAssociation.TYPE_ID, propertyConstraintMap);
    }

    /**
     * Returns a map of Component instances.
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findComponents()
    {
        return findObjects(Component.TYPE_ID);
    }

    /**
     * Returns a filtered map of Component instances.
     *
     * @param componentTypeId the component type id
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findComponents(String componentTypeId)
    {
        return findComponents(null, null, null, componentTypeId);
    }

    /**
     * Returns a filtered map of Component instances.
     *
     * @param scope the scope
     * @param regionId the region id
     * @param sourceId the source id
     * @param componentTypeId the component type id
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findComponents(
            String scope, String regionId, String sourceId, String componentTypeId)
    {
        if (componentTypeId != null || (scope == null && regionId == null && sourceId == null))
        {
            // build property map
            Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
            addPropertyConstraint(propertyConstraintMap, Component.PROP_SCOPE, scope);
            addPropertyConstraint(propertyConstraintMap, Component.PROP_REGION_ID, regionId);
            addPropertyConstraint(propertyConstraintMap, Component.PROP_SOURCE_ID, sourceId);
            addPropertyConstraint(propertyConstraintMap, Component.PROP_COMPONENT_TYPE_ID, componentTypeId);

            // do the lookup
            return findObjects(Component.TYPE_ID, propertyConstraintMap);
        }
        else
        {
            // perform an optimized lookup - using any of scope/regionid/sourceid as component path keys
            // TODO: careful of future encoding of component IDs which may affect the filtering process
            if (scope == null)
            {
                scope = "*";
            }
            if (regionId == null)
            {
                regionId = "*";
            }
            if (sourceId == null)
            {
                sourceId = "*";
            }

            String filter = RenderUtil.generateComponentId(scope, regionId, sourceId);

            return getAllObjects(Component.TYPE_ID, filter);
        }
    }

    /**
     * Returns a map of Template instances.
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findTemplates()
    {
        return findTemplates(null);
    }

    /**
     * Returns a filtered map of Template instances.
     *
     * @param templateType the template type
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findTemplates(String templateType)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap,
                TemplateInstance.PROP_TEMPLATE_TYPE, templateType);

        // do the lookup
        return findObjects(TemplateInstance.TYPE_ID, propertyConstraintMap);
    }

    /**
     * Returns a map of Template Type instances.
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findTemplateTypes()
    {
        return findTemplateTypes(null);
    }

    /**
     * Returns a filtered map of Template Type instances.
     *
     * @param uri the uri
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findTemplateTypes(String uri)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap, TemplateType.PROP_URI, uri);

        // do the lookup
        return findObjects(TemplateType.TYPE_ID, propertyConstraintMap);
    }

    /**
     * Returns a map of Component Type instances.
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findComponentTypes()
    {
        return findComponentTypes(null);
    }

    /**
     * Returns a fitlered map of Component Type instances.
     *
     * @param uri the uri
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findComponentTypes(String uri)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap, ComponentType.PROP_URI, uri);

        // do the lookup
        return findObjects(ComponentType.TYPE_ID, propertyConstraintMap);
    }

    /**
     * Returns a map of Page instances.
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findPages()
    {
        return findPages(null, null);
    }

    /**
     * Returns a filtered map of Page instances.
     *
     * @param templateId the template id
     * @param pageTypeId the page type id
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findPages(String templateId, String pageTypeId)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap, Page.PROP_TEMPLATE_INSTANCE,
                templateId);
        addPropertyConstraint(propertyConstraintMap, Page.PROP_PAGE_TYPE_ID,
                pageTypeId);

        // do the lookup
        return findObjects(Page.TYPE_ID, propertyConstraintMap);
    }

    /**
     * Returns a map of Page Type instances.
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findPageTypes()
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();

        // do the lookup
        return findObjects(PageType.TYPE_ID, propertyConstraintMap);
    }

    /**
     * Returns a map of Theme instances.
     *
     * @return the map< string, model object>
     */
    public Map<String, ModelObject> findThemes()
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();

        // do the lookup
        return findObjects(Theme.TYPE_ID, propertyConstraintMap);
    }

    /**
     * Returns a map of objects of the given type
     *
     * @param objectTypeId the object type id
     *
     * @return A map of objects keyed by id
     */
    public Map<String, ModelObject> findObjects(String objectTypeId)
    {
        return findObjects(objectTypeId, null);
    }

    /**
     * Associates a source Page instance to a destination Page instance.
     * The type of the association is set to "child".
     *
     * @param sourceId the source id
     * @param destId the dest id
     * @throws ModelObjectPersisterException 
     */
    public void associatePage(String sourceId, String destId) throws ModelObjectPersisterException
    {
        associatePage(sourceId, destId, "child");
    }

    /**
     * Associates a source Page instance to a destination Page instance.
     *
     * @param sourceId the source id
     * @param destId the dest id
     * @param associationType the association type
     * @throws ModelObjectPersisterException 
     */
    public void associatePage(String sourceId, String destId, String associationType) throws ModelObjectPersisterException
    {
        // first call unassociate just to be safe
        unassociatePage(sourceId, destId, associationType);

        // create a new template association
        PageAssociation pageAssociation = newPageAssociation();
        pageAssociation.setSourceId(sourceId);
        pageAssociation.setDestId(destId);
        pageAssociation.setAssociationType(associationType);

        // save the object
        saveObject(pageAssociation);
    }

    /**
     * Unassociates a destination Page instance from a source Page instance.
     * The association type is assumed to be "child".
     *
     * @param sourceId the source id
     * @param destId the dest id
     */
    public void unassociatePage(String sourceId, String destId)
    {
        unassociatePage(sourceId, destId, "child");
    }

    /**
     * Unassociates a destination Page instance from a source Page instance.
     *
     * @param sourceId the source id
     * @param destId the dest id
     * @param associationTypeId the association type id
     */
    @SuppressWarnings("rawtypes")
    public void unassociatePage(String sourceId, String destId, String associationTypeId)
    {
        Map<String, ModelObject> objects = findPageAssociations(sourceId, destId, associationTypeId);
        Iterator it = objects.keySet().iterator();
        while (it.hasNext())
        {
            String pageAssociationId = (String) it.next();
            unassociatePage(pageAssociationId);
        }
    }

    /**
     * Unassociates a page given its Page Association instance id.
     *
     * @param pageAssociationId the page association id
     */
    public void unassociatePage(String pageAssociationId)
    {
        removeObject(PageAssociation.TYPE_ID, pageAssociationId);
    }

    /**
     * Associates content to a given presentation object.
     *
     * Normally, this is used to associate a content id to a template id.
     *
     * @param sourceId the source id
     * @param sourceType the source type
     * @param destId the dest id
     * @param assocType the assoc type
     * @param formatId the format id
     * @throws ModelObjectPersisterException 
     */
    public void associateContent(String sourceId, String sourceType, String destId, String assocType, String formatId) throws ModelObjectPersisterException
    {
        // first call unassociate just to be safe
        unassociateContent(sourceId, sourceType, destId, assocType, formatId);

        // create a new association
        ContentAssociation association = newContentAssociation();
        association.setSourceId(sourceId);
        association.setSourceType(sourceType);
        association.setDestId(destId);
        association.setAssociationType(assocType);
        association.setFormatId(formatId);

        // save the object
        saveObject(association);
    }

    /**
     * Unassociates content from a given presentation object.
     *
     * @param sourceId the source id
     * @param sourceType the source type
     * @param destId the dest id
     * @param assocType the assoc type
     * @param formatId the format id
     */
    public void unassociateContent(String sourceId, String sourceType, String destId, String assocType, String formatId)
    {
        Map<String, ModelObject> objects = findContentAssociations(sourceId, sourceType, destId, assocType, formatId);
        Iterator<String> it = objects.keySet().iterator();
        while (it.hasNext())
        {
            String associationId = (String) it.next();
            unassociateContent(associationId);
        }
    }

    /**
     * Unassociates content given a Content Association instance id.
     *
     * @param objectAssociationId the object association id
     */
    public void unassociateContent(String objectAssociationId)
    {
        removeObject(ContentAssociation.TYPE_ID, objectAssociationId);
    }

    /**
     * Associates a Template to a Page given their respective ids.
     * The format key is assumed to be the default format.
     *
     * @param templateId the template id
     * @param pageId the page id
     * @throws ModelObjectPersisterException 
     */
    public void associateTemplate(String templateId, String pageId) throws ModelObjectPersisterException
    {
        associateTemplate(templateId, pageId, null);
    }

    /**
     * Associates a Template to a Page given their respective ids.
     *
     * @param templateId the template id
     * @param pageId the page id
     * @param formatId the format id
     * @throws ModelObjectPersisterException 
     */
    public void associateTemplate(String templateId, String pageId, String formatId) throws ModelObjectPersisterException
    {
        Page page = getPage(pageId);
        page.setTemplateId(templateId, formatId);
        saveObject(page);
    }

    /**
     * Unassociates a Template from a Page.
     * The format key is assumed to be the default format.
     *
     * @param pageId the page id
     * @throws ModelObjectPersisterException 
     */
    public void unassociateTemplate(String pageId) throws ModelObjectPersisterException
    {
        unassociateTemplate(pageId, null);
    }

    /**
     * Unassociates a Template from a Page.
     *
     * @param pageId the page id
     * @param formatId the format id
     * @throws ModelObjectPersisterException 
     */
    public void unassociateTemplate(String pageId, String formatId) throws ModelObjectPersisterException
    {
        Page page = getPage(pageId);
        page.removeTemplateId(formatId);
        saveObject(page);
    }

    /**
     * Binds a Component to a given scope, region id and source id.
     *
     * If an existing Component has this binding, the existing Component
     * is removed.
     *
     * @param componentId the component id
     * @param scope the scope
     * @param regionId the region id
     * @param sourceId the source id
     * @throws ModelObjectPersisterException 
     */
    public void bindComponent(String componentId, String scope, String regionId, String sourceId) throws ModelObjectPersisterException
    {
        // get the component to bind in
        Component component = getComponent(componentId);
        bindComponent(component, scope, regionId, sourceId);
    }

    /**
     * Binds a Component to a given scope, region id and source id.
     *
     * If an existing Component has this binding, the existing Component
     * is removed.
     *
     * @param component the component
     * @param scope the scope
     * @param regionId the region id
     * @param sourceId the source id
     * @throws ModelObjectPersisterException 
     */
    public void bindComponent(Component component, String scope, String regionId, String sourceId) throws ModelObjectPersisterException
    {
        // remove any existing bound components
        Component existingComponent = this.getComponent(scope, regionId, sourceId);
        if (existingComponent != null)
        {
            removeObject(existingComponent);
        }

        // bind it
        component.setScope(scope);
        component.setSourceId(sourceId);
        component.setRegionId(regionId);

        // save the object
        saveObject(component);
    }

    /**
     * Removes a Component binding with the given Component id.
     *
     * @param componentId the component id
     */
    public void unbindComponent(String componentId)
    {
        Component existingComponent = getComponent(componentId);
        if (existingComponent != null)
        {
            removeObject(existingComponent);
        }
    }

    /**
     * Removes a Component binding with the given binding properties.
     *
     * @param scope the scope
     * @param regionId the region id
     * @param sourceId the source id
     */
    public void unbindComponent(String scope, String regionId, String sourceId)
    {
        Component existingComponent = getComponent(scope, regionId, sourceId);
        if (existingComponent != null)
        {
            removeObject(existingComponent);
        }
    }

    /**
     * Creates a new property constraint map.
     *
     * The map describes property constraints that should be applied
     * when filtering the resultset.
     *
     * @return the map< string, object>
     */
    protected Map<String, Object> newPropertyConstraintMap()
    {
        return new HashMap<String, Object>(8, 1.0f);
    }

    /**
     * Adds the property constraint.
     *
     * @param propertyConstraintMap the property constraint map
     * @param propertyName the property name
     * @param propertyValue the property value
     */
    protected static void addPropertyConstraint(Map<String, Object> propertyConstraintMap,
            String propertyName, Object propertyValue)
    {
        if (propertyValue != null)
        {
            propertyConstraintMap.put(propertyName, propertyValue);
        }
    }

    /**
     * Filtering function that looks up objects of a given type id
     * and then applies the provided property constraint map.
     *
     * @param propertyConstraintMap the property constraint map
     * @param objectTypeId the object type id
     *
     * @return the map of IDs to ModelObjects
     */
    protected Map<String, ModelObject> findObjects(String objectTypeId, Map<String, Object> propertyConstraintMap)
    {
        Map<String, ModelObject> objectsMap = getAllObjects(objectTypeId);

        List<String> toRemove = new ArrayList<String>(objectsMap.size());

        for (String objectKey : objectsMap.keySet())
        {
            boolean found = true;

            ModelObject object = objectsMap.get(objectKey);

            // walk the property map and make sure all matches are satisfied
            if (propertyConstraintMap != null)
            {
                for (String propertyName : propertyConstraintMap.keySet())
                {
                    Object propertyValue = propertyConstraintMap.get(propertyName);
                    if (propertyValue != null)
                    {
                        // constraints
                        if (propertyValue instanceof String)
                        {
                            String currentValue = object.getProperty(propertyName);
                            if (!propertyValue.equals(currentValue))
                            {
                                found = false;
                                break;
                            }
                        }
                        else if (propertyValue instanceof Boolean)
                        {
                            boolean currentValue = object.getBooleanProperty(propertyName);
                            if (currentValue != ((Boolean) propertyValue).booleanValue())
                            {
                                found = false;
                                break;
                            }
                        }
                    }
                }
            }

            if (!found)
            {
                toRemove.add(objectKey);
            }
        }

        // remove anything we no longer want to keep
        for (int i = 0; i < toRemove.size(); i++)
        {
            objectsMap.remove(toRemove.get(i));
        }

        return objectsMap;
    }
    
    /**
     * @param objectId  to test for
     * @return true if the page with the given id is present in a persister
     */
    public boolean hasPage(String objectId)
    {
        return hasObject(Page.TYPE_ID, objectId);
    }

    /**
     * @param objectTypeId  to test for
     * @param objectId      to test for
     * @return true if the given object is present in a persister
     */
    public boolean hasObject(String objectTypeId, String objectId)
    {
        if (objectTypeId == null)
        {
            throw new IllegalArgumentException("ObjectTypeId is mandatory");
        }
        
        if (objectId == null)
        {
            throw new IllegalArgumentException("ObjectId is mandatory");
        }
        
        ObjectPersistenceService ops = getObjectPersistenceService();
        
        return ops.hasObject(objectTypeId, objectId);
    }

    /**
     * Clones a given model object
     *
     * @param objectTypeId the object type id
     * @param objectId the object id
     *
     * @return the page
     */
    public ModelObject clone(String objectTypeId, String objectId)
    {
        return clone(objectTypeId, objectId, null);
    }

    /**
     * Clones a given model object
     *
     * @param objectTypeId the object type id
     * @param objectId the object id
     * @param newId the id to set to the new object
     *
     * @return the page
     */
    public ModelObject clone(String objectTypeId, String objectId, String newObjectId)
    {
        ModelObject newObject = this.newObject(objectTypeId);

        ModelObject object = getObject(objectTypeId, objectId);

        // copy in properties
        Map<String, Serializable> properties = object.getProperties();
        Iterator<String> propIt = properties.keySet().iterator();
        while (propIt.hasNext())
        {
            String propertyName = (String) propIt.next();
            Object propertyValue = properties.get(propertyName);

            newObject.setProperty(propertyName, (String)propertyValue);
        }

        // copy in resources
        if (object instanceof ResourceProvider)
        {
            ResourceProvider source = (ResourceProvider) object;
            ResourceProvider dest = (ResourceProvider) newObject;

            Resource[] resources = source.getResources();
            for(int i = 0; i < resources.length; i++)
            {
                String name = resources[i].getName();

                String _protocolId = resources[i].getProtocolId();
                String _endpointId = resources[i].getEndpointId();
                String _objectId = resources[i].getObjectId();

                dest.addResource(name, _protocolId, _endpointId, _objectId);
            }
        }

        return newObject;
    }
}
