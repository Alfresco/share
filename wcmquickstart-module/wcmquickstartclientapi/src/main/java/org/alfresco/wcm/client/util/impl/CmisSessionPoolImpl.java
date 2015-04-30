/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
package org.alfresco.wcm.client.util.impl;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.wcm.client.util.CmisSessionPool;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.commons.pool.ObjectPool;

/**
 * Facade for CMIS collection pool implementations
 * 
 * @author Chris Lack
 * @author Brian
 */
public class CmisSessionPoolImpl implements CmisSessionPool
{
    private ObjectPool guestSessionPool;
    private Session session;
    private ReadWriteLock sessionLock = new ReentrantReadWriteLock(true);
    
    private long ticketDuration = 60*60*1000;
    private AtomicLong sessionLastTouched = new AtomicLong(System.currentTimeMillis());

    public CmisSessionPoolImpl(ObjectPool guestSessionPool, long ticketDuration)
    {
        this.guestSessionPool = guestSessionPool;
        this.ticketDuration = ticketDuration;
    }

    /**
     * @see org.alfresco.wcm.client.util.CmisSessionPool#closeSession(Session)
     */
    @Override
    public synchronized void closeSession(Session session) throws Exception
    {
        //Do nothing. CMIS sessions no longer need to be pooled - they are fully thread-safe - so we
        //now permanently "borrow" just one session from the backing pool and provide it to
        //our clients. We never return it to the pool.
    }

    /**
     * @see org.alfresco.wcm.client.util.CmisSessionPool#getGuestSession()
     */
    @Override
    public Session getGuestSession() throws Exception
    {
        sessionLock.readLock().lock();
        
        boolean refreshSession = System.currentTimeMillis() >= sessionLastTouched.get() + ticketDuration;
        
        try
        {
            if (refreshSession || session == null)
            {
                sessionLock.readLock().unlock();
                sessionLock.writeLock().lock();
                try
                {
                    refreshSession = System.currentTimeMillis() >= sessionLastTouched.get() + ticketDuration;
                    if (refreshSession || session == null)
                    {
                        if (session != null)
                        {
                            guestSessionPool.invalidateObject(session);
                        }
                        
                        session = (Session) guestSessionPool.borrowObject();
                    }
                }
                finally
                {
                    sessionLock.readLock().lock();
                    sessionLock.writeLock().unlock();
                }
            }
            sessionLastTouched.set(System.currentTimeMillis());
            return session;
        }
        finally
        {
            sessionLock.readLock().unlock();
        }
    }

    /**
     * @see org.alfresco.wcm.client.util.CmisSessionPool#getSession(String,
     *      String)
     */
    @Override
    public synchronized Session getSession(String username, String password)
    {
        throw new UnsupportedOperationException("Custom authenticated sessions not yet supported by this class");
    }
}
