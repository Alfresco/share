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

import org.springframework.extensions.surf.ModelObject;

/**
 * <p>A <code>SurfBug</code> object is used for writing debug HTML to the
 * output stream when SurfBug mode is enabled.</p>
 * 
 * @author David Draper
 */
public interface SurfBug extends ModelObject
{
    // type
    public static String TYPE_ID = "surfbug";
    
    // properties
    public static String PROP_SURFBUG_TYPE = "surfbug-type";

    public String getSurfBugType();
    
    public void setSurfBugType(String surfBugType);
    
    /**
     * <p>Should set the <code>Component</code> currently being debugged.</p>
     * @param component
     */
    public void setCurrentComponent(Component component);
    
    /**
     * <p>Should return the <code>Component</code> currently being debugged.</p>
     * @return
     */
    public Component getCurrentComponent();
    
    /**
     * <p>Should return a boolean value indicating whether or not the supplied <code>Component</code>
     * has already been debugged</p>
     * @param object
     * @return
     */
    public boolean hasBeenDebugged(Component object);
}
