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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.wcm.client.exception.RepositoryUnavailableException;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.bindings.spi.AbstractAuthenticationProvider;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;

/**
 * GuestSessionFactoryImpl implements a PoolableObjectFactory for use with an
 * apache commons GenericObjectPool. The class creates and destroys CMIS
 * sessions. It uses a thread which periodically tries to reach the repository.
 * This allows for the repository not being available at application start-up
 * without re-trying on every request.
 * 
 * @author Chris Lack
 */
public class GuestSessionFactoryImpl implements PoolableObjectFactory, Runnable
{
    private final static Log log = LogFactory.getLog(GuestSessionFactoryImpl.class);
    private int repositoryPollInterval;
    private Repository repository;
    private SessionFactoryImpl sessionFactory;
    private Map<String, String> parameters;
    private volatile Thread waitForRepository;
    private Exception lastException;
    private AbstractAuthenticationProvider authenticationProvider;
    private String repoUrl;
    private String username;
    private String password;

    /**
     * Create a CMIS session factory.
     * 
     * @param repo
     *            CMIS repository URL
     */
    public GuestSessionFactoryImpl()
    {
    }

    public void setRepositoryPollInterval(int repositoryPollInterval)
    {
        this.repositoryPollInterval = repositoryPollInterval;
    }

    public void setAuthenticationProvider(AbstractAuthenticationProvider authenticationProvider)
    {
        this.authenticationProvider = authenticationProvider;
    }

    public void setRepoUrl(String repoUrl)
    {
        this.repoUrl = repoUrl;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Create a CMIS session factory.
     * 
     * @param repo
     *            CMIS repository URL
     * @param user
     *            CMIS user
     * @param password
     *            CMIS password
     * @param repositoryPollInterval
     *            Optional. If > 0 a thread polls for the repository, otherwise
     *            the constructor will just issue an exception if the repository
     *            is not available.
     */
    public void init()
    {
        if (repositoryPollInterval > 0)
        {
            // Start thread which gets repository object
            this.waitForRepository = new Thread(this);
            waitForRepository.start();
        }
        else
        {
            // If no poll interval then just check in the current thread and
            // throw exception if not available
            getRepository();
        }
    }

    private void configureSessionFactory()
    {
        if (sessionFactory == null)
        {
            this.parameters = new HashMap<String, String>();

            // user credentials
            parameters.put(SessionParameter.USER, username);
            parameters.put(SessionParameter.PASSWORD, password);

            // connection settings
            parameters.put(SessionParameter.ATOMPUB_URL, repoUrl);
            parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

            // Create session factory
            this.sessionFactory = (SessionFactoryImpl) SessionFactoryImpl.newInstance();
        }
    }
    
    private void getRepository()
    {
        configureSessionFactory();
        List<Repository> repositories = sessionFactory.getRepositories(parameters, null, authenticationProvider, null);
        this.repository = repositories.get(0);
    }

    @Override
    public void run()
    {
        Thread thisThread = Thread.currentThread();
        while (waitForRepository == thisThread)
        {
            // See if the repository can be reached
            try
            {
                getRepository();
                log.info("Repository available");
                break;
            } 
            catch (Exception e)
            {
                lastException = e;
                log.warn("WQS unable to connect to repository: " + e.getMessage());
                if (log.isDebugEnabled())
                {
                    log.debug("Caught exception while attempting to connect to repository over CMIS", e);
                }
            }

            // Wait a bit
            try
            {
                Thread.sleep(repositoryPollInterval);
            } 
            catch (InterruptedException e)
            {
            }
        }
        waitForRepository = null;
    }

    public void stop()
    {
        waitForRepository = null;
    }

    /**
     * @see org.apache.commons.pool.PoolableObjectFactory#activateObject(Object)
     */
    @Override
    public void activateObject(Object obj) throws Exception
    {
    }

    /**
     * @see org.apache.commons.pool.PoolableObjectFactory#destroyObject(Object)
     */
    @Override
    public void destroyObject(Object obj) throws Exception
    {
    }

    /**
     * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
     */
    @Override
    public Object makeObject() throws Exception
    {
        if (repository == null)
        {
            throw new RepositoryUnavailableException(lastException);
        }
        return repository.createSession();
    }

    /**
     * @see org.apache.commons.pool.PoolableObjectFactory#passivateObject(Object)
     */
    @Override
    public void passivateObject(Object obj) throws Exception
    {
    }

    /**
     * @see org.apache.commons.pool.PoolableObjectFactory#validateObject(Object)
     */
    @Override
    public boolean validateObject(Object obj)
    {
        return true;
    }

}
