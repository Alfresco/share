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

package org.springframework.extensions.surf.cache;

/**
 * Interface that describes basic methods for working with a cache
 * of content objects.  
 * 
 * @author muzquiano
 */
public interface ContentCache<K>
{
    /**
     * Gets content stored in the cache 
     * 
     * @param key the key
     * 
     * @return the object
     */
    public K get(String key);

    /**
     * Places content into the cache (with default timeout)
     * 
     * @param key the key
     * @param obj the obj
     */
    public void put(String key, K obj);
    
    /**
     * Places content into the cache
     * 
     * @param key the key
     * @param obj the obj
     * @param timeout the timeout in milliseconds
     */
    public void put(String key, K obj, long timeout);
    
    /**
     * Removes a content object from the cache.
     * 
     * @param key the key
     */
    public void remove(String key);

    /**
     * Invalidates the cache
     */
    public void invalidate();
}
