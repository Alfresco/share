/*
 * #%L
 * Alfresco Share WAR
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.web.scripts;

import org.alfresco.web.evaluator.Evaluator;
import org.alfresco.web.resolver.doclib.DoclistActionGroupResolver;
import org.alfresco.web.resolver.doclib.DoclistDataUrlResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

/**
 * @author ewinlof
 */
public class ResolverHelper extends BaseProcessorExtension implements ApplicationContextAware
{
    private static Log logger = LogFactory.getLog(ActionEvaluatorHelper.class);

    protected ApplicationContext applicationContext = null;

    /*
     * Set ApplicationContext
     *
     * @param applicationContext
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /*
     * Returns a DoclistDataUrlResolver bean given it's bean reference
     *
     * @param resolverName     Bean reference of resolver
     * @return DoclistDataUrlResolver
     */
    public DoclistDataUrlResolver getDoclistDataUrlResolver(String resolverName)
    {
        try
        {
            DoclistDataUrlResolver resolver = (DoclistDataUrlResolver) applicationContext.getBean(resolverName);
            if (resolver == null)
            {
                logger.warn("Bean with id '" + resolverName + "' does not implement DoclistDataUrlResolver interface.");
                return null;
            }
            return resolver;
        }
        catch (Exception e)
        {
            logger.warn("DoclistDataUrlResolver '" + resolverName + "' not found.");
            if (logger.isDebugEnabled())
            {
                logger.debug("Exception when trying to get doclistDataUrlResolver '" + resolverName + "':", e);
            }
        }
        return null;
    }

    /*
     * Returns a DoclistDataUrlResolver bean given it's bean reference
     *
     * @param resolverName     Bean reference of resolver
     * @return DoclistActionGroupResolver
     */
    public DoclistActionGroupResolver getDoclistActionGroupResolver(String resolverName)
    {
        try
        {
            DoclistActionGroupResolver resolver = (DoclistActionGroupResolver) applicationContext.getBean(resolverName);
            if (resolver == null)
            {
                logger.warn("Bean with id '" + resolverName + "' does not implement DoclistActionGroupResolver interface.");
                return null;
            }
            return resolver;
        }
        catch (Exception e)
        {
            logger.warn("DoclistActionGroupResolver '" + resolverName + "' not found.");
        }
        return null;
    }

}
