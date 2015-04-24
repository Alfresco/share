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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.tree.BaseElement;
import org.dom4j.tree.DefaultDocument;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.SubComponentEvaluation;
import org.springframework.extensions.surf.extensibility.XMLHelper;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluation;
import org.springframework.extensions.surf.render.AbstractRenderableModelObject;
import org.springframework.extensions.surf.render.Renderable;
import org.springframework.extensions.surf.uri.UriUtils;
import org.springframework.extensions.webscripts.WebScript;

/**
 * <p>A type of {@link Renderable} object that is referenced by an {@link AdvancedComponent}.
 * An {@link AdvancedComponent} can have zero or more ComponentElements that it uses to render its content. This
 * allows the {@link AdvancedComponent} to behave as a group into which multiple ComponoentElements can be added
 * into.</p>
 * <p>Although a ComponentElement is an instance of a {@link ModelObject} it cannot be represented by it's own
 * configuration. Instead it is "hidden" from the Spring Surf configuration so that it can only be defined within
 * and {@link AdvancedComponent} or {@link ModuleDeployment}.</p>
 *   
 * @author David Draper
 */
public class SubComponent extends AbstractRenderableModelObject implements Comparable<SubComponent>, SurfBugData
{
    private static final Log logger = LogFactory.getLog(SubComponent.class);
    
    private static final long serialVersionUID = -7181077392520052355L;

    public static final String SUB_COMPONENT_TYPE = "sub-component";
    
    public SubComponent(String id, String parentId)
    {
        super(parentId + "#" + id, null, new DefaultDocument(new BaseElement("generated-document").addAttribute("component", parentId).addAttribute("sub-component", id)));
        this.baseId = id;
        this.parentId = parentId;
        this.displayId = "Component: " + parentId + ", Sub-Component: " + id;
    }
    
    /**
     * <p>This is required for cloning.</p>
     */
    private String baseId = null;
    
    
    /**
     * <p>The id of the parent {@link AdvancedComponent} to which this SubComponent belongs.</p>
     */
    private String parentId = null;
    
    public String getParentId()
    {
        return this.parentId;
    }

    /**
     * <p>A useful String to use when logging the id of the SubComponent as it shows both the parent component
     * as well as the sub-component id</p>
     */
    private String displayId = null;
    
    @Override
    public String toString()
    {
        return this.displayId;
    }

    /**
     * <p>The index of the SubComponent is used to sort lists of SubComponents into the correct
     * order in which to be processed. It will default to the value 50 if there is no index definition in the 
     * configuration. The lower the index the earlier the SubComponent will be processed.</p>
     */
    private Integer index = null;
    
    public Integer getIndex()
    {
        return index;
    }
    
    private void setIndex(Integer index)
    {
        this.index = index;
    }

    private Integer baseIndex = null;
    
    private String uri = null;
    
    private String baseUri = null;
    
    public void setUri(String uri)
    {
        this.uri = uri;
        this.baseUri = uri;
    }

    private Map<String, String> defaultProperties = new HashMap<String, String>();
    
    /**
     * <p>This is only part of the SubComponent to support the conversion of "old-style" {@link Component} instances.
     * Ideally the {@link AdvancedComponent} approach would only support WebScript rendered components but they need to 
     * support the conversion of Components rendered by other processors.</p>
     */
    private String processorId = null;
    
    /**
     * <p>This is the id of the processor defined in the base configuration. It is preserved in order to restore the SubComponent
     * to it's original status which may have changed due to merging extension configuration.</p>
     */
    private String baseProcessorId = null;
    
    public String getProcessorId()
    {
        return processorId;
    }

    public void setProcessorId(String processorId)
    {
        this.processorId = processorId;
        this.baseProcessorId = processorId;
    }

    /**
     * <p>This is only part of the SubComponent to support the conversion of "old-style" {@link Component} instances.
     * Ideally the {@link AdvancedComponent} approach would only support WebScript rendered components but they need to 
     * support the conversion of Components rendered by other processors that may be defined by a {@link ComponentType}.</p>
     */
    private String componentTypeId = null;
    
    /**
     * <p>This is the id of the component type defined in the base configuration. It is preserved in order to restore the SubComponent
     * to it's original status which may have changed due to merging extension configuration.</p>
     */
    private String baseComponentTypeId = null;
    
    public String getComponentTypeId()
    {
        return componentTypeId;
    }

    public void setComponentTypeId(String componentTypeId)
    {
        this.componentTypeId = componentTypeId;
        this.baseComponentTypeId = componentTypeId;
    }
    
    /**
     * <p>This maintains the index into which {@link ModuleDeployment} extensions providing
     * updates to the evaluations should be added. This is done so that the deployer can
     * control the order of evaluations. The default evaluations (i.e those defined in
     * the {@link AdvancedComponent} will be processed last (in the order in which they
     * were defined in XML).</p>
     */
    private int lastEvaluationMergeIndex = 0;
    /**
     * <p>Merges the overrides defined in the supplied extension.</p>
     * 
     * @param ext The extending {@link SubComponent} to merge.
     */
    public void mergeExtension(SubComponent ext)
    {
        this.index = (ext.index != null) ? ext.index : this.index;
        this.uri = (ext.uri != null) ? ext.uri : this.uri;
        for (Entry<String, String> property: ext.defaultProperties.entrySet())
        {
            this.defaultProperties.put(property.getKey(), property.getValue());
        }
        // Add an extending evaluations at the start of the evaluation list...
        this.evaluations.addAll(lastEvaluationMergeIndex, ext.getEvaluations());
        lastEvaluationMergeIndex += ext.getEvaluations().size();
    }

    /**
     * <p>Clones the current object, resetting all the key data as it does so. This method should be called 
     * before applying extensions to ensure that the correct base data is set for extending and that multiple
     * threads won't attempt to concurrently modify the original.</p>
     */
    public SubComponent clone()
    {
        SubComponent clone = new SubComponent(this.baseId, this.parentId);
        clone.setIndex((this.baseIndex != null ? new Integer(this.baseIndex) : null));
        clone.setUri(this.baseUri);
        clone.getEvaluations().addAll(this.baseEvaluations);
        clone.setProcessorId(this.baseProcessorId);
        clone.setComponentTypeId(this.baseComponentTypeId);
        for (Entry<String, String> property: this.defaultProperties.entrySet())
        {
            clone.defaultProperties.put(property.getKey(), property.getValue());
        }
        clone.setAllProperties(this.getModelProperties(), this.getCustomProperties());
        return clone;
    }
    
    /**
     * <p>A SubComponent can be configured to process zero or more {@link SubComponentEvaluation}
     * instances to determine whether or not it should be rendered and if so the URL to use to perform
     * the rendering. This {@link List} that maintains those {@link SubComponentEvaluation} instances.
     * It is populated by the <code>applyConfiguration</code> and <code>mergeExtension</code> methods.</p>
     */
    private List<SubComponentEvaluation> evaluations = new ArrayList<SubComponentEvaluation>();
    
    /**
     * <p>The evaluations that defined in the base configuration of the SubComponent. These are set during
     * the <code>applyConfig</code> method and are set and replace the contents of the <code>evaluations</code>
     * when the <code>reset</code> method is called. This is done to remove any previous extension merge
     * data.</p> 
     */
    private List<SubComponentEvaluation> baseEvaluations = new ArrayList<SubComponentEvaluation>();
    
    /**
     * @return The {@link List} of {@link SubComponentEvaluation} instances configured for this SubComponent 
     */
    public List<SubComponentEvaluation> getEvaluations()
    {
        return evaluations;
    }

    /**
     * <p>Provides data about the rendering of a {@link SubComponent}, specifically whether or not it
     * should be rendered, the URI to render it and the properties associated with it.</p> 
     */
    public class RenderData
    {
        private boolean shouldRender = false;
        private String uri = null;
        private Map<String, Serializable> properties = null;
        private SubComponentEvaluation evaluation = null;
        public RenderData(boolean shouldRender, String uri, Map<String, Serializable> properties, SubComponentEvaluation evaluation)
        {
            this.shouldRender = shouldRender;
            this.uri = uri;
            this.properties = properties;
            this.evaluation = evaluation;
        }
        public boolean shouldRender()
        {
            return this.shouldRender;
        }
        public String getUri()
        {
            return this.uri;
        }
        public Map<String, Serializable> getProperties()
        {
            return this.properties;
        }
        public SubComponentEvaluation getEvaluation()
        {
            return this.evaluation;
        }
        @Override
        public String toString()
        {
            return "Should render: " + shouldRender + ", URI: " + uri;
        }
    }
    
