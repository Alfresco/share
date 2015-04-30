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

import org.springframework.extensions.webscripts.WebScript;

public interface SurfBugData
{
    /**
     * Store the WebScript resolved for this component (this will only be set if the
     * Component is backed by a WebScript).
     * 
     * @param webScript
     */
    public void setResolvedWebScript(WebScript webScript);
    
    /**
     * Return the WebScript that was resolved to render this component (this will only
     * be available during rendering) it will return null if the component is not backed
     * by a WebScript.
     * 
     * @return
     */
    public WebScript getResolvedWebScript();
}
