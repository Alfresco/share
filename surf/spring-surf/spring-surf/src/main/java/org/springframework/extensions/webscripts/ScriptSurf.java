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

package org.springframework.extensions.webscripts;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;

/**
 * A root-scoped Java object that represents the framework configuration
 * 
 * @author muzquiano
 */
@ScriptClass 
(
        help="Root-scoped Java object that represents the configuration of Spring Surf",
        types=
        {
                ScriptClassType.JavaScriptRootObject,
                ScriptClassType.TemplateRootObject
        }
)
public final class ScriptSurf extends ScriptBase
{
    public ScriptSurf(RequestContext context)
    {
        super(context);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebFrameworkScriptBase#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        return null;
    }
    
    
    // --------------------------------------------------------------
    // JavaScript Properties
    
    @ScriptMethod 
    (
            help="Indicates whether Spring Surf has a non-default User Factory configured for it",
            output="Whether a user factory is configured"
    )
    public boolean getLoginEnabled()
    {
        boolean enabled = false;
        
        String defaultUserFactoryId =context.getServiceRegistry().getWebFrameworkConfiguration().getDefaultUserFactoryId();
        if (defaultUserFactoryId != null)
        {
            if (!WebFrameworkConstants.DEFAULT_USER_FACTORY_ID.equals(defaultUserFactoryId))
            {
                enabled = true;
            }
        }
        
        return enabled;       
    }
}
