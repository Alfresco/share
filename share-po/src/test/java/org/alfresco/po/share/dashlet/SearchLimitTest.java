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
package org.alfresco.po.share.dashlet;

import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit test for SearchLimit enum
 * 
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class SearchLimitTest extends AbstractSiteDashletTest
{

    @Test
    public void getSearchLimit()
    {
        Assert.assertEquals(SearchLimit.TEN, SearchLimit.getSearchLimit(10));
        Assert.assertEquals(SearchLimit.TWENTY_FIVE, SearchLimit.getSearchLimit(25));
        Assert.assertEquals(SearchLimit.FIFTY, SearchLimit.getSearchLimit(50));
        Assert.assertEquals(SearchLimit.HUNDRED, SearchLimit.getSearchLimit(100));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void getSearchLimitWithException()
    {
        SearchLimit.getSearchLimit(2);
    }

}
