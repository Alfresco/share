/*
 * #%L
 * Alfresco CMM Automation QA
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

package org.alfresco.test.properties;

/**
 * Properties used for QA tests.
 * 
 * @author Meenal Bhave
 * @since 1.0
 */
public class QATestSettings
{
    private final String uniqueTestRunName;
    
    private final String adminUsername;

    private final String adminPassword;
    
    private final String defaultUser;

    private final String defaultPassword;

    private final String domainFree;

    private final String domainPremium;

    private final String domainHybrid;

    private int solrRetryCount;

    private long solrWaitTime;

    public QATestSettings(final String uniqueTestRunName, final String adminUsername, final String adminPassword, final String defaultUser, final String defaultPassword,
            final String domainFree, final String domainPremium, final String domainHybrid, final long solrWaitTime, final int solrRetryCount)
    {
        this.uniqueTestRunName = uniqueTestRunName;
        
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;

        this.defaultUser = defaultUser;
        this.defaultPassword = defaultPassword;

        this.domainFree = domainFree;
        this.domainPremium = domainPremium;
        this.domainHybrid = domainHybrid;
        
        this.solrWaitTime = solrWaitTime;
        this.solrRetryCount = solrRetryCount;        
    }
    
    public String getQATestSettings()
    {        
        return "Test Properties set as: Admin Username: " + adminUsername
         + " Admin Password: " + adminPassword 
         + " Default Password: " + defaultPassword
         + " uniqueTestRunName: " + uniqueTestRunName
         + " solr Wait Time: " + solrWaitTime;
    }

    public String getUniqueTestRunName()
    {
        return uniqueTestRunName;
    }
    
    public String getAdminUsername()
    {
        return adminUsername;
    }

    public String getAdminPassword()
    {
        return adminPassword;
    }

    public String getDefaultUser()
    {
        return defaultUser;
    }
    
    public String getDefaultPassword()
    {
        return defaultPassword;
    }

    public String getDomainFree()
    {
        return domainFree;
    }

    public String getDomainPremium()
    {
        return domainPremium;
    }

    public String getDomainHybrid()
    {
        return domainHybrid;
    }

    public int getSolrRetryCount()
    {
        return solrRetryCount;
    }
    
    public long getSolrWaitTime()
    {
        return solrWaitTime;
    }
}