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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;

/**
 * A helper class with static functions for working with Scriptable maps,
 * arrays and ScriptModelObjects.
 * 
 * @author muzquiano
 */
public final class ScriptHelper implements Serializable
{
    /**
     * Creates a Scriptable Map for a given array of model objects
     * 
     * @param context the context
     * @param modelObjects the model objects
     * 
     * @return the scriptable map
     */
    public static ScriptableMap toScriptableMap(RequestContext context,
            ModelObject[] modelObjects)
    {
        ScriptableMap<String, Serializable> map =
            new ScriptableLinkedHashMap<String, Serializable>(modelObjects.length);
        
        for (int i = 0; i < modelObjects.length; i++)
        {
            ScriptModelObject scriptModelObject = toScriptModelObject(context, modelObjects[i]);
            String id = modelObjects[i].getId();
            map.put(id, scriptModelObject);
        }
        
        return map;
    }

    /**
     * Creates a Scriptable Map for a given map of model objects
     * 
     * @param context the context
     * @param objects a map of model objects (keyed by object id)
     * 
     * @return the scriptable map
     */
    public static ScriptableMap toScriptableMap(RequestContext context,
            Map<String, ModelObject> objects)
    {
        ScriptableMap<String, Serializable> map = new ScriptableLinkedHashMap<String, Serializable>(objects.size());
        
        // convert to map of script model objects
        Iterator it = objects.keySet().iterator();
        while (it.hasNext())
        {
            String id = (String) it.next();
            ModelObject modelObject = (ModelObject) objects.get(id);
            
            ScriptModelObject scriptModelObject = toScriptModelObject(context, modelObject);
            map.put(id, scriptModelObject);            
        }
        
        return map;
    }
    
    /**
     * Converts an existing map to a Scriptable map
     * 
     * @param map the map
     * 
     * @return the scriptable map
     */
    public static ScriptableMap toScriptableMap(Map<String, Serializable> map)
    {
        return new ScriptableLinkedHashMap<String, Serializable>(map);
    }

    /**
     * Converts a given array to a Scriptable array that can be traversed
     * by the script and Freemarker engines
     * 
     * @param scope the scope
     * @param elements the elements
     * 
     * @return the scriptable
     */
    public static Scriptable toScriptableArray(Scriptable scope, String[] elements)
    {
        Object[] array = new Object[elements.length];
        for (int i = 0; i < elements.length; i++)
        {
            array[i] = elements[i];
        }

        return Context.getCurrentContext().newArray(scope, array);
    }

    /**
     * Wraps a ModelObject with a script wrapper to produce a ScriptModelObject
     * that can be used by the script and Freemarker engines.
     * 
     * @param context the context
     * @param modelObject the model object
     * 
     * @return the script model object
     */
    public static ScriptModelObject toScriptModelObject(RequestContext context,
            ModelObject modelObject)
    {
        if (modelObject != null)
        {
            return new ScriptModelObject(context, modelObject);
        }
        return null;
    }

    /**
     * Converts an array of ModelObjects to an array of ScriptModelObjects
     * which can be used by the script and Freemarker engines.
     * 
     * @param context the context
     * @param modelObjects the model objects
     * 
     * @return the object[]
     */
    public static Object[] toScriptModelObjectArray(RequestContext context,
            ModelObject[] modelObjects)
    {
        Object[] array = new Object[] {};
        if (modelObjects != null)
        {
            array = new Object[modelObjects.length];
            for (int i = 0; i < modelObjects.length; i++)
            {
                array[i] = toScriptModelObject(context, modelObjects[i]);
            }
        }
        return array;
    }

    /**
     * Converts a map of model objects to an array of ScriptModelObjects
     * which can be used by the script and Freemarker engines.
     * 
     * @param context the context
     * @param objects the model objects
     * 
     * @return the object[]
     */
    public static Object[] toScriptModelObjectArray(RequestContext context,
            Map<String, ModelObject> objects)
    {
        // convert to array
        Object[] array = objects.values().toArray(new Object[objects.size()]);
        
        // walk through array and wrap everything as a script model object
        for (int i = 0; i < array.length; i++)
        {
            array[i] = toScriptModelObject(context, (ModelObject)array[i]);
        }
        
        return array;
    }
    
    
    /**
     * Retrieves a model object from the underlying store and hands it back
     * wrapped as a ScriptModelObject.  If the model object cannot be found,
     * null will be returned.
     * 
     * @param id the id
     * 
     * @return the script model object
     */
    public static ScriptModelObject getObject(RequestContext context, String objectTypeId, String objectId)
    {
        ScriptModelObject scriptModelObject = null;
        
        ModelObject modelObject = context.getObjectService().getObject(objectTypeId, objectId);
        if (modelObject != null)
        {
            scriptModelObject = new ScriptModelObject(context, modelObject);
        }
        
        return scriptModelObject;
    }        
}
