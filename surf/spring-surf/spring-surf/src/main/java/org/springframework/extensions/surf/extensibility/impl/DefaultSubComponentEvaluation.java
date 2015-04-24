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
package org.springframework.extensions.surf.extensibility.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.SubComponentEvaluation;
import org.springframework.extensions.surf.extensibility.SubComponentEvaluator;
import org.springframework.extensions.surf.types.SubComponent;
import org.springframework.extensions.surf.uri.UriUtils;

/**
 * <p>This is the default implementation of the {@link SubComponentEvaluation} interface. It is
 * used by the {@link SubComponent}.</p>
 * @author David Draper
 *
 */
public class DefaultSubComponentEvaluation implements SubComponentEvaluation
{
    private static final Log logger = LogFactory.getLog(DefaultSubComponentEvaluation.class);
    
    public DefaultSubComponentEvaluation(String id, boolean renderIfEvaluated)
    {
        this.id = id;
        this.renderIfEvaluated = renderIfEvaluated;
    }
    
    /**
     * <p>An identifier can optionally be provided for an evaluation to assist with debugging via
     * log messages.</p>
     */
    private String id;
    
    public String getId()
    {
        return id;
    }

    /**
     * <p>This is the URI that should be used to render a {@link SubComponent} if the evaluation passes.</p>
     */
    private String uri = null;
    
    /**
     * <p>Sets the URI that should be used to render a {@link SubComponent} if the evaluation passes.</p>
     * @param uri The URI to set.
     */
    public void setUri(String uri)
    {
        this.uri = uri;
    }
    
    /**
     * @return This is the URI that should be used to render a {@link SubComponent} if the evaluation passes.
     */
    public String getUri()
    {
        return this.uri;
    }

    private Map<String, String> properties = new HashMap<String, String>();
    
    public void addProperty(String name, String value)
    {
        this.properties.put(name,value);
    }
   
    public Map<String, String> getProperties()
    {
        return this.properties;
    }
    
    private List<Object[]> evaluatorData = new ArrayList<Object[]>();
    
    /**
     * <p>Adds a {@link SubComponentEvaluator} along with the parameters that should be used to evaluate it to the 
     * evaluator map.</p>
     * @param evaluationParams A {@link Map} of name/value parameters that should be passed to the {@link SubComponentEvaluator} when
     * evaluation is performed.
     * @param negate Indicates that the result of the evaluator should be negated (e.g. an evaluator returning <code>false</code> should 
     * be converted to <code>true</code>
     * @param evaluatorid The id of a {@link SubComponentEvaluator} that should be used as part of the overall evaluation.
     */
    public void addEvaluator(String evaluatorId, Map<String, String> evaluationParams, boolean negate)
    {
        Object[] data = new Object[] { evaluatorId, evaluationParams, Boolean.valueOf(negate) };
        evaluatorData.add(data);
    }
    
    /**
     * <p>Iterates over all the entries in the <code>evaluators</code> map evaluating each one.</p>
     * 
     * @return <code>true</code> if all evaluators pass and <code>false</code> otherwise.
     */
    @SuppressWarnings({ "unchecked" })
    public boolean evaluate(RequestContext context, ApplicationContext applicationContext)
    {
        boolean allPass = true;
        for (Object[] data: this.evaluatorData)
        {
            String evaluatorId = (String) data[0];
            if (evaluatorId != null)
            {
                try
                {
                    SubComponentEvaluator evaluator = applicationContext.getBean(evaluatorId, SubComponentEvaluator.class);
                    
                    Map<String, String> tokenizedParams = new HashMap<String, String>();
                    Map<String, String> evaluatorParams = (Map<String, String>) data[1];
                    for (Entry<String, String> prop: evaluatorParams.entrySet())
                    {
                        tokenizedParams.put(prop.getKey(), UriUtils.replaceTokens(prop.getValue(), context, null, null, ""));
                    }
                    
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Evaluating: " + evaluatorId);
                    }
                    boolean evaluatedTrue =  evaluator.evaluate(context, tokenizedParams);
                    if ((Boolean) data[2])
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Negating evaluator result of '" + evaluatedTrue + "' for: " + evaluatorId);
                        }
                        evaluatedTrue = !evaluatedTrue;
                    }
                    allPass = allPass && evaluatedTrue;
                    if (!allPass)
                    {
                        // If the current evaluator has failed then there is no point in evaluating
                        // the others so we'll just break out of the for loop knowing that the return
                        // boolean has been correctly set...
                        break;
                    }
                }
                catch (NoSuchBeanDefinitionException e)
                {
                    if (logger.isErrorEnabled())
                    {
                        logger.error("It was not possible to find " + SubComponentEvaluator.class.getName() + " with the id '" + evaluatorId + "' in the Spring application context", e);
                    }
                    allPass = false;
                    break;
                }
                catch (BeanNotOfRequiredTypeException e)
                {
                    if (logger.isErrorEnabled())
                    {
                        logger.error("The bean with the id '" + evaluatorId + "' was not an instance of " + SubComponentEvaluator.class.getName(), e);
                    }
                    allPass = false;
                    break;
                }
                catch (BeansException e)
                {
                    if (logger.isErrorEnabled())
                    {
                        logger.error("The following exception occurred trying to retrieve " + SubComponentEvaluator.class.getName() + " with the id '" + evaluatorId + "' in the Spring application context", e);
                    }
                    allPass = false;
                    break;
                }
            }
            else 
            {
                if (logger.isWarnEnabled())
                {
                    logger.warn("A " + DefaultSubComponentEvaluation.class.getName() + " has been configured with a null " + SubComponentEvaluator.class.getName());
                }
            }
        }
        return allPass;
    }

    /**
     * <p>This field indicates whether or not a {@link SubComponent} that this {@link SubComponentEvaluation}
     * successfully evaluates for will be rendered or not. It is provided so that {@link ComponentElement} instances can be
     * prevented from being rendered under certain circumstances.</p>
     */
    private boolean renderIfEvaluated = true;
    
    /**
     * @return <code>true</code> if this {@link SubComponentEvaluation} is configured for the purpose of providing
     * a rendering URL and <code>false</code> if it is configured for the purpose of preventing a {@link SubComponent}
     * from being rendered.
     */
    public boolean renderIfEvaluated()
    {
        return renderIfEvaluated;
    }
}
