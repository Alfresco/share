/*
 * #%L
 * Alfresco Web Framework common libraries
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
package org.alfresco.web.config.forms;

import org.springframework.extensions.config.evaluator.Evaluator;

/**
 * Evaluator to determine whether the condition matches the content
 * model type provided.
 * 
 * @author Gavin Cornwell
 */
public class ModelTypeEvaluator implements Evaluator
{
    /**
     * Determines whether the given condition matches the type provided
     * by the object.
     * 
     * @see org.springframework.extensions.config.evaluator.Evaluator#applies(java.lang.Object,
     *      java.lang.String)
     */
    public boolean applies(Object obj, String condition)
    {
        boolean result = false;

        if (obj instanceof String)
        {
            if (condition.equalsIgnoreCase((String)obj))
            {
                result = true;
            }
        }

        return result;
    }
}