/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.web.scripts;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

/**
 * Helper base class to wrap the thread-safe singleton locking pattern used to retrieve single
 * instance values from the repository. Example might be the Data Dictionary (used by all users,
 * different per tenant, only retrieved once) or the Sync Mode status (used by all users,
 * same per tenant, only retrieved once).
 * <p>
 * The implementing class only needs to provide method to perform the remote retrieval of the
 * value and indicate if the value needs to be stored per-tenant or not. 
 * 
 * @author Kevin Roast
 */
public abstract class SingletonValueProcessorExtension<T> extends BaseProcessorExtension
{
    /** Map of store ID to value that has been retrieved for it */
    private final Map<String, T> storeValues = new HashMap<String, T>(); 
    
    /** Lock for access to store values */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    
    /**
     * Thread-safe get of the singleton value.
     * 
     * @return singleton value
     */
    protected final T getSingletonValue()
    {
        return getSingletonValue(false);
    }
    
    /**
     * Thread-safe get of the singleton value. Can be optionally stored per tenant based on user Id.
     * 
     * @param tenant    True to get/store per tenant, false for a single value for all repo instances.
     * 
     * @return singleton value, optionally per tenant.
     */
    protected final T getSingletonValue(final boolean tenant)
    {
        return getSingletonValue(tenant, ThreadLocalRequestContext.getRequestContext().getUserId());
    }
    
    /**
     * Thread-safe get of the singleton value. Can be optionally stored per tenant based on user Id.
     * 
     * @param tenant    True to get/store per tenant, false for a single value for all repo instances.
     * 
     * @return singleton value, optionally per tenant.
     */
    protected final T getSingletonValue(final boolean tenant, final String userId)
    {
        T result;
        
        final String storeId = tenant ? getTenantUserStore(userId) : "";
        
        // NOTE: currently there is a single RRW lock for all values -
        // in a heavily multi-tenant scenario (especially ones with new tenants
        // being created often) the first access of a new tenant dictionary would
        // potentially slow other tenant users access to their dictionary.
        // In this situation a lock per tenant would be preferable.
        this.lock.readLock().lock();
        try
        {
            result = storeValues.get(storeId);
            if (result == null)
            {
                this.lock.readLock().unlock();
                this.lock.writeLock().lock();
                try
                {
                    // check again, as more than one thread could have been waiting on the Write lock 
                    result = storeValues.get(storeId);
                    if (result == null)
                    {
                        // call the retrieve implementation - probably going to do a remote call or similar
                        result = retrieveValue(userId, storeId);
                        
                        // store result against the current store i.e. tenant
                        storeValues.put(storeId, result);
                    }
                }
                catch (ConnectorServiceException cerr)
                {
                    throw new AlfrescoRuntimeException("Unable to retrieve " + getValueName() + " configuration from Alfresco: " + cerr.getMessage());
                }
                catch (Exception err)
                {
                    throw new AlfrescoRuntimeException("Failed during processing of " + getValueName() + " configuration from Alfresco: " + err.getMessage());
                }
                finally
                {
                    this.lock.readLock().lock();
                    this.lock.writeLock().unlock();
                }
            }
        }
        finally
        {
            this.lock.readLock().unlock();
        }
        
        return result;
    }
    
    /**
     * Query the existence of a singleton value for the given userId
     * 
     * @param tenant
     * @param userId
     * 
     * @return true if singleton value exists, false otherwise
     */
    protected final boolean hasSingletonValue(final boolean tenant, final String userId)
    {
        boolean result = false;
        
        final String storeId = tenant ? getTenantUserStore(userId) : "";
        this.lock.readLock().lock();
        try
        {
            result = (storeValues.get(storeId) != null);
        }
        finally
        {
            this.lock.readLock().unlock();
        }
        
        return result;
    }
    
    /**
     * Method for implementing class to provide that retrieve the remote value from the appropriate
     * endpoint and API. This is generally used to execute a REST API and extract a value from a
     * response. The value is then automatically stored by the base class.
     * 
     * @param userId    Current user Id
     * @param storeId   Current store Id if any - may be used if per-tenant values are required
     * 
     * @return value from the remote endpoint
     * 
     * @throws ConnectorServiceException
     */
    protected abstract T retrieveValue(String userId, String storeId) throws ConnectorServiceException;
    
    /**
     * Output value name for informational and error messages to the administrator.
     * 
     * @return output value name
     */
    protected abstract String getValueName();
    
    /**
     * Calculate the tenant store for the given user Id.
     * 
     * @param userId    User Id to process - must be non-null and cannot be Guest.
     * 
     * @return Tenant store for the user or empty string for the default tenant.
     */
    private final String getTenantUserStore(final String userId)
    {
        if (userId == null || AuthenticationUtil.isGuest(userId))
        {
            throw new AlfrescoRuntimeException("User ID must exist and cannot be guest.");
        }
        String storeId = "";            // default domain
        int idx = userId.indexOf('@');
        if (idx != -1)
        {
            // assume MT so partition by user domain
            storeId = userId.substring(idx);
        }
        return storeId;
    }
}