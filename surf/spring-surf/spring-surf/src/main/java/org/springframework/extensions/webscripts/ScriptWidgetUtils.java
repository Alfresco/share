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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.extensions.directives.ProcessJsonModelDirective;

public class ScriptWidgetUtils extends ScriptBase
{
    private static final long serialVersionUID = 1L;

    @Override
    protected ScriptableMap<String, Serializable> buildProperties()
    {
        return null;
    }
    
    /**
     * This method performs recursive searching through the supplied object.
     * 
     * @param o
     * @param targetAttributeKey
     * @param targetAttributeValue
     * @param delete
     * @param results
     * @return
     */
    public static ScriptableObject findObject(Object o, 
                                              String targetAttributeKey, 
                                              String targetAttributeValue, 
                                              boolean delete, 
                                              RecursionResults results)
    {
        ScriptableObject target = null;
        if (o instanceof NativeObject)
        {
            NativeObject no = (NativeObject) o;
            Object[] allIds = no.getAllIds();
            for (Object currentAttributeKey: allIds)
            {
                // Iterate through all the attributes of an object...
                if (currentAttributeKey instanceof String)
                {
                    Object p = no.get((String) currentAttributeKey, null);
                    if (targetAttributeKey.equals((String) currentAttributeKey))
                    {
                        // The current attribute key is the same as the attribute key that we're looking for...
                        if (p instanceof String && p.equals(targetAttributeValue))
                        {
                            // The current attribute value is a String that matches the value we're searching for...
                            // This means that the object that contains this attribute is our target object.
                            target = no;
                            break;
                        }
                    }
                    else if (p instanceof NativeObject || p instanceof NativeArray)
                    {
                        // Recursively search through any supplied object or array...
                        target = findObject(p, targetAttributeKey, targetAttributeValue, delete, results);
                        if (target != null)
                        {
                            break;
                        }
                    }
                }
            }
        }
        else if (o instanceof NativeArray)
        {
            // Iterate through all the elements of an array...
            Integer index = null;
            NativeArray na = (NativeArray) o;
            for (Object nao: na.getIds())
            {
                index = (Integer) nao;
                target = findObject(na.get(index, null), targetAttributeKey, targetAttributeValue, delete, results);
                if (target != null)
                {
                    break;
                }
            }
            // Handle deletion requests... 
            if (delete && target != null && index != null)
            {
                ScriptableObject.callMethod(Context.getCurrentContext(), 
                                            na, 
                                            "splice", 
                                            new Object[] {index, 1});
                target = null;
                results.setSuccess(true);
            }
        }
        return target;
    }
    
    /**
     * <p>Finds a {@link ScriptableObject} that is located somewhere in the supplied object that has an attribute
     * matching the supplied attribute key and a value matching the supplied attribute value. This method will search
     * through all {@link NativeArray} and {@link NativeObject} instances until it finds a match. 
     * @param o The object to search in (this should be a {@link ScriptableObject} that is either a {@link NativeArray} or a {@link NativeObject}
     * @param targetAttributeKey The value of the attribute key to find (e.g. "id")
     * @param targetAttributeValue The value of the attribute value to find
     * @return The first object matching the target parameters or <code>null</code> if no match can be found.
     */
    public static ScriptableObject findObject(Object o, String targetAttributeKey, String targetAttributeValue)
    {
        RecursionResults results = new RecursionResults();
        ScriptableObject target = findObject(o, targetAttributeKey, targetAttributeValue, false, results);
        return target;
    }
    
    /**
     * <p>Finds and deletes {@link ScriptableObject} that is located in a {@link NativeArray} within the supplied object
     * attribute where the object has the supplied target attribute that matches the supplied target value. The first 
     * object matching this criteria will be deleted - any other occurrences will remain. This method is intended for use
     * in deleting widget definitions from the JSON model that will be processed by the {@link ProcessJsonModelDirective}. 
     * 
     * @param o
     * @param targetAttributeKey
     * @param targetAttributeValue
     * @return
     */
    public static boolean deleteObjectFromArray(Object o, String targetAttributeKey, String targetAttributeValue)
    {
        RecursionResults results = new RecursionResults();
        findObject(o, targetAttributeKey, targetAttributeValue, true, results);
        return results.isSuccess();
    }
}
