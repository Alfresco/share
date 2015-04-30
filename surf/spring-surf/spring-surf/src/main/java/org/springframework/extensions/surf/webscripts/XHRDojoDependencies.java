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
package org.springframework.extensions.surf.webscripts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.directives.OutputCSSContentModelElement;
import org.springframework.extensions.surf.CssImageDataHandler;
import org.springframework.extensions.surf.DependencyAggregator;
import org.springframework.extensions.surf.DependencyHandler;
import org.springframework.extensions.surf.DojoDependencies;
import org.springframework.extensions.surf.DojoDependencyHandler;
import org.springframework.extensions.surf.I18nDependencyHandler;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This backs the /surf/dojo/xhr/dependencies WebScript and should be used to build resource files containing
 * all the required CSS and JavaScript dependencies for a set of widgets defined in a stringified JSON object
 * passed as a request parameter.
 * 
 * @author David Draper
 *
 */
public class XHRDojoDependencies extends DeclarativeWebScript
{
    private DependencyHandler dependencyHandler;
    private DojoDependencyHandler dojoDependencyHandler;
    private DependencyAggregator dependencyAggregator;
    private I18nDependencyHandler i18nDependencyHandler;
    private WebFrameworkConfigElement webFrameworkConfig;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        if (this.dojoDependencyHandler != null)
        {
            try
            {
                String content = req.getContent().getContent();
                JSONParser jp = new JSONParser();
                Object o = jp.parse(content);
                if (o instanceof JSONObject)
                {
                    JSONObject jsonData = (JSONObject) o;
                    JSONObject json = (JSONObject)jsonData.get("jsonContent");
                    
                    // Process all the dependencies for all the widgets referenced in the JSON object...
                    Map<String, DojoDependencies> dependenciesForCurrentRequest = new LinkedHashMap<String, DojoDependencies>();
                    this.processServices(json, dependenciesForCurrentRequest);
                    this.processWidgets(json, dependenciesForCurrentRequest);
                    
                    // Extract the CSS dependencies and build a map of the media type (e.g. "screen") to the aggregated resource...
                    Map<String, String> mediaToResource = this.generateCssMediaToResourceMap(dependenciesForCurrentRequest);
                    
                    // Extract the i18n dependencies...
                    Map<String, Map<String, Object>> i18nMap = this.i18nDependencyHandler.generateScopeToBundleMap(dependenciesForCurrentRequest);
                    
                    // Build an aggregated resource of the JavaScript dependencies...
                    String aggregatedOutput = this.dojoDependencyHandler.outputAggregateResource(dependenciesForCurrentRequest, null).toString();
                    String checksum = this.dojoDependencyHandler.getChecksumPathForDependencies(aggregatedOutput);
                    
                    Set<String> nonAmdDeps = new LinkedHashSet<String>();
                    for (Entry<String, DojoDependencies> entry: dependenciesForCurrentRequest.entrySet())
                    {
                       nonAmdDeps.addAll(entry.getValue().getNonAmdDependencies());
                    }
                    
                    // Construct the model...
                    model.put("nonAmdDeps", nonAmdDeps);
                    model.put("jsResource", checksum);
                    model.put("cssMap", mediaToResource);
                    model.put("i18nMap", i18nMap);
                    model.put("i18nGlobalObject", this.webFrameworkConfig.getDojoMessagesObject());
                }
            }
            catch (IOException e)
            {
                
            }
            catch (ParseException e)
            {
                status.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                status.setMessage("An error occurred parsing the 'widgets' request parameter into JSON");
                status.setException(e);
                status.setRedirect(true);
            }
        }
        return model;
    }
    
    /**
     * @param json A {@link JSONObject} containing the services configuration to be processed.
     * @param dependenciesForCurrentRequest A {@link Map} of the dependencies processed for the current request
     */
    @SuppressWarnings("rawtypes")
    public void processServices(JSONObject json, Map<String, DojoDependencies> dependenciesForCurrentRequest)
    {
        Object o = json.get("services");
        if (o != null && o instanceof JSONArray)
        {
            JSONArray servicesList = (JSONArray) o;
            Iterator i = servicesList.iterator();
            while (i.hasNext())
            {
                Object p = i.next();
                if (p instanceof String)
                {
                    this.processDependency((String) p, dependenciesForCurrentRequest);
                }
                else if (p instanceof JSONObject)
                {
                    JSONObject service = (JSONObject) p;
                    Object _name = service.get("name");
                    if (_name instanceof String)
                    {
                        this.processDependency((String) _name, dependenciesForCurrentRequest);
                    }
                }
            }
        }
    }

    /**
     * Processes an individual dependency.
     *
     * @param name The name of the widget/service to be processed.
     * @param dependenciesForCurrentRequest A {@link Map} of the dependencies processed for the current request
     */
    public void processDependency(String name, Map<String, DojoDependencies> dependenciesForCurrentRequest)
    {
        String depPath = this.dojoDependencyHandler.getPath(null, name + ".js");
        DojoDependencies deps = this.dojoDependencyHandler.getDependencies(depPath);
        dependenciesForCurrentRequest.put(depPath, deps);
        this.dojoDependencyHandler.recursivelyProcessDependencies(deps, dependenciesForCurrentRequest);
    }

    /**
     * This method recursively processes JSON objects defining widgets updating the supplied {@link Map} of
     * dependencies.
     * 
     * @param widgets A {@link JSONObject} containing the widget configuration to be processed.
     * @param dependenciesForCurrentRequest A {@link Map} of the dependencies processed for the current request
     */
    @SuppressWarnings("rawtypes")
    public void processWidgets(JSONObject widgets, Map<String, DojoDependencies> dependenciesForCurrentRequest)
    {
        // Widgets processed in the JavaScript controller must all be relative to the Dojo package configuration. 
        if (widgets != null)
        {
            Object o = widgets.get(DojoDependencyHandler.WIDGETS_LIST);
            if (o instanceof JSONArray)
            {
                JSONArray widgetList = (JSONArray) o;
                Iterator i = widgetList.iterator();
                while (i.hasNext())
                {
                    Object p = i.next();
                    if (p instanceof JSONObject)
                    {
                        JSONObject widget = (JSONObject) p;
                        Object _name = widget.get(DojoDependencyHandler.WIDGET_NAME);
                        if (_name instanceof String)
                        {
                            String widgetPath = this.dojoDependencyHandler.getPath(null, ((String)_name)) + ".js";
                            DojoDependencies widgetDeps = this.dojoDependencyHandler.getDependencies(widgetPath);
                            dependenciesForCurrentRequest.put(widgetPath, widgetDeps);
                            this.dojoDependencyHandler.recursivelyProcessDependencies(widgetDeps, dependenciesForCurrentRequest);
                        }
                        Object _config = widget.get(DojoDependencyHandler.WIDGET_CONFIG);
                        if (_config instanceof JSONObject)
                        {
                            this.processWidgets(((JSONObject)_config), dependenciesForCurrentRequest);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 
     * @param dependenciesForCurrentRequest
     * @return
     */
    public Map<String, String> generateCssMediaToResourceMap(Map<String, DojoDependencies> dependenciesForCurrentRequest)
    {
        Map<String, String> mediaToResource = new HashMap<String, String>();
        OutputCSSContentModelElement outputCss = new OutputCSSContentModelElement(null, null, this.dependencyAggregator);
        String prefix = this.dependencyAggregator.getServletContext().getContextPath() + this.dependencyHandler.getResourceControllerMapping() + CssImageDataHandler.FORWARD_SLASH;
        this.dojoDependencyHandler.processCssDependencies(dependenciesForCurrentRequest, outputCss, prefix, "xhr");
        LinkedHashMap<String, HashMap<String, LinkedHashSet<String>>> cssMap = outputCss.getDojoCssGroupToDependencyMap();
        for (HashMap<String, LinkedHashSet<String>> group: cssMap.values())
        {
            // Iterate over all media types for the group...
            for (Entry<String, LinkedHashSet<String>> mediaToCssResource : group.entrySet())
            {
                // Append the specific CSS dependencies as an aggregated resource
                String checksum = dependencyAggregator.generateCSSDependencies(mediaToCssResource.getValue());
                mediaToResource.put(mediaToCssResource.getKey(), this.dependencyHandler.getResourceControllerMapping() + CssImageDataHandler.FORWARD_SLASH + checksum);
            }
        }
        return mediaToResource;
    }
    
    public DependencyHandler getDependencyHandler()
    {
        return dependencyHandler;
    }

    public void setDependencyHandler(DependencyHandler dependencyHandler)
    {
        this.dependencyHandler = dependencyHandler;
    }

    public DojoDependencyHandler getDojoDependencyHandler()
    {
        return dojoDependencyHandler;
    }

    public void setDojoDependencyHandler(DojoDependencyHandler dojoDependencyHandler)
    {
        this.dojoDependencyHandler = dojoDependencyHandler;
    }

    public DependencyAggregator getDependencyAggregator()
    {
        return dependencyAggregator;
    }

    public void setDependencyAggregator(DependencyAggregator dependencyAggregator)
    {
        this.dependencyAggregator = dependencyAggregator;
    }

    public I18nDependencyHandler getI18nDependencyHandler()
    {
        return i18nDependencyHandler;
    }

    public void setI18nDependencyHandler(I18nDependencyHandler i18nDependencyHandler)
    {
        this.i18nDependencyHandler = i18nDependencyHandler;
    }

    public WebFrameworkConfigElement getWebFrameworkConfig()
    {
        return webFrameworkConfig;
    }

    public void setWebFrameworkConfig(WebFrameworkConfigElement webFrameworkConfig)
    {
        this.webFrameworkConfig = webFrameworkConfig;
    }
}
