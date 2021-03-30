/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
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
package org.alfresco.po;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RetryAnalyzer implements IRetryAnalyzer
{
    private static final Log logger = LogFactory.getLog(RetryAnalyzer.class);

    private int retryCount = 0;
    private final int MAX_RETRIES = 2;

    @Override
    public boolean retry(ITestResult result)
    {
        String methodName = result.getMethod().getMethodName();
        if (retryCount < MAX_RETRIES)
        {
            logger.warn("RERUNNING FAILED TEST : " + methodName + " , retry count : " + retryCount);
            retryCount++;
            return true;
        }
        return false;
    }
}
