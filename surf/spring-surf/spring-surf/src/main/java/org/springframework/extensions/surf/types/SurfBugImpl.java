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

import org.dom4j.Document;
import org.springframework.extensions.surf.ModelPersisterInfo;
import org.springframework.extensions.surf.render.AbstractRenderableModelObject;

/**
 * <p>A <code>SurfBug</code> object is used for writing debug HTML to the
 * output stream when SurfBug mode is enabled.</p>
 * 
 * @author David Draper
 */
public class SurfBugImpl extends AbstractRenderableModelObject implements SurfBug
{
    private static final long serialVersionUID = 8076013453214119283L;

    public SurfBugImpl(String id, ModelPersisterInfo key, Document document)
    {
        super(id, key, document);
    }    

    public String getTypeId()
    {
        return SurfBug.TYPE_ID;
    }
    
    public String getSurfBugType()
    {
        return getProperty(PROP_SURFBUG_TYPE);
    }
    
    public void setSurfBugType(String surfbugType)
    {
        setProperty(PROP_SURFBUG_TYPE, surfbugType);
    }

    /**
     * <p>The <code>Component</code> currently being debugged.</p>
     */
    private Component currentComponent = null;
    
    /**
     * <p>Set the <code>Component</code> currently being debugged.</p>
     * @param component
     */
    public void setCurrentComponent(Component component)
    {
        this.currentComponent = component;
    }
    
    /**
     * <p>Indicates whether or not the supplied <code>Component</code> has already been debugged.
     * This will return <code>true</code> if the supplied <code>Component</code> matches the 
     * <code>currentComponent</code> instance variable.</p>
     * @param object
     * @return
     */
    public boolean hasBeenDebugged(Component object)
    {
        return (this.currentComponent != null && this.currentComponent.equals(object));
    }

    /**
     * <p>Return the <code>Component</code> currently being debugged.</p>
     * @return
     */
    public Component getCurrentComponent()
    {
        return this.currentComponent;
    }
}
