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

import java.util.Map;

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.bean.Index;

/**
 * Overridden index of all Web Scripts to include SurfBug information.
 * 
 * @author David Draper
 */
public class IndexWithSurfBug extends Index
{
    /**
     * <p>The <code>WebFrameworkConfigElement</code> is required to enable/disable SurfBug.</p>
     */
    protected WebFrameworkConfigElement webFrameworkConfigElement;
    
    public void setWebFrameworkConfigElement(WebFrameworkConfigElement webFrameworkConfigElement)
    {
        this.webFrameworkConfigElement = webFrameworkConfigElement;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {        
        Map<String, Object> model = super.executeImpl(req, status);
        model.put("surfbugEnabled", this.webFrameworkConfigElement.isSurfBugEnabled());
        return model;
    }

}
