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

package org.springframework.extensions.surf.persister;


/**
 * Indicates that the persister implementation provides caching.
 * 
 * @author muzquiano
 * @author Kevin Roast
 */
public interface CachedPersister
{
    /**
     * Enables or disables the caching mechanics
     * 
     * @param cache
     */
    public void setCache(boolean cache);
    
    /**
     * Sets the number of seconds to wait between cache checks, -1 for never.
     * 
     * @param cacheCheckDelay
     */
    public void setCacheCheckDelay(int cacheCheckDelay);
    
    /**
     * Sets the maximum size of the underlying cache, -1 for no max size.
     * 
     * @param cacheMaxSize
     */
    public void setCacheMaxSize(int cacheMaxSize);
    
    /**
     * Invalidates the cache
     */
    public void invalidateCache();
}
