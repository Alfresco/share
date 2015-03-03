/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.web.scripts;

import org.alfresco.web.evaluator.Evaluator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

/**
 * @author: mikeh
 */
public class ActionEvaluatorHelper extends BaseProcessorExtension implements ApplicationContextAware
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
     * Returns an evaluator given it's bean reference
     * 
     * @param evaluatorName     Bean reference of evaluator
     * @return Evaluator
     */
    public Evaluator getEvaluator(String evaluatorName)
    {
        try
        {
            Evaluator evaluator = (Evaluator) applicationContext.getBean(evaluatorName);
            if (evaluator instanceof Evaluator == false)
            {
                logger.warn("Bean with id '" + evaluatorName + "' does not implement Evaluator interface.");
                return null;
            }
            return evaluator;
        }
        catch (Exception e)
        {
            logger.warn("Evaluator '" + evaluatorName + "' not found.");
        }
        return null;
    }
}