    /**
     * <p>Determines the URL to use to render the SubComponent instance. The URL returned will be
     * defined in the first successfully passed {@link SubComponentEvaluation} processed (providing
     * that it is configured to enable the SubComponent to render). If there are no {@link SubComponentEvaluation}
     * instances configured then the default URL is returned. If a {@link SubComponentEvaluation} 
     * evaluates but indicates that the SubComponent should not be rendered or no URL is defined then
     * <code>null</code> will be returned which indicates that nothing will get rendered.</p>
     * 
     * @param context The current {@link RequestContext} is supplied to for the {@link SubComponentEvaluation} instances to use.
     * @return The URL to use to render the SubComponent or <code>null</code> if it should not be rendered.
     */
    public RenderData determineURI(RequestContext context, ApplicationContext applicationContext)
    {
        SubComponentEvaluation successfulEvaluation = null;
        String uri = this.uri;
        boolean shouldRender = true;
        HashMap<String, Serializable> evaluatedProps = new HashMap<String, Serializable>();
        evaluatedProps.putAll(this.defaultProperties);
        
        if (this.evaluations != null && this.evaluations.isEmpty())
        {
            // If there are no evaluations, then just use the default URI...
            if (this.uri != null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("No <" + AdvancedComponent.EVALUATIONS + "> are available for <" + AdvancedComponent.SUB_COMPONENT + "> '" + this.displayId + "' - using default URI: " + this.uri);
                }
                uri = this.uri;
            }
            else if (this.componentTypeId == null && this.processorId == null)
            {
                // If the uri is null then we should not render the sub-component UNLESS there is a componentTypeId
                // or a processorId as this indicates that this sub-component was converted from legacy configuration
                // in which case it will be valid to return a URI of null as the correct URI will be determined by 
                // other means.
                if (logger.isErrorEnabled())
                {
                    logger.error("There are no <" + AdvancedComponent.EVALUATIONS + "> for <"  + AdvancedComponent.SUB_COMPONENT + "> '" + this.displayId + "' and it has not been configured with a " + AdvancedComponent.URI + ". Therefore the <" + AdvancedComponent.SUB_COMPONENT + "> cannot be rendered");
                }
                shouldRender = false;
            }
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Processing <" + AdvancedComponent.EVALUATIONS + "> for <" + AdvancedComponent.SUB_COMPONENT + "> '" + this.displayId + "'");
            }
            boolean evaluationPassed = false;
            for (SubComponentEvaluation evaluation: this.evaluations)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Evaluating <" + AdvancedComponent.SUB_COMPONENT + "> '" + this.displayId + "' using " + AdvancedComponent.EVALUATION + " '" + evaluation.getId() + "'");
                }
                
                // Evaluate the current evaluation, if it successfully passes then check to see if that
                // means that the SubComponent should be rendered or not (since an extension could have
                // provided an evaluation to prevent a SubComponent from rendering in certain circumstance).
                if (evaluation.evaluate(context, applicationContext))
                {
                    // This cannot be an AND in the enclosing if statement because we always need to break regardless...
                    if (evaluation.renderIfEvaluated())
                    {
                        // Get the URI from the evaluation, if it is null then use the default.
                        uri = evaluation.getUri();
                        if (uri == null)
                        {
                            if (this.uri == null)
                            {
                                if (logger.isErrorEnabled())
                                {
                                    logger.error("Neither <" + AdvancedComponent.EVALUATION + "> '" + evaluation.getId() + "' nor <"  + AdvancedComponent.SUB_COMPONENT + "> '" + this.displayId + "' have not been configured with a " + AdvancedComponent.URI + ". Therefore the <" + AdvancedComponent.SUB_COMPONENT + "> cannot be rendered");
                                }
                            }
                            else
                            {
                                uri = this.uri;
                            }
                        }
                        
                        // Added evaluated properties...
                        evaluatedProps.putAll(evaluation.getProperties());
                    }
                    else
                    {
                        // If the evaluation indicates that the SubComponent should not be rendered then set the URI to be null
                        // indicates that it should not be rendered...
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("<" + AdvancedComponent.EVALUATION + "> '" + evaluation.getId() + "' has passed indicating that <"  + AdvancedComponent.SUB_COMPONENT + "> '" + this.displayId + "' should not be rendered");
                        }
                        uri = null;
                        shouldRender = false;
                        
                    }
                    successfulEvaluation = evaluation;
                    evaluationPassed = true;
                    break;
                }
            }
            
            // If all the evaluations have been processed but a uri was not set
            if (uri == null && !evaluationPassed)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("No evaluations passed for <"  + AdvancedComponent.SUB_COMPONENT + "> '" + this.displayId + "' and it has no default URI so it will not be rendered");
                }
                shouldRender = false;
            }
        }

        // Add the evaluation properties into the default (this allows for overrides)...
        for (Entry<String, Serializable> prop: evaluatedProps.entrySet())
        {
            evaluatedProps.put(prop.getKey(), UriUtils.replaceTokens(prop.getValue().toString(), context, null, null, ""));
        }
        
        RenderData data = new RenderData(shouldRender, uri, evaluatedProps, successfulEvaluation);
        return data;
    }
    
    /**
     * <p>Compares the indices of the current instance and the supplied instance. If either index
     * is null it will be treated as null.</p>
     */
    public int compareTo(SubComponent that)
    {
        int thisIndex = 50;
        if (this.index != null)
        {
            thisIndex = this.index.intValue();
        }
        int thatIndex = 50;
        if (that.index != null)
        {
            thatIndex = that.index.intValue();
        }
        return thisIndex - thatIndex;
    }

    @Override
    public String getTypeId()
    {
        return SUB_COMPONENT_TYPE;
    }
    
    /**
     * <p>The {@link WebScript} that ultimately gets resolved to render this SubComponent. This is
     * provided to support the {@link SurfBugData} interface.</p>
     */
    private WebScript resolvedWebScript;
    
    /**
     * <p>Sets the {@link WebScript} that gets resolved to render this SubComponent.</p>
     * @param webScript The resolved {@link WebScript}
     */
    public void setResolvedWebScript(WebScript webScript)
    {
        this.resolvedWebScript = webScript;
    }

    /**
     * @returns the {@link WebScript} that gets resolved to render this SubComponent.
     */
    public WebScript getResolvedWebScript()
    {
        return this.resolvedWebScript;
    }
    
    /**
     * <p>Sets all the properties for this SubComponent. This is provided so that an {@link AdvancedComponent} can parse and
     * old style {@link SubComponent} configuration file and pass the properties to the SubComponent.</p>
     * @param standardProps
     * @param customProps
     */
    public void setAllProperties(Map<String, Serializable> standardProps, Map<String, Serializable> customProps)
    {
        this.modelProperties = standardProps;
        this.customProperties = customProps;
    }
    
    /**
     * <p>Parses the supplied configuration {@link Element} (which should be a <{@code}renderable-element>) and
     * applies the data therein to the current instance. This should result in the index, uri and evaluation (and
     * associated evaluators) being set.</p>
     * @param componentEl The <{@code}renderable-element> configuration {@link Element} to parse and apply.
     */
    @SuppressWarnings("unchecked")
    public void applyConfiguration(Element componentEl)
    {
        // Get an index if one has been provided. The index is used when sorting collections of SubComponent.
        // If no index is provided the SubComponent will default to having an index of 50. It is not a required
        // part of the configuration, if invalid data is provided though we will output an error message.
        String index = componentEl.attributeValue(AdvancedComponent.INDEX);
        if (index != null)
        {
            try
            {
                this.index = Integer.parseInt(index);
                this.baseIndex = Integer.parseInt(index); // Backup for reset method...
            }
            catch (NumberFormatException e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error("Invalid " + AdvancedComponent.INDEX + "'" + index + "' defined for " + AdvancedComponent.SUB_COMPONENT + "'" + this.getId() + "'");
                }
            }
        }
        
        // Get the <uri> element if one is defined and use it to set the uri instance variable...
        Element uriEl = componentEl.element(AdvancedComponent.URI);
        if (uriEl != null)
        {
            this.uri = uriEl.getTextTrim();
        }
        else
        {
            // Try "URL" instead of "URI"...
            Element evalutionUrlEl = componentEl.element(AdvancedComponent.URL);
            if (evalutionUrlEl != null)
            {
                this.uri = evalutionUrlEl.getTextTrim();
            }
        }
        
        // Backup for reset method...
        this.baseUri = this.uri;
        
        // Set the default properties for the renderable-element - these properties will be used if no evaluation is passed.
        Element propertiesEl = componentEl.element(AdvancedComponent.PROPERTIES);
        if (propertiesEl != null)
        {
            List<Element> propertiesList = propertiesEl.elements();
            for (Element property: propertiesList)
            {
                this.defaultProperties.put(property.getName(), property.getTextTrim());
            }
        }
        
        Element evaluationsEl = componentEl.element(AdvancedComponent.EVALUATIONS);
        if (evaluationsEl != null) 
        {
            List<Element> evaluationList = evaluationsEl.elements(AdvancedComponent.EVALUATION);
            for (Element evaluationEl: evaluationList)
            {
                // Get the id of the evaluation - this is not used for anything other than 
                // the purpose of assisting with debug logging...
                String id = evaluationEl.attributeValue(AdvancedComponent.ID);
                if (id == null)
                {
                    id = "";
                }
                
                // Get the attribute that indicates if this evaluation should ensure that the SubComponent
                // is rendered when passed (a user may want to set the attribute to false to prevent a 
                // SubComponent from getting rendered under certain circumstances...
                String renderIfEvaluatedStr = evaluationEl.attributeValue(AdvancedComponent.RENDER_IF_EVALUATED);
                if (renderIfEvaluatedStr == null)
                {
                    renderIfEvaluatedStr = XMLHelper.getStringData(AdvancedComponent.RENDER_IF_EVALUATED, evaluationEl, false);
                }
                
                // If render data hasn't been set, then successful evaluation should result in rendering...
                boolean renderIfEvaluated = (renderIfEvaluatedStr == null || Boolean.parseBoolean(renderIfEvaluatedStr));
                
                // We've found an <evaluation> element so we can create a new Evaluation object...
                DefaultSubComponentEvaluation dcee = new DefaultSubComponentEvaluation(id, renderIfEvaluated);
                this.evaluations.add(dcee);
                this.baseEvaluations.add(dcee); // Backup for reset method
                
                // If we are adding an evaluation then we know that we are dealing with a new style component
                // and as such we should ensure that neither ComponentType nor Processor are configured for
                // the SubComponent. This only works because a Component with no ComponentType but a URI
                // is treated as a WebScript.
                this.componentTypeId = null;
                this.processorId = null;
                
                // Set the evaluation URI if provided (this will override the SubComponent URI)...
                Element evalutionUriEl = evaluationEl.element(AdvancedComponent.URI);
                if (evalutionUriEl != null)
                {
                    dcee.setUri(evalutionUriEl.getTextTrim());
                }
                else
                {
                    // Try "URL" instead of "URI"...
                    Element evalutionUrlEl = evaluationEl.element(AdvancedComponent.URL);
                    if (evalutionUrlEl != null)
                    {
                        dcee.setUri(evalutionUrlEl.getTextTrim());
                    }
                }
                
                // Set the properties for the evaluation...
                Element evalPropertiesEl = evaluationEl.element(AdvancedComponent.PROPERTIES);
                if (evalPropertiesEl != null)
                {
                    List<Element> propertiesList = evalPropertiesEl.elements();
                    for (Element property: propertiesList)
                    {
                        dcee.addProperty(property.getName(), property.getTextTrim());
                    }
                }
                
                // Set the evaluators from the supplied configuration...
                Element evaluatorsEl = evaluationEl.element(AdvancedComponent.EVALUATORS);
                if (evaluatorsEl != null)
                {
                    List<Element> evaluatorList = evaluatorsEl.elements(AdvancedComponent.EVALUATOR);
                    for (Element evaluatorEl: evaluatorList)
                    {
                        // Attempt to locate the Evaluator bean...
                        String evaluatorType = evaluatorEl.attributeValue(AdvancedComponent.TYPE);
                        if (evaluatorType != null)
                        {
                            Map<String, String> evaluatorParams = XMLHelper.getProperties(AdvancedComponent.PARAMS, evaluatorEl);
                            boolean negate = XMLHelper.getBooleanAttribute(AdvancedComponent.NEGATE, evaluatorEl, false);
                            
                            // Add the evaluator to the evalution...
                            dcee.addEvaluator(evaluatorType, evaluatorParams, negate);
                        }
                    }
                }
            }
        }
    }
}
