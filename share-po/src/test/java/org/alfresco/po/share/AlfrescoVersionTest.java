/*
 * #%L
 * share-po
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
package org.alfresco.po.share;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups={"unit"})
public class AlfrescoVersionTest
{
    @Test
    public void createFromString()
    {
        AlfrescoVersion enterprise = AlfrescoVersion.fromString("Enterprise");
        Assert.assertEquals(AlfrescoVersion.Enterprise, enterprise);
        AlfrescoVersion enterprise41 = AlfrescoVersion.fromString("Enterprise-41");
        Assert.assertEquals(AlfrescoVersion.Enterprise41, enterprise41);
        AlfrescoVersion enterprise42 = AlfrescoVersion.fromString("Enterprise-42");
        Assert.assertEquals(AlfrescoVersion.Enterprise42, enterprise42);
        AlfrescoVersion enterprise43 = AlfrescoVersion.fromString("Enterprise-43");
        Assert.assertEquals(AlfrescoVersion.Enterprise43, enterprise43);
        AlfrescoVersion enterprise5 = AlfrescoVersion.fromString("Enterprise-5");
        Assert.assertEquals(AlfrescoVersion.Enterprise5, enterprise5);
        AlfrescoVersion cloud = AlfrescoVersion.fromString("Cloud");
        Assert.assertEquals(AlfrescoVersion.Cloud, cloud);
        AlfrescoVersion lowerCasecloud = AlfrescoVersion.fromString("cloud");
        Assert.assertEquals(AlfrescoVersion.Cloud, lowerCasecloud);
        AlfrescoVersion cloud2 = AlfrescoVersion.fromString("CloudNonFacetSearch");
        Assert.assertEquals(AlfrescoVersion.CloudNonFacetSearch, cloud2);
        AlfrescoVersion myAlfresco = AlfrescoVersion.fromString("myalfresco");
        Assert.assertEquals(AlfrescoVersion.MyAlfresco, myAlfresco);
    }
    @Test
    public void isCloud()
    {
        boolean cloud = AlfrescoVersion.Cloud.isCloud();
        boolean cloudNonFacetedSearch = AlfrescoVersion.CloudNonFacetSearch.isCloud();
        boolean myAlfresco = AlfrescoVersion.MyAlfresco.isCloud();
        boolean enterprise41 = AlfrescoVersion.Enterprise41.isCloud();
        boolean enterprise42 = AlfrescoVersion.Enterprise42.isCloud();
        boolean enterprise43 = AlfrescoVersion.Enterprise43.isCloud();
        boolean enterprise5 = AlfrescoVersion.Enterprise5.isCloud();
        Assert.assertEquals(true, cloud);
        Assert.assertEquals(true, cloudNonFacetedSearch);
        Assert.assertEquals(true, myAlfresco);
        Assert.assertEquals(false, enterprise41);
        Assert.assertEquals(false, enterprise42);
        Assert.assertEquals(false, enterprise43);
        Assert.assertEquals(false, enterprise5);
    }
    @Test
    public void isDojoSupported()
    {
        Assert.assertEquals(false, AlfrescoVersion.Enterprise41.isDojoSupported());
        Assert.assertEquals(true, AlfrescoVersion.Enterprise42.isDojoSupported());
        Assert.assertEquals(true, AlfrescoVersion.Enterprise43.isDojoSupported());
        Assert.assertEquals(true, AlfrescoVersion.Enterprise5.isDojoSupported());
        Assert.assertEquals(true, AlfrescoVersion.Cloud.isDojoSupported());
        Assert.assertEquals(true, AlfrescoVersion.CloudNonFacetSearch.isDojoSupported());
        Assert.assertEquals(true, AlfrescoVersion.MyAlfresco.isDojoSupported());
    } 
    @Test
    public void isFacetSearchSupported()
    {
        Assert.assertEquals(false, AlfrescoVersion.Enterprise41.isFacetedSearch());
        Assert.assertEquals(false, AlfrescoVersion.Enterprise42.isFacetedSearch());
        Assert.assertEquals(false, AlfrescoVersion.Enterprise43.isFacetedSearch());
        Assert.assertEquals(true, AlfrescoVersion.Cloud.isFacetedSearch());
        Assert.assertEquals(false, AlfrescoVersion.CloudNonFacetSearch.isFacetedSearch());
        Assert.assertEquals(true, AlfrescoVersion.Enterprise5.isFacetedSearch());
        Assert.assertEquals(true, AlfrescoVersion.MyAlfresco.isFacetedSearch());
    } 
    @Test
    public void createFromNull()
    {
        Assert.assertEquals(AlfrescoVersion.fromString(null), AlfrescoVersion.Share);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createFromInvalidString()
    {
        AlfrescoVersion.fromString("FakeSite");
    }
}
