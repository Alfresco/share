/*
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
package org.springframework.extensions.directives;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONAware;
import org.springframework.extensions.surf.DependencyAggregator;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.extensibility.DeferredContentTargetModelElement;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.webscripts.json.JSONWriter;
import org.springframework.extensions.webscripts.json.RawValue;

import freemarker.core.Environment;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

/**
 * <p>This directive should be used in WebScripts that intend use client-side JavaScript "widgets". The directive
 * relies upon the following specific data having been added to the model. All widget data should be placed in 
 * a "webScriptWidgets" array. Each element in the array should contain the following properties:
 * <ul><li>name - The fully qualified name of the widget to be instantiated</li>
 * <li>provideMessages - A boolean value indicating whether or not the i18n messages objects should be passed to
 * the widget via the .setMessages() function</li>
 * <li>provideOptions - A boolean value indicating whether or not additional configuration options should be passed
 * to the widget via the .setOptions() function</li>
 * <li>options - an object containing all the objects to be passed to the widget via the .setOptions() function</li></ul>
 * </p>
 * 
 * @author David Draper
 */
public class CreateWebScriptWidgetsDirective extends JavaScriptDependencyDirective
{
    private static final Log logger = LogFactory.getLog(CreateWebScriptWidgetsDirective.class);

    public CreateWebScriptWidgetsDirective(String directiveName, ExtensibilityModel model)
    {
        super(directiveName, model);
    }

    
    /**
     * This is simply overridden to prevent the "src" not found error being generated. When in legacy mode 
     * this directive can have no function. Any version of Share that is running in legacy mode will not
     * be able to process widgets anyway.  
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected void addLegacyDependencyRequest(Map params) throws TemplateModelException
    {
        // No action required.
    }

    /**
     * This constant is used as one of the two attributes that specify custom options (the other being <code>REFERENCE_TYPE_KEY</code>)
     * if a {@link Map} is supplied as value that only contains these two attributes then it is considered a custom options and is
     * processed by the <code>processCustomOption</code> method. By default the value associated with the key in the {@link Map}
     * will be used as a reference to a JavaScript variable.</p>
     */
    public static final String REFERENCE_VALUE_KEY = "_alfValue";
    
    /**
     * This constant is used as one of the two attributes that specify custom options (the other being <code>REFERENCE_TYPE_KEY</code>)
     * if a {@link Map} is supplied as value that only contains these two attributes then it is considered a custom options and is
     * processed by the <code>processCustomOption</code> method. The value associated with the key in the {@link Map} is not actually
     * used in the default implementation of {@link CreateWebScriptWidgetsDirective} (other than as a marker to indicate a custom 
     * option) but can be used by extending classes that wish to handle other custom options.</p>
     */
    public static final String REFERENCE_TYPE_KEY = "_alfType";
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public ExtensibilityDirectiveData createExtensibilityDirectiveData(String id, 
                                                                       String action, 
                                                                       String target,
                                                                       Map params, 
                                                                       TemplateDirectiveBody body, 
                                                                       Environment env) throws TemplateException
    {
        // Get the FreeMarker model...
        TemplateHashModel model = env.getDataModel();
        
        // The htmlId is a required constructor argument for all WebScript JavaScript widgets... we need to make sure this
        // is available in the model otherwise there is no point in continuing...
        String htmlId = null;
        Object o = model.get(DirectiveConstants.ARGS);
        if (o instanceof SimpleHash)
        {
            Object _htmlid = ((SimpleHash) o).get(WebFrameworkConstants.RENDER_DATA_HTMLID);
            if (_htmlid instanceof SimpleScalar)
            {
                htmlId = ((SimpleScalar)_htmlid).toString();
            }
            else
            {
                // Error when htmlid not available - this shouldn't ever really occur for a WebScript, but it could occur if the directive is used incorrectly (e.g. not in a WebScript)
                logger.error("\"htmlid\" is either not available or is not an instance of SimpleScalar: " + _htmlid);
            }
        }
        
        // Retrieve the messages String if available...
        String messages = "";
        Object _messages = model.get(DirectiveConstants.MESSAGES);
        if (_messages instanceof SimpleScalar)
        {
            messages = ((SimpleScalar) _messages).toString();
        }

        // Get the group...
        String group = getStringProperty(params, DirectiveConstants.GROUP_PARAM, false); // null is acceptable as a group (it is effectively the default group)
        
        // Build the list of widgets to instantiate and then convert the data into the correct
        // JavaScript needed to perform the instantiation...
        StringBuilder content = new StringBuilder(DependencyAggregator.INLINE_AGGREGATION_MARKER);
        for (WidgetData widget: buildWidgetDataList(model))
        {
            content.append(buildWidgetInstantiatationScript(env, widget, htmlId, messages));
        }
            
        DeferredContentTargetModelElement targetElement = getModel().getDeferredContent(OutputJavaScriptDirective.OUTPUT_DEPENDENCY_DIRECTIVE_ID, OutputJavaScriptDirective.OUTPUT_JS_DEPENDENCIES_DIRECTIVE_NAME);
        DependencyDirectiveData directiveData = new DependencyDirectiveData(id, 
                                                                            action, 
                                                                            target, 
                                                                            getDirectiveName(), 
                                                                            body, 
                                                                            env, 
                                                                            content.toString(), 
                                                                            group,
                                                                            getWebFrameworkConfig().isAggregateDependenciesEnabled(),
                                                                            targetElement);
        return directiveData;
    }
    
