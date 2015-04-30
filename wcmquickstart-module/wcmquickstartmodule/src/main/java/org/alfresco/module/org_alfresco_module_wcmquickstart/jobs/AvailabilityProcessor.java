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

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.admin.RepositoryState;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.repo.lock.JobLockService.JobLockRefreshCallback;
import org.alfresco.repo.lock.LockAcquisitionException;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.VmShutdownListener.VmShutdownException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is designed to be run at midnight. It finds any web assets that are either becoming available today or expiring today
 * and sets their "published" flag as appropriate
 * @author Brian
 *
 */
public class AvailabilityProcessor
{
    private static final Log log = LogFactory.getLog(AvailabilityProcessor.class);
    
    private RetryingTransactionHelper txHelper;
    private SearchService searchService;
    private NodeService nodeService;
    private BehaviourFilter behaviourFilter;
    private RepositoryState repositoryState;
    private JobLockService jobLockService;

    private static final long LOCK_TTL = 60000L;
    private static final QName LOCK_QNAME = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "org.alfresco.module.org_alfresco_module_wcmquickstart.jobs.AvailabilityProcessor");
    
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
                log.debug("Availability processor can not be executed while the repository is bootstrapping");
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
                log.trace("Activities availability processor started");
            }

            jobLockService.refreshLock(lockToken, LOCK_QNAME, LOCK_TTL, lockCallback);

            runInternal();

            // Done
            if (log.isDebugEnabled())
            {
                log.trace("Activities availability processor completed");
            }
        }
        catch (LockAcquisitionException e)
        {
            // Job being done by another process
            if (log.isDebugEnabled())
            {
                log.debug("Activities availability processor already underway");
            }
        }
        catch (VmShutdownException e)
        {
            // Aborted
            if (log.isDebugEnabled())
            {
                log.debug("Activities availability processor aborted");
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
        txHelper.doInTransaction(new RetryingTransactionCallback<Object>()
        {
            @Override
            public Object execute() throws Throwable
            {
                return AuthenticationUtil.runAs(new RunAsWork<Object>()
                {
                    @Override
                    public Object doWork() throws Exception
                    {
                        behaviourFilter.disableBehaviour(ContentModel.ASPECT_AUDITABLE);
                        ResultSet rs = null;
                        try
                        {
                            //Find all web assets that are due to become available today
                            rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
                                    SearchService.LANGUAGE_LUCENE, "+@ws\\:availableFromDate:today +@ws\\:published:\"false\"");
                         
                            if (log.isDebugEnabled())
                            {
                                log.debug("Number of assets found that are due to become available: " + rs.length());
                            }
                            for (ResultSetRow row : rs)
                            {
                                nodeService.setProperty(row.getNodeRef(), WebSiteModel.PROP_AVAILABLE, Boolean.TRUE);
                            }
                        }
                        finally
                        {
                            if (rs != null) {rs.close();}
                        }
                        rs = null;

                        try
                        {
                            //Find all web assets that are due to expire today
                            rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
                                    SearchService.LANGUAGE_LUCENE, "+@ws\\:availableToDate:today +@ws\\:published:\"true\"");
                         
                            if (log.isDebugEnabled())
                            {
                                log.debug("Number of assets found that are due to expire: " + rs.length());
                            }
                            for (ResultSetRow row : rs)
                            {
                                nodeService.setProperty(row.getNodeRef(), WebSiteModel.PROP_AVAILABLE, Boolean.FALSE);
                            }
                        }
                        finally
                        {
                            behaviourFilter.enableBehaviour(ContentModel.ASPECT_AUDITABLE);
                            rs.close();
                        }
                        return null;
                    }
                }, AuthenticationUtil.SYSTEM_USER_NAME);
            }
        });
    }
    
    public void setTxHelper(RetryingTransactionHelper txHelper)
    {
        this.txHelper = txHelper;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
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
