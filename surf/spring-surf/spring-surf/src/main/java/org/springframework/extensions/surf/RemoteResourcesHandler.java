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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.SearchPath;
import org.springframework.extensions.webscripts.Store;

public class RemoteResourcesHandler
{
    private static final Log logger = LogFactory.getLog(RemoteResourcesHandler.class);
    
    /**
     * <p>This a reference to the {@link SearchPath} that identifies the {@link Store} objects to search through. This
     * should be configured via the Spring bean application context.</p>
     */
    private SearchPath searchPath;
    
    /**
     * <p>This defines a list of the acceptable path filters that can be searched on. Paths are filtered to prevent
     * unnecessary remote requests being made. Although the {@link RemoteResourcesHandler} is searched first the requests
     * it is allowed to make needs to be carefully managed to avoid negative performance impacts.</p>
     */
    private List<String> filters;
    
    /**
     * <p>Attempts to retrieve the supplied path from a remote resource. A remote request will only be made if the path
     * starts with one of the filters defined. This ensures that unnecessary requests are not made that will negatively
     * impact performance.</p>
     * 
     * @param path The path to find.
     * @return An {@link InputStream} to the requested path or <code>null</code> if it cannot be found.
     */
    public InputStream getRemoteResource(String path)
    {
        InputStream in = null;
        if (this.filters != null)
        {
            for (String filter: this.filters)
            {
                if (path.startsWith(filter))
                {
                    for (Store store : this.searchPath.getStores())
                    {
                        try
                        {
                            in = store.getDocument(processPath(path));
                            break;
                        }
                        catch (IOException e)
                        {
                            // This log is commented out to prevent verbose output...
                            //logger.error("Error occurred obtaining remote resource: '" + path + "'", e);
                        }
                    }
                }
            }
        }
        return in;
    }

    /**
     * By default this method simply returns the path provided. The method is provided for extensions
     * to have the opportunity to manipulate the supplied path before any resource resolving is attempted.
     * 
     * @return
     */
    protected String processPath(String path)
    {
        return path;
    }
    
    public SearchPath getSearchPath()
    {
        return searchPath;
    }

    public void setSearchPath(SearchPath searchPath)
    {
        this.searchPath = searchPath;
    }

    public List<String> getFilters()
    {
        return filters;
    }

    public void setFilters(List<String> filters)
    {
        this.filters = filters;
    }
}
