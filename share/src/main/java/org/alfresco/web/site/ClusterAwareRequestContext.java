/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.site;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.web.site.ClusterAwarePathStoreObjectPersister.ClusterMessage;
import org.alfresco.web.site.ClusterAwarePathStoreObjectPersister.PathInvalidationMessage;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.LinkBuilder;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.support.ServletRequestContext;

/**
 * RequestContext impl responsible for pushing cache invalidation messages to a cluster.
 * <p>
 * This RequestContext maintains a list of paths that have been marked as invalid by a
 * persister. At the end of the page request processing lifecycle, if the list is not empty
 * then it is pushed out to the cluster so each node can update itself.
 * 
 * @see ClusterAwarePathStoreObjectPersister
 * 
 * @author Kevin Roast
 */
public class ClusterAwareRequestContext extends ServletRequestContext
{
    private List<String> invalidCachePaths = new ArrayList<String>();
    private ClusterAwarePathStoreObjectPersister clusterObjectPersister;
    
    public ClusterAwareRequestContext(
            ClusterAwarePathStoreObjectPersister clusterObjectPersister,
            WebFrameworkServiceRegistry serviceRegistry,
            FrameworkBean frameworkBean,
            LinkBuilder linkBuilder)
    {
        super(serviceRegistry, frameworkBean, linkBuilder);
        this.clusterObjectPersister = clusterObjectPersister;
    }
    
    void addInvalidCachePath(final String path)
    {
        this.invalidCachePaths.add(path);
    }
    
    @Override
    public void release()
    {
        try
        {
            if (this.invalidCachePaths.size() != 0)
            {
                ClusterMessage msg = new PathInvalidationMessage(this.invalidCachePaths);
                this.clusterObjectPersister.pushMessage(msg);
            }
        }
        finally
        {
            super.release();
        }
    }
}