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
package org.alfresco.test.wqs;


public class WqsTestProperty
{
    private String wcmqs;
    //properties that should probably be in share-po
    private String domainFree;
    private String domainPremium;
    private String domainHybrid;
    private String defaultUser;
    private String uniqueTestDataString;
    private String adminUsername;
    private String adminPassword;
    private String maxWaitTime;
    public WqsTestProperty(String wcmqs)
    {
        this.wcmqs = wcmqs;
    }
    public WqsTestProperty(String wcmqs, String domainFree, String domainPremium, String domainHybrid, String defaultUser, String uniqueTestDataString, String adminUsername, String adminPassword)
    {
        this.wcmqs = wcmqs;
        this.domainFree = domainFree;
        this.domainPremium = domainPremium;
        this.domainHybrid = domainHybrid;
        this.defaultUser = defaultUser;
        this.uniqueTestDataString = uniqueTestDataString;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public String getWcmqs()
    {
        return wcmqs;
    }

    public void setWcmqs(String wcmqs)
    {
        this.wcmqs = wcmqs;
    }

    public String getMaxWaitTime()
    {
        return maxWaitTime;
    }

    public void setMaxWaitTime(String maxWaitTime)
    {
        this.maxWaitTime = maxWaitTime;
    }

    public String getDomainFree()
    {
        return domainFree;
    }

    public void setDomainFree(String domainFree)
    {
        this.domainFree = domainFree;
    }

    public String getDomainPremium()
    {
        return domainPremium;
    }

    public void setDomainPremium(String domainPremium)
    {
        this.domainPremium = domainPremium;
    }

    public String getDomainHybrid()
    {
        return domainHybrid;
    }

    public void setDomainHybrid(String domainHybrid)
    {
        this.domainHybrid = domainHybrid;
    }

    public String getDefaultUser()
    {
        return defaultUser;
    }

    public void setDefaultUser(String defaultUser)
    {
        this.defaultUser = defaultUser;
    }

    public String getUniqueTestDataString()
    {
        return uniqueTestDataString;
    }

    public void setUniqueTestDataString(String uniqueTestDataString)
    {
        this.uniqueTestDataString = uniqueTestDataString;
    }

    public String getAdminUsername()
    {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername)
    {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword()
    {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword)
    {
        this.adminPassword = adminPassword;
    }
}
