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

package org.springframework.extensions.surf.render;

import java.util.Map;

/**
 * Implemented by object types which wish to expose a set of renderer
 * configurations.  Each renderer configuration identifies a processor
 * id and provides the processor with information about how to render.
 * <p>
 * A renderable object is one that has renderer processors
 * defined on it for one or more render modes. 
 * 
 * @author muzquiano
 */
public interface Renderable 
{
    /*
    JSP EXAMPLE:

    <processor mode="view">
       <id>jsp</id>
       <url>/abc/view.jsp</url>
    </processor>    
    <processor mode="edit">
       <id>jsp</id>
       <url>/abc/edit.jsp</url>
    </processor>
    
    
    WEBSCRIPT:

    <processor mode="view">
       <id>webscript</id>
    </processor>
    <processor mode="edit">
       <id>webscript</id>
       <uri>${mode.view.uri}/edit</uri>
    </processor>
     */
    
    /**
     * The list of defined render modes
     * 
     * @return an array of render modes
     */
    public RenderMode[] getRenderModes();

    /**
     * Gets the default 'view' processor id
     * 
     * @return the processor id
     */
    public String getProcessorId();
    
    /**
     * Gets the processor id
     * 
     * @param renderMode
     * 
     * @return the processor id
     */
    public String getProcessorId(RenderMode renderMode);

    /**
     * Gets a default 'view' processor property
     * 
     * @param propertyName
     * 
     * @return the processor property value
     */
    public String getProcessorProperty(String propertyName);
    
    /**
     * Gets a processor property
     * 
     * @param renderMode
     * @param propertyName
     * 
     * @return the processor property value
     */
    public String getProcessorProperty(RenderMode renderMode, String propertyName);
    
    /**
     * Gets a map of default 'view' processor properties
     *  
     * @return the map
     */
    public Map<String, String> getProcessorProperties();

    /**
     * Gets a map of processor properties for the given mode
     *  
     * @param renderMode the render mode
     * 
     * @return the map
     */    
    public Map<String, String> getProcessorProperties(RenderMode renderMode);  
}