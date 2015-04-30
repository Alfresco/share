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

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.types.SubComponent;
import org.springframework.extensions.surf.types.ModuleDeployment;

/**
 * <p>A SubComponentEvaluator is used to help evaluate whether or not a {@link SubComponent} should 
 * be rendered or not. One or more SubComponentEvaluators are called upon by a {@link SubComponentEvaluation}
 * to determine whether a {@link SubComponent} should be rendered. If the <code>evaluate</code> method of
 * every SubComponentEvaluator returns <code>true</code> the the {@link SubComponent} will be rendered.</p>
 * <p>Each SubComponentEvaluator implementation should be configured as a Spring bean in the application context
 * and it's bean id should be referenced in the {@link Component} or {@link ModuleDeployment} configuration (depending 
 * upon whether or not it is being defined as part of base or extension configuration.</p>
 * 
 * @author David Draper
 */
public interface SubComponentEvaluator extends BeanNameAware
{
    /**
     * <p>Evaluates whether or not the information in the supplied {@link RequestContext} meets the criteria
     * defined in the supplied parameter {@link Map}.</p>
     * @param context The {@link RequestContext} currently being serviced.
     * @param params A {@link Map} of name/value parameters that define the evaluation criteria.
     * @return <code>true</code> if the evaluation passes and <code>false</code> otherwise.
     */
    public boolean evaluate(RequestContext context, Map<String, String> params);
}
