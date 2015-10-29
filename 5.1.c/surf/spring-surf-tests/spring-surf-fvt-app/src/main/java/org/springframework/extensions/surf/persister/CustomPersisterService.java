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

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigElement.PersisterConfigDescriptor;
import org.springframework.extensions.surf.ModelObjectPersister;
import org.springframework.extensions.surf.persister.CachedPersister;
import org.springframework.extensions.surf.persister.PersisterService;


public class CustomPersisterService extends PersisterService
{
    private ModelObjectPersister persister;

    public void setPersister(ModelObjectPersister persister)
    {
        this.persister = persister;
    }

    private WebFrameworkConfigElement webFrameworkConfig;

    public void setWebFrameworkConfig(WebFrameworkConfigElement webFrameworkConfig)
    {
        this.webFrameworkConfig = webFrameworkConfig;
    }

    /**
     * <p>This overrides the default implementation to just return the <code>ModelObjectPersister</code>
     * configured via the Spring application context.</p>
     */
    public ModelObjectPersister getPersisterById(String persisterId)
    {
        return this.persister;
    }

    /**
     * <p>This overrides the default implementation to just return the <code>ModelObjectPersister</code>
     * configured via the Spring application context.</p>
     */
    public ModelObjectPersister getPersisterByTypeId(String objectTypeId)
    {
        return this.persister;
    }

    public void initPersisters()
    {
        PersisterConfigDescriptor config =  this.webFrameworkConfig.getPersisterConfigDescriptor();
        if (persister instanceof CachedPersister)
        {
            if (config != null)
            {
                // global cache settings
                boolean cache = config.getCacheEnabled();
                int cacheCheckDelay = config.getCacheCheckDelay();

                // set onto persister
                ((CachedPersister)persister).setCache(cache);
                ((CachedPersister)persister).setCacheCheckDelay(cacheCheckDelay);
            }
        }

        // init the persister
        persister.init(null);
    }
}
