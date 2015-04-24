/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_wcmquickstart.jobs;

import java.util.concurrent.atomic.AtomicBoolean;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.publish.PublishService;
import org.alfresco.repo.admin.RepositoryState;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.repo.lock.JobLockService.JobLockRefreshCallback;
import org.alfresco.repo.lock.LockAcquisitionException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.VmShutdownListener.VmShutdownException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is designed to be run periodically. It looks for all web sites in the repository
 * and publishes any nodes that are waiting in each one's publishing queue
 * 
 * @author Brian
 * 
 */
public class PublishQueueProcessor
{
    private static final Log log = LogFactory.getLog(PublishQueueProcessor.class);

    private TransactionService transactionService;
    private SearchService searchService;
    private PublishService publishService;
    private RepositoryState repositoryState;
    private JobLockService jobLockService;

    private static final long LOCK_TTL = 60000L;
    private static final QName LOCK_QNAME = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "org.alfresco.module.org_alfresco_module_wcmquickstart.jobs.PublishQueueProcessor");
    
    private class LockCallback implements JobLockRefreshCallback
    {
        final AtomicBoolean running = new AtomicBoolean(true);

        @Override
        public boolean isActive()
        {
            return running.get();
        }

        @Override
        public void lockReleased()
        {
            running.set(false);
            if (log.isDebugEnabled())
            {
                log.debug("Lock released : " + LOCK_QNAME);
            }
        }
    }

    public void run()
    {
        if (repositoryState.isBootstrapping())
        {
            if (log.isDebugEnabled())
            {
                log.debug("PublishQueue processor can not be executed while the repository is bootstrapping");
            }
            return;
        }

        LockCallback lockCallback = new LockCallback();
        String lockToken = null;
        try
        {
            lockToken = jobLockService.getLock(LOCK_QNAME, LOCK_TTL);
            if (lockToken == null)
            {
                if (log.isTraceEnabled())
                {
                    log.trace("Can't get lock.");
                }
                return;
            }

            if (log.isDebugEnabled())
            {
                log.trace("Activities publishQueue processor started");
            }

            jobLockService.refreshLock(lockToken, LOCK_QNAME, LOCK_TTL, lockCallback);

            runInternal();

            // Done
            if (log.isDebugEnabled())
            {
                log.trace("Activities publishQueue processor completed");
            }
        }
        catch (LockAcquisitionException e)
        {
            // Job being done by another process
            if (log.isDebugEnabled())
            {
                log.debug("Activities publishQueue processor already underway");
            }
        }
        catch (VmShutdownException e)
        {
            // Aborted
            if (log.isDebugEnabled())
            {
                log.debug("Activities publishQueue processor aborted");
            }
        }
        finally
        {
            // The lock will self-release if answer isActive in the negative
            lockCallback.running.set(false);
            if (lockToken != null)
            {
                jobLockService.releaseLock(lockToken, LOCK_QNAME);
            }
        }
    }

    private void runInternal()
    {
        AuthenticationUtil.runAs(new RunAsWork<Object>()
        {
            @Override
            public Object doWork() throws Exception
            {
                transactionService.getRetryingTransactionHelper().doInTransaction(
                        new RetryingTransactionCallback<Object>()
                {
                    @Override
                    public Object execute() throws Throwable
                    {
                    	ResultSet rs = null;
                    	
                    	try
                    	{
                            //Find all web root nodes
                            rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
                                SearchService.LANGUAGE_LUCENE, "TYPE:\"" + WebSiteModel.TYPE_WEB_SITE + "\"");
                     
                            if (log.isDebugEnabled())
                            {
                                log.debug("Running publish queue processor across " + rs.length() + " website nodes");
                            }
                            for (ResultSetRow row : rs)
                            {
                                publishService.publishQueue(row.getNodeRef());
                            }
                    	}
                    	finally
                    	{
                    		if (rs != null) {rs.close();}
                    	}
                        return null;
                    }   
                });
                return null;
            }
        }, AuthenticationUtil.SYSTEM_USER_NAME);
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setPublishService(PublishService publishService)
    {
        this.publishService = publishService;
    }

    public void setRepositoryState(RepositoryState repositoryState)
    {
        this.repositoryState = repositoryState;
    }

    public void setJobLockService(JobLockService jobLockService)
    {
        this.jobLockService = jobLockService;
    }
}