    /**
     * <p>Constructs a String of JavaScript that can be used to instantiate the widget defined by the
     * supplied {@lik WidgetData} objects. The output will be a single line of JavaScript with chained
     * .setOptions() and .setMessages() functions calls if appropriate.</p>
     * 
     * @param model The {@link TemplateHashModel} for the current execution environment. This can be used to
     * retrieve additional data such as any configuration options that need to be provided.
     * @param widget The {@link WidgetData} object containing the data about the widget to be instantiated.
     * @param htmlId The current unique id of an HTML <{@code}div> element that should have been placed in the
     * WebScript FreeMarker template for the JavaScript widget to attach to.
     * @param messages The current i18n message object for the current request.
     * @return A String of JavaScript that will create the necessary widget.
     */
    protected String buildWidgetInstantiatationScript(Environment env,
                                                      WidgetData widget,
                                                      String htmlId,
                                                      String messages)
    {
        StringBuilder js = new StringBuilder(1024);
        
        // If requested, assign the widget to a supplied variable...
        if (widget.getAssignmentVariableName() != null)
        {
            js.append(DirectiveConstants.VAR);
            js.append(widget.getAssignmentVariableName());
            js.append(DirectiveConstants.EQUALS);
        }
        js.append(DirectiveConstants.NEW);
        js.append(widget.getName());
        js.append(DirectiveConstants.OPEN_BRACKET);
        // Output the instantiation arguments...
        if (widget.getAdditionalInstantiationArgs() != null)
        {
            // Use the supplied instantiation arguments...
            Iterator<Object> i = widget.getAdditionalInstantiationArgs().iterator();
            while (i.hasNext())
            {
                js.append(i.next().toString());
                if (i.hasNext())
                {
                    js.append(DirectiveConstants.COMMA);
                }
            }
        }
        else
        {
            // If there are no alternative instantiation arguments just used the HTML id...
            js.append(DirectiveConstants.QUOTE);
            js.append(htmlId);
            js.append(DirectiveConstants.QUOTE);
        }
        js.append(DirectiveConstants.CLOSE_BRACKET);
        
        // Provide options is requested...
        if (widget.isProvideOptions())
        {
            js.append(DirectiveConstants.SET_OPTIONS);

            // The issue is that we don't want to force developers to surround String values in quotes, but 
            // at the same time need to find a way to treat non-Strings (which are still presented as Strings)
            // as not Strings.  The options available are to use some form of character to delimit non-Strings
            // (e.g. control characters that would not be expected to be found in a normal String). The problem 
            // with this approach is that any character is valid when enclosed in quotes - so we'd be working on 
            // unlikely probabilities as opposed to certainties.
            //
            // The toJSONString method will ultimately defer to the .toString() method of an object in the Map
            // if it is not a known value. To work around this problem we will process the map for custom options.
            // These are defined as objects that just contain 2 special attributes. These are markers to indicate
            // a custom option (which by default is a JavaScript variable). 
            Map<Object,Object> processedOptions = processCustomOptionsInMap(env, widget.getOptions());
            String jsonString = JSONWriter.encodeToJSON(processedOptions); 
            js.append(jsonString);
            js.append(DirectiveConstants.CLOSE_BRACKET);
        }
        
        // Add messages if requested...
        if (widget.isProvideMessages())
        {
            js.append(DirectiveConstants.SET_MESSAGES);
            js.append(messages);
            js.append(DirectiveConstants.CLOSE_BRACKET);
        }
        js.append(DirectiveConstants.CLOSE_LINE);
        return js.toString();
    }
    
    /**
     * <p>In order to distinguish between genuine Strings and value references in the options map
     * it is necessary to process all the String values. This method works through the map (which
     * could have have nested lists and maps as values) and converts all Strings to either
     * StringOption or ReferenceOption instances. This is done so that when the options map is
     * converted into a JSON string it will fall back on the .toString() method of the unknown
     * type which we can therefore control.</p>
     * 
     * @param options
     * @return
     * @throws Exception 
     */
    protected Map<Object, Object> processCustomOptionsInMap(Environment env, Map<Object, Object> options)
    {
        // We need to create a new map to avoid ConcurrentModificationExceptions being thrown
        // when manipulating any lists in the options map...
        Map<Object,Object> processedOptions = new HashMap<Object, Object>();
        for (Entry<Object, Object> entry: options.entrySet())
        {
            processedOptions.put(entry.getKey(), processCustomOptionsInObject(env, entry.getValue()));
        }
        return processedOptions; 
    }
    
    /**
     * <p>Processes any custom options that are found in the supplied object. The supplied object will either
     * be a {@link Map} a {@link List} or some other valid JSON value (e.g. a String, a boolean, etc). Custom
     * options are defined as a {@link Map} containing two special attributes (and only those two attributes)
     * so this method will check all {@link Map} instances for those attributes and return a {@link JSONAware}
     * object in their place for outputting. Otherwise the method will recurse through all the objects.
     * 
     * @param env
     * @param object
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Object processCustomOptionsInObject(Environment env, Object object)
    {
        Object processedOption = null;
        if (object instanceof Map)
        {
            // Maps can contain custom options that are not standard JSON types. This deals with the specific
            // problem of how to define a reference to a JavaScript variable...
            Map map = (Map) object;
            if (map.keySet().size() == 2 && map.containsKey(REFERENCE_TYPE_KEY) && map.containsKey(REFERENCE_VALUE_KEY))
            {
                // For an object to specify a reference String it MUST contain only two attributes and they
                // must specify the value and its type
                processedOption = processCustomOption(env, map);
            }
            else
            {
                // Recurse on map values...
                processedOption = processCustomOptionsInMap(env, (Map) object);
            }
        }
        else if (object instanceof List)
        {
            // If the object is a list then create a new list (to avoid concurrent modification exceptions)
            // and then process all the elements in it...
            List<Object> processedList = new ArrayList<Object>();
            for (Object element: ((List) object))
            {
                processedList.add(processCustomOptionsInObject(env, element));    
            }
            // Assign the new list to the return object..
            processedOption = processedList;
        }
        else
        {
            // No processing required if not a String and no recursion required if not map or list
            processedOption = object;
        }
        return processedOption;
    }
    
    /**
     * <p>This method is provided to allow extending classes to process custom options other than the
     * default {@link ReferenceCustomOption} that is used to render variable references instead of
     * String literals. It should be called with a {@link Map} containing two entries; the 
     * value and the type. This implementation will only return a {@link ReferenceCustomOption}
     * instance.</p>
     * 
     * @param env The current FreeMarker {@link Environment}. Not used by this implementation but 
     * provided for the benefit of extending classes.
     * @param customOption A {@link Map} containing value and type for the custom option.
     * @return A new {@link JSONAware} object.
     */
    protected RawValue processCustomOption(Environment env, Map<String, Object> customOption)
    {
        return new RawValue(customOption.get(REFERENCE_VALUE_KEY).toString());
    }
    
