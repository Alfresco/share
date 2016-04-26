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
