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
