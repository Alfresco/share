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

package org.springframework.extensions.surf.site;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.RequestContext;

/**
 * Utility class that allows for easy invalidation of the cache.
 * 
 * This is used primarly by the CacheServlet which receives calls
 * from the outside world to "refresh" the cache.
 * 
 * It is also invoked from within the scripting layer to force
 * cache refreshes when objects have been changed through scripting.
 * 
 * @author muzquiano
 */
public class CacheUtil
{
    private static Log logger = LogFactory.getLog(CacheUtil.class);
    
    /**
     * Invalidate model object service object cache.
     * 
     * @param context the context
     */
    public static void invalidateModelObjectServiceCache(RequestContext context)
    {
        // invalidate the model object service state for this user context
        context.getServiceRegistry().getObjectPersistenceService().invalidateCache();

        logger.info("Invalidated Object Cache");
    }
}
