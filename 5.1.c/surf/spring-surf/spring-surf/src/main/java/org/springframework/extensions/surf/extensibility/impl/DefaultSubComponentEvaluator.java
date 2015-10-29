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

/**
 * <p>This is the default {@link SubComponentEvaluator} and always evaluates to <code>true</code>. This 
 * is the {@link SubComponentEvaluator} that will be used when no other is configured.</p>
 * 
 * @author David Draper
 */
public class DefaultSubComponentEvaluator implements SubComponentEvaluator
{
    /**
     * @return Always returns <code>true</code> regardless of the context and params arguments supplied.
     */
    public boolean evaluate(RequestContext context, Map<String, String> params)
    {
        return true;
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
