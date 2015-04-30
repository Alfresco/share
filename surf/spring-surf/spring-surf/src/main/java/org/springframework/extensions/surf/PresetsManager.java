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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.types.Component;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.surf.util.XMLUtil;
import org.springframework.extensions.webscripts.SearchPath;
import org.springframework.extensions.webscripts.Store;

/**
 * Spring util bean responsible for preset model object generation.
 * <p>
 * Presets are defined as XML snippets representing the model objects for a given set.
 * Each file can contain many presets each referenced by a unique ID. The preset definitions
 * can be located in any Store and any number of stores can be searched.
 * <p>
 * A set of parameterised model objects such as page, template instances and component
 * bindings can be defined for a preset. The XML for each model object definition is
 * effectively identical to that used to define the model object within its own file -
 * but nested within the preset structure as follows:
 * <pre>
 * <{@code}?xml version='1.0' encoding='UTF-8'?>
 * <{@code}presets>
 *     <{@code}preset id="someid">
 *         <{@code}components>
 *             ...
 *         <{@code}/components>
 *         <{@code}pages>
 *             ...
 *         <{@code}/pages>
 *         <{@code}template-instances>
 *             ...
 *         <{@code}/template-instances>
 *     <{@code}/preset>
 *     <{@code}preset id="anotherid">
 *         ...
 *     <{@code}/preset>
 * <{@code}/presets>
 * </pre>
 * One important difference to standard model object XML is that the ID for an object is
 * specified as an attribute on the parent element, for instance:
 * <pre><{@code}page id="user/${userid}/dashboard"></pre>
 * See the file slingshot\config\alfresco\site-data\presets\presets.xml for example usage.
 * <p>
 * Each preset supports parameterisation via "token" name/value pair replacements. For example:
 * <pre>
 *     <{@code}preset id="site-dashboard">
 *         <{@code}components>
 *             <{@code}component>
 *                 <{@code}scope>${scope}<{@code}/scope>
 *                 <{@code}region-id>title<{@code}/region-id>
 *                 <{@code}source-id>site/${siteid}/dashboard<{@code}/source-id>
 *                 <{@code}url>/components/title/collaboration-title<{@code}/url>
 *             <{@code}/component>
 *             ...
 * </pre>
 * where the values of "${scope}" and "${siteid}" would be replaced if supplied in the token
 * map during preset construction. See the method constructPreset() below.
 * </p>
 * @author Kevin Roast
 */
public class PresetsManager
{
    /**
     * <p>The {@link SearchPath} instance to use when looking up preset configuration files. This should
     * be defined in the Spring application context.</p>
     */
    private SearchPath searchPath;
    
    /**
     * <p>A {@link List} of suffices that valid presets configuration files are allowed to have. Only
     * files that end in a suffix defined in this list will be processed.</p>
     */
    private List<String> fileSuffices;

    /**
     * <p>An array of all the {@link Document} instances that represent all the preset configuration files
     * processed for the application. This array is generated at application startup by the <code>init</code>
     * method.</p>
     */
    private Document[] documents;

    /**
     * <p>The {@link ModelObjectService} is required in order to create new objects from the presets.</p>
     */
    private ModelObjectService modelObjectService;

    /**
     * @param modelObjectService the model object service
     */
    public void setModelObjectService(ModelObjectService modelObjectService)
    {
        this.modelObjectService = modelObjectService;
    }

    /**
     * @param searchPath The SearchPath to set
     */
    public void setSearchPath(SearchPath searchPath)
    {
        this.searchPath = searchPath;
    }

    /**
     * <p>This setter is provided to allow the Spring application context to set the filename suffices
     * that should be used for presets. Originally this was treated as a list of complete filenames but
     * has since been expanded to increase the ability to extend default application presets.</p>
     * 
     * @param files A list of filename suffices that can be matched to presets configuration files.
     */
    public void setFiles(List<String> files)
    {
        this.fileSuffices = files;
    }

    /**
     * <p>Initialise the presets manager to load all the presets configuration files.</p>
     */
    private void init()
    {
        if (this.searchPath == null || this.fileSuffices == null)
        {
            throw new IllegalArgumentException("SearchPath and Files list are mandatory.");
        }

        // Search for our preset XML descriptor documents
        
        // Find all the preset configuration files in all the configured stores. In order to maintain
        // a sensible precedence order we will search the stores in order and then check every 
        // document path against the list of suffices. This is not the most efficient way of processing
        // the configuration files but as this only happens at application startup it is not a major
        // problem...
        List<Document> docs = new ArrayList<Document>(4);
        for (Store store : this.searchPath.getStores())
        {
            // For the current storee...
            for (String path: store.getAllDocumentPaths())
            {
                // ...get all the documents...
                for (String fileSuffix : this.fileSuffices)
                {
                    // ...and see if each ends with the current file suffix...
                    if (path.endsWith(fileSuffix))
                    {
                        try
                        {
                            docs.add(XMLUtil.parse(store.getDocument(path)));
                        }
                        catch (IOException ioe)
                        {
                            throw new PlatformRuntimeException("Error loading presets XML file: " +
                                    fileSuffix + " in store: " + store.toString(), ioe);
                        }
                        catch (DocumentException de)
                        {
                            de.printStackTrace();
                            throw new PlatformRuntimeException("Error processing presets XML file: " +
                                    fileSuffix + " in store: " + store.toString(), de);
                        }
                        break; // No point in carrying on around the loop, we've already added the file.
                    }
                }
                
            }
        }
        this.documents = docs.toArray(new Document[docs.size()]);
    }

