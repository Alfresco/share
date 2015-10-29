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

package org.springframework.extensions.surf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Describes the context within which a persister should execute.
 * 
 * The object principally stores the user id which may eventually be used
 * by our store implementations to check for user rights against the
 * accessed object.
 * 
 * The object also describes the web framework's currently bound
 * repository store id and web application id (to support Alfresco
 * web projects).
 * 
 * The object may also eventually store role and rights information 
 * regarding who they are in the end application.
 * 
 * @author muzquiano
 */
public final class ModelPersistenceContext
{
    public static String REPO_STOREID = "REPO_STOREID";
    public static String REPO_WEBAPPID = "REPO_WEBAPPID";
    
    private final String userId;
    private final Map<String, Object> values;
        
    /**
     * Instantiates a new persister context.
     * 
     * @param userId the user id
     */
    public ModelPersistenceContext(String userId)
    {
        this.userId = userId;
        this.values = new HashMap<String, Object>(2, 1.0f);
    }

    /**
     * Gets the user id.
     * 
     * @return the user id
     */
    public String getUserId()
    {
        return this.userId;
    }
    
    /**
     * Returns the stored value with the given key
     * 
     * @param key the key
     * 
     * @return the value
     */
    public Object getValue(String key)
    {
        return values.get(key);
    }
    
    /**
     * Stores a value with the given key
     * 
     * @param key the key
     * @param value the value
     */
    public void putValue(String key, Object value)
    {
        this.values.put(key, value);
    }
    
    /**
     * Returns the set of keys
     * 
     * @return the set
     */
    public Set<String> keys()
    {
        return this.values.keySet();
    }
    
    /**
     * Returns the collection of values
     * 
     * @return the collection
     */
    public Collection<Object> values()
    {
        return this.values.values();
    }
    
    /**
     * Sets the store id.
     * 
     * @param storeId the new store id
     */
    public void setStoreId(String storeId)
    {
        this.putValue(REPO_STOREID, storeId);
    }
    
    /**
     * Gets the store id.
     * 
     * @return the store id
     */
    public String getStoreId()
    {
        return (String) this.getValue(REPO_STOREID);
    }
    
    /**
     * Sets the webapp id.
     * 
     * @param webappId the new webapp id
     */
    public void setWebappId(String webappId)
    {
        this.putValue(REPO_WEBAPPID, webappId);
    }
    
    /**
     * Gets the webapp id.
     * 
     * @return the webapp id
     */
    public String getWebappId()
    {
        return (String) this.getValue(REPO_WEBAPPID);
    }

    @Override
    public String toString()
    {
        return "ModelPersistenceContext-" + userId + "-" + values.toString();
    } 
}
