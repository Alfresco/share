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
package org.springframework.extensions.surf.extensibility;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.types.SubComponent;

/**
 * <p>A ComponentElementEvaluation is used to evaluate whether or not a {@link SubComponent} should be
 * rendered or not and if it should be rendered the URI of the WebScript that should be used to perform
 * the rendering.</p>
 * 
 * @author David Draper
 */
public interface SubComponentEvaluation
{
    /**
     * An evaluation should define an ID to help with debugging. This will return it.
     * @return
     */
    public String getId();
    
    /**
     * @return The configured URI to use to render the {@link SubComponent} if all configured
     * {@link SubComponentEvaluator} instances pass successfully.
     */
    public String getUri();

    /**
     * <p>Processes all the configured {@link ComponentEvaluator} instances and returns the overall
     * result. This is effectively an AND gate such that it will only return <code>true</code> if 
     * every {@link SubComponentEvaluator} returns true.</p>
     * @param context The current {@link RequestContext}
     * @param applicationContext The {@link ApplicationContext} to use to look up the {@link SubComponentEvaluator}
     * instances from.
     * @return
     */
    public boolean evaluate(RequestContext context, ApplicationContext applicationContext);
    
    /**
     * <p>Indicates whether or not the successful evaluation means that the {@link SubComponent} should
     * be rendered or not. This should return <code>false</code> if the the <{@code}evaluation> XML configuration
     * includes has the attribute <code>renderIfEvaluated</code> set to the value "false".</p>
     *  
     * @return
     */
    public boolean renderIfEvaluated();
    
    
    public Map<String, String> getProperties();
}