    /**
     * 
     * @param env
     * @return
     * @throws TemplateModelException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected List<WidgetData> buildWidgetDataList(TemplateHashModel model) throws TemplateModelException
    {
        List<WidgetData> widgetDataList = new ArrayList<WidgetData>();
        Object o = model.get(DirectiveConstants.WEBSCRIPT_WIDGETS);
        if (o instanceof SimpleSequence)
        {
            SimpleSequence widgetList = (SimpleSequence) o;
            for (Object w: widgetList.toList())
            {
                if (w instanceof Map)
                {
                    Map widgetDataMap = (Map) w;
                    Object _widgetName = widgetDataMap.get(DirectiveConstants.NAME);
                    if (_widgetName instanceof String)
                    {
                        // If a name was provided then we can at least create a WidgetData object from this data (all
                        // the remaining data can be left as the defaults if necessary)...
                        String name = (String) _widgetName;
                        boolean provideOptions = true;
                        boolean provideMessages = true; 
                        Map<Object, Object> options = null;
                        String assignmentVariable = null;
                        List<Object> instantiationArgs = null;
                        Object _provideOptions = widgetDataMap.get(DirectiveConstants.PROVIDE_OPTIONS);
                        if (_provideOptions instanceof Boolean && ((Boolean)_provideOptions).booleanValue() == false)
                        {
                            // Don't get the options
                            provideOptions = false;
                        }
                        else
                        {
                            Object _options = widgetDataMap.get(DirectiveConstants.OPTIONS);
                            if (_options instanceof Map)
                            {
                                options = (Map) _options;
                            }
                            else
                            {
                                options = null;
                                provideOptions = false;
                            }
                        }
                        
                        // Get the message information...
                        Object _provideMessages = widgetDataMap.get(DirectiveConstants.PROVIDE_MESSAGES);
                        if (_provideMessages instanceof Boolean)
                        {
                            provideMessages = (Boolean) _provideMessages;
                        }
                        
                        // Get the name of the variable to assign the widget to (this is optional)...
                        Object _assignmentVariableName = widgetDataMap.get(DirectiveConstants.ASSIGNMENT_VARIABLE_NAME);
                        if (_assignmentVariableName instanceof String)
                        {
                            assignmentVariable = (String) _assignmentVariableName;
                        }
                        
                        // Get the instantiation arguments (optional). These will replace the default
                        // widget instantiation pattern of just using the HTML ID...
                        Object _instantiationArgs = widgetDataMap.get(DirectiveConstants.INSTANTIATION_ARGS);
                        if (_instantiationArgs instanceof List<?>)
                        {
                            instantiationArgs = (List<Object>) _instantiationArgs;
                        }
                                
                            
                        // Construct and add the new WidgetData object to the list...
                        widgetDataList.add(new WidgetData(name, provideMessages, provideOptions, options, assignmentVariable, instantiationArgs));
                    }
                    else
                    {
                        // A "name" property must be provided in the widget data object (or that object should be a String)
                        // if this is not the case then we cannot construct the widget...
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("A \"" + DirectiveConstants.NAME + "\" property must be included in each object in the \"" + DirectiveConstants.WEBSCRIPT_WIDGETS + "\" array " + 
                                        "as this is the fully-qualified name of the JavaScript widget to be instantiated. The \"" + DirectiveConstants.NAME + "\" property " +
                                        "was either not supplied or was not a String");
                        }
                    }
                }
                else
                {
                    // We expect each object in the WEB_SCRIPTS array to be an object containing the data necessary 
                    // to instantiate a JavaScript widget...
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Each object in the \"" + DirectiveConstants.WEBSCRIPT_WIDGETS + "\" array should be an object that contains the data " +
                                    "necessary to instantiate a JavaScript widget. The type detected was a: " + w.getClass());
                    }
                }
            }
        }
        else
        {
            // We were expecting the WEBSCRIPT_WIDGETS object in the model to be a SimpleSequence of 
            // maps containing the data necessary to generate the JavaScript to instantiate each widget.
            if (logger.isDebugEnabled())
            {
                logger.debug("When using the " + OutputJavaScriptDirective.OUTPUT_JS_DEPENDENCIES_DIRECTIVE_NAME + " directive it is expected that the associated " +
                            "model should contain an object \"" + DirectiveConstants.WEBSCRIPT_WIDGETS + "\" that is an array of objects, where each object contains the data " +
                            "necessary to instantiate a JavaScript widget. The type detected was a: " + ((o != null) ? o.getClass(): null));
            }
        }
        
        return widgetDataList;
    }
    
    /**
     * <p>Contains the data that defines a JavaScript widget to be instantiated. This inner class should be overridden as 
     * nessary when the default attributes to not provide enough data for the widget to be instantiated (the
     * <code>buildWidgetInstantiatationScript</code> method will also need to be overridden to make use of the extra data and
     * the <code>buildWidgetDataList</code> method will need to be overridden to instantiate the overridden type.</p>
     * <p>By default this object simply contains the widget name, whether or not to assign i18n messages, whether or not to 
     * assign configuration options and the options to assign.</p>
     * @author David Draper
     */
    protected class WidgetData
    {
        protected WidgetData(String name, 
                             boolean provideMessages, 
                             boolean provideOptions, 
                             Map<Object, Object> options,
                             String assignmentVariableName,
                             List<Object> additionalInstantiationArgs)
        {
            this.name = name;
            this.provideMessages = provideMessages;
            this.provideOptions = provideOptions;
            if (options == null)
            {
                this.options = new HashMap<Object, Object>();
            }
            else
            {
                this.options = options;
            }
            this.assignmentVariableName = assignmentVariableName;
            this.additionalInstantiationArgs = additionalInstantiationArgs;
        }
        
        private String name;
        private boolean provideMessages;
        private boolean provideOptions;
        private Map<Object, Object> options;
        private String assignmentVariableName;
        private List<Object> additionalInstantiationArgs;
        
        public String getName()
        {
            return name;
        }
        public boolean isProvideMessages()
        {
            return provideMessages;
        }
        public boolean isProvideOptions()
        {
            return provideOptions;
        }
        public Map<Object, Object> getOptions()
        {
            return options;
        }
        public String getAssignmentVariableName()
        {
            return assignmentVariableName;
        }
        public List<Object> getAdditionalInstantiationArgs()
        {
            return additionalInstantiationArgs;
        }
    }
}
