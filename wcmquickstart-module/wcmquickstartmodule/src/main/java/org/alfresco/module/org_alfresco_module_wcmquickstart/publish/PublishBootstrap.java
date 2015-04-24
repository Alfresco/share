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
package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferService2;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * @author Brian Remmington
 */
public class PublishBootstrap implements ApplicationContextAware, 
										   ApplicationListener<ApplicationContextEvent>,
										   WebSiteModel
{
	private Lifecycle lifecycle = new Lifecycle();
	private PublishService publishService;
	private TransferService2 transferService;
	
	public void setPublishService(PublishService publishService)
    {
        this.publishService = publishService;
    }

    public void setTransferService(TransferService2 transferService)
    {
        this.transferService = transferService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
		lifecycle.setApplicationContext(applicationContext);
    }

	@Override
    public void onApplicationEvent(ApplicationContextEvent event)
    {
		lifecycle.onApplicationEvent(event);
    }
	
	private class Lifecycle extends AbstractLifecycleBean
    {
        /**
         * @see org.alfresco.util.AbstractLifecycleBean#onBootstrap(org.springframework.context.ApplicationEvent)
         */
        @Override
        protected void onBootstrap(ApplicationEvent event)
        {
        	AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>()
			{
				@Override
                public Object doWork() throws Exception
                {
			        //Check that a transfer target exists with the configured name, and create it if not
			        //Note that the assumption here is that the transfer service being used has been configured
			        //to transfer within a single repo, and therefore if a new target has to be created then we
			        //are creating a "dummy" one that just has sufficient dummy information to work.
				    String transferTargetName = publishService.getTransferTargetName();
				    try
			        {
			            transferService.getTransferTarget(transferTargetName);
			        }
			        catch (TransferException e) 
			        {
			            transferService.createAndSaveTransferTarget(transferTargetName, transferTargetName, transferTargetName, 
			                    "https", "internal", 443, "", "notused", "notused".toCharArray());
			        }
			        return transferTargetName;
                }
			}, AuthenticationUtil.getSystemUserName());            
        }
    
        /**
         * @see org.alfresco.util.AbstractLifecycleBean#onShutdown(org.springframework.context.ApplicationEvent)
         */
        @Override
        protected void onShutdown(ApplicationEvent event)
        {
            // Intentionally empty
        }
    }
}