    /**
     * Construct the model objects for a given preset.
     * Objects persist to the default store for the appropriate object type.
     *
     * @param id        Preset ID to use
     * @param tokens    Name value pair tokens to replace in preset definition
     * @return true on successfully created preset, false on failure
     */
    @SuppressWarnings("unchecked")
    public boolean constructPreset(String id, Map<String, String> tokens)
    {
        boolean created = false;
        
        if (id == null)
        {
            throw new IllegalArgumentException("Preset ID is mandatory.");
        }

        // perform one time init - this cannot be perform in an app handler or by the
        // framework init - as it requires the Alfresco server to be started...
        synchronized (this)
        {
            if (this.documents == null)
            {
                init();
            }
        }

        List<ModelObject> docsToCreate = new LinkedList<ModelObject>();
        boolean foundPreset = false;
        for (int i=0; (!foundPreset && i<this.documents.length); i++)
        {
            Iterator<Element> presets = ((List<Element>) this.documents[i].getRootElement().elements("preset")).iterator();
            while (!foundPreset && presets.hasNext())
            {
                Element preset = presets.next();
                if (id.equals(preset.attributeValue("id")))
                {
                    // Found our preset, this will be out last iteration of both inner and outer loop...
                    foundPreset = true;

                    // any components in the preset?
                    Element components = preset.element("components");
                    if (components != null)
                    {
                        for (Element c : (List<Element>)components.elements("component"))
                        {
                            // apply token replacement to each value as it is retrieved
                            String title = replace(c.elementTextTrim(Component.PROP_TITLE), tokens);
                            String titleId = replace(c.elementTextTrim(Component.PROP_TITLE_ID), tokens);
                            String description = replace(c.elementTextTrim(Component.PROP_DESCRIPTION), tokens);
                            String descriptionId = replace(c.elementTextTrim(Component.PROP_DESCRIPTION_ID), tokens);
                            String typeId = replace(c.elementTextTrim(Component.PROP_COMPONENT_TYPE_ID), tokens);
                            String scope = replace(c.elementTextTrim(Component.PROP_SCOPE), tokens);
                            String regionId = replace(c.elementTextTrim(Component.PROP_REGION_ID), tokens);
                            String sourceId = replace(c.elementTextTrim(Component.PROP_SOURCE_ID), tokens);
                            String url = replace(c.elementTextTrim(Component.PROP_URL), tokens);
                            String uri = replace(c.elementTextTrim(Component.PROP_URI), tokens);
                            String chrome = replace(c.elementTextTrim(Component.PROP_CHROME), tokens);

                            // validate mandatory values
                            if (scope == null || scope.length() == 0)
                            {
                                throw new IllegalArgumentException("Scope is a mandatory property for a component preset.");
                            }
                            if (regionId == null || regionId.length() == 0)
                            {
                                throw new IllegalArgumentException("RegionID is a mandatory property for a component preset.");
                            }
                            if (sourceId == null || sourceId.length() == 0)
                            {
                                throw new IllegalArgumentException("SourceID is a mandatory property for a component preset.");
                            }

                            // generate component
                            Component component = modelObjectService.newComponent(scope, regionId, sourceId);
                            component.setComponentTypeId(typeId);
                            component.setTitle(title);
                            component.setTitleId(titleId);
                            component.setDescription(description);
                            component.setDescriptionId(descriptionId);
                            component.setURL(url);
                            component.setURI(uri); // Set both URI and URL to support consistency between component types
                            component.setChrome(chrome);

                            // apply arbituary custom properties
                            if (c.element("properties") != null)
                            {
                                for (Element prop : (List<Element>)c.element("properties").elements())
                                {
                                    String propName = replace(prop.getName(), tokens);
                                    String propValue = replace(prop.getTextTrim(), tokens);
                                    component.setCustomProperty(propName, propValue);
                                }
                            }

                            // collect the object to persist later
                            docsToCreate.add(component);
                        }
                    }

                    // any pages in the preset?
                    Element pages = preset.element("pages");
                    if (pages != null)
                    {
                        for (Element p : (List<Element>)pages.elements("page"))
                        {
                            // apply token replacement to each value as it is retrieved
                            String pageId = replace(p.attributeValue(Page.PROP_ID), tokens);
                            String title = replace(p.elementTextTrim(Page.PROP_TITLE), tokens);
                            String titleId = replace(p.elementTextTrim(Page.PROP_TITLE_ID), tokens);
                            String description = replace(p.elementTextTrim(Page.PROP_DESCRIPTION), tokens);
                            String descriptionId = replace(p.elementTextTrim(Page.PROP_DESCRIPTION_ID), tokens);
                            String typeId = replace(p.elementTextTrim(Page.PROP_PAGE_TYPE_ID), tokens);
                            String auth = replace(p.elementTextTrim(Page.PROP_AUTHENTICATION), tokens);
                            String template = replace(p.elementTextTrim(Page.PROP_TEMPLATE_INSTANCE), tokens);

                            // validate mandatory values
                            if (pageId == null || pageId.length() == 0)
                            {
                                throw new IllegalArgumentException("ID is a mandatory attribute for a page preset.");
                            }
                            if (template == null || template.length() == 0)
                            {
                                throw new IllegalArgumentException("Template is a mandatory property for a page preset.");
                            }

                            // generate page
                            Page page = modelObjectService.newPage(pageId);
                            page.setPageTypeId(typeId);
                            page.setTitle(title);
                            page.setTitleId(titleId);
                            page.setDescription(description);
                            page.setDescriptionId(descriptionId);
                            page.setAuthentication(auth);
                            page.setTemplateId(template);

                            // apply arbituary custom properties
                            if (p.element("properties") != null)
                            {
                                for (Element prop : (List<Element>)p.element("properties").elements())
                                {
                                    String propName = replace(prop.getName(), tokens);
                                    String propValue = replace(prop.getTextTrim(), tokens);
                                    page.setCustomProperty(propName, propValue);
                                }
                            }

                            // collect the object to persist later
                            docsToCreate.add(page);
                        }
                    }

                    // any template instances in the preset?
                    Element templates = preset.element("template-instances");
                    if (templates != null)
                    {
                        for (Element t : (List<Element>)templates.elements("template-instance"))
                        {
                            // apply token replacement to each value as it is retrieved
                            String templateId = replace(t.attributeValue(TemplateInstance.PROP_ID), tokens);
                            String title = replace(t.elementTextTrim(TemplateInstance.PROP_TITLE), tokens);
                            String titleId = replace(t.elementTextTrim(TemplateInstance.PROP_TITLE_ID), tokens);
                            String description = replace(t.elementTextTrim(TemplateInstance.PROP_DESCRIPTION), tokens);
                            String descriptionId = replace(t.elementTextTrim(TemplateInstance.PROP_DESCRIPTION_ID), tokens);
                            String templateType = replace(t.elementTextTrim(TemplateInstance.PROP_TEMPLATE_TYPE), tokens);

                            // validate mandatory values
                            if (templateId == null || templateId.length() == 0)
                            {
                                throw new IllegalArgumentException("ID is a mandatory attribute for a template-instance preset.");
                            }
                            if (templateType == null || templateType.length() == 0)
                            {
                                throw new IllegalArgumentException("Template is a mandatory property for a page preset.");
                            }

                            // generate template-instance
                            TemplateInstance template = modelObjectService.newTemplate(templateId);
                            template.setTitle(title);
                            template.setTitleId(titleId);
                            template.setDescription(description);
                            template.setDescriptionId(descriptionId);
                            template.setTemplateTypeId(templateType);

                            // apply arbituary custom properties
                            if (t.element("properties") != null)
                            {
                                for (Element prop : (List<Element>)t.element("properties").elements())
                                {
                                    String propName = replace(prop.getName(), tokens);
                                    String propValue = replace(prop.getTextTrim(), tokens);
                                    template.setCustomProperty(propName, propValue);
                                }
                            }

                            // collect the object to persist later
                            docsToCreate.add(template);
                        }
                    }

                    // TODO: any chrome, associations, types, themes etc. in the preset...

                    // Bulk create the documents in a single save operation
                    // typically this results in a single POST to a remote store
                    if (!docsToCreate.isEmpty())
                    {
                        try
                        {
                            created = modelObjectService.saveObjects(docsToCreate);
                        }
                        catch (ModelObjectPersisterException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    // found our preset - no need to process further
                    break;
                }
            }
        }
        return created;
    }

    /**
     * Replace token strings - marked by ${...} in the given string with
     * the supplied tokens.
     *
     * @param s         String to process (can be null - will return null)
     * @param tokens    Token map (can be null - will return original string)
     *
     * @return replaced string or null if input is null or original string if tokens is null
     */
    private static String replace(String s, Map<String, String> tokens)
    {
        if (s != null && tokens != null)
        {
            for (Entry<String, String> entry : tokens.entrySet())
            {
                String key = "${" + entry.getKey() + "}";
                String value = entry.getValue();
                if (s.indexOf(key) != -1 && value != null)
                {
                    // There is no point attempting to replace the key if it isn't present,
                    // and if we attempt to replace a key with a null then we'll just end up with
                    // a NullPointerException
                    s = s.replace(key, value);
                }
            }
        }
        return s;
    }
}
