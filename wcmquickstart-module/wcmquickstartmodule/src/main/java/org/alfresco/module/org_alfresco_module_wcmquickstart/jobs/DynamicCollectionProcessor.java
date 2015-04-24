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

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.WebassetCollectionHelper;
import org.alfresco.repo.admin.RepositoryState;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.repo.lock.JobLockService.JobLockRefreshCallback;
import org.alfresco.repo.lock.LockAcquisitionException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.VmShutdownListener.VmShutdownException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Roy Wetherall
 */
public class DynamicCollectionProcessor implements WebSiteModel
{
    /** Log */
	private static final Log log = LogFactory.getLog(DynamicCollectionProcessor.class);

	/** Query */
	private static final String QUERY = "+ TYPE:\"ws:webassetCollection\" + @ws\\:isDynamic:true";
	
    /** The time this lock will persist in the database (60 sec but refreshed at regular intervals) */
    private static final long LOCK_TTL = 60000L;

    /** The name of the lock used to ensure that feedback processor does not run on more than one node at the same time */
    private static final QName LOCK_QNAME = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "org.alfresco.module.org_alfresco_module_wcmquickstart.jobs.DynamicCollectionProcessor");

	/** Transaction service */
    private TransactionService transactionService;
    
    /** Node service */
    private NodeService nodeService;
    
    /** Search service */
    private SearchService searchService;
    
    /** Webasset Collection Helper */
    private WebassetCollectionHelper collectionHelper;
    
    /** Repository State */
    private RepositoryState repositoryState;
    
    /** Job Lock service **/
    private JobLockService jobLockService;
    
    /**
     * Sets the job lock service
     * @param jobLockService   service for managing job locks
     */
    public void setJobLockService(JobLockService jobLockService)
    {
        this.jobLockService = jobLockService;
    }
    
    /**
     * Sets the repository state
     * @param repositoryState   repository state
     */
    public void setRepositoryState(RepositoryState repositoryState)
    {
        this.repositoryState = repositoryState;
    }
    
    /**
     * Set search service
     * @param searchService search service
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    /**
     * Set node service
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set transaction service
     * @param transactionService    transaction service
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    /**
     * Set collection helper
     * @param collectionHelper  collection helper
     */
    public void setCollectionHelper(WebassetCollectionHelper collectionHelper)
    {
        this.collectionHelper = collectionHelper;
    }


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

    /**
     * Run the processor job.  Refreshing any dynamic queries who's refresh date is before today.
     */
    public void run()
    {
        if (repositoryState.isBootstrapping())
        {
            if (log.isDebugEnabled())
            {
                log.debug("DynamicCollection processor can not be executed while the repository is bootstrapping");
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
                    log.trace("Can't get lock : " + LOCK_QNAME);
                }
                return;
            }

            if (log.isDebugEnabled())
            {
                log.trace("Activities dynamicCollection processor started");
            }

            jobLockService.refreshLock(lockToken, LOCK_QNAME, LOCK_TTL, lockCallback);

            runInternal();

            // Done
            if (log.isDebugEnabled())
            {
                log.trace("Activities dynamicCollection processor completed");
            }
        }
        catch (LockAcquisitionException e)
        {
            // Job being done by another process
            if (log.isDebugEnabled())
            {
                log.debug("Activities dynamicCollection processor already underway");
            }
        }
        catch (VmShutdownException e)
        {
            // Aborted
            if (log.isDebugEnabled())
            {
                log.debug("Activities dynamicCollection processor aborted");
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
                            rs = searchService.query(
                        				StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
                        				SearchService.LANGUAGE_LUCENE, 
                        				QUERY);
                     
                            if (log.isDebugEnabled())
                            {
                                log.debug("Running dynamic collection refresh processor across " + rs.length() + " dynamic collection nodes");
                            }
                        
                            // Get the current date
                            Calendar now = Calendar.getInstance();
                        
                            // Interate over the dynamic queries 
                            for (NodeRef collection : rs.getNodeRefs())
                            {
                                Date refreshAtDate = (Date)nodeService.getProperty(collection, PROP_REFRESH_AT);
                                Calendar refreshAt = Calendar.getInstance();
                                if (refreshAtDate != null)
                                {
                                    // Convert the date to calendar
                                    refreshAt.setTime(refreshAtDate);
                                }
                                
                                if ((refreshAtDate == null) || now.after(refreshAt))
                                {
                                    if (log.isDebugEnabled() == true)
                                    {
                                        String collectionName = (String)nodeService.getProperty(collection, ContentModel.PROP_NAME);
                                        if (collectionName != null)
                                        {
                                            log.debug("Refreshing dynamic collection " + collectionName);
                                        }
                                    }                                    
                                
                                    // Refresh the collection
                                    collectionHelper.refreshCollection(collection);
                                }
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
}
