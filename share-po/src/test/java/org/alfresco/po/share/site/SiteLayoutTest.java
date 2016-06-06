package org.alfresco.po.share.site;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SiteLayoutTest 
{

    @Test(groups="unit")
    public void getLayout()
    {
        SiteLayout value = SiteLayout.THREE_COLUMN_WIDE_CENTRE;
        By by = SiteLayout.THREE_COLUMN_WIDE_CENTRE.getLocator();
        Assert.assertNotNull(value);
        Assert.assertNotNull(by);
    }
}
