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

import java.util.Map;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.SubComponentEvaluator;
import org.springframework.extensions.surf.types.SubComponent;

/**
 * <p>A sample {@link SubComponentEvaluator} that always evaluates to false. This can be referenced
 * in an <{@code}evaluation> element to ensure that a {@link SubComponent} is not rendered.</p>
 * 
 * @author David Draper
 */
public class RejectionSubComponentEvaluator implements SubComponentEvaluator
{
    /**
     * @return Always returns <code>false</code> regardless of the context and params arguments supplied.
     */
    public boolean evaluate(RequestContext context, Map<String, String> params)
    {
        return false;
    }

    private String beanName = null;
    
    public String getBeanName()
    {
        return beanName;
    }

    public void setBeanName(String name)
    {
        this.beanName = name;
    }
}
