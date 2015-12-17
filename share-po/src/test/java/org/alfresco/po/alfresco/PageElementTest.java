package org.alfresco.po.alfresco;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.PageElement;
import org.alfresco.po.share.Navigation;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Michael Suzuki on 30/11/2015.
 */
public class PageElementTest extends AbstractTest
{

    PageElement element;
    @Test
    public void findValidElement()
    {
        driver.get(shareUrl);
        element = factoryPage.instantiatePageElement(driver, Navigation.class);
        Assert.assertNotNull(element);
        boolean visible = element.findAndWait(By.cssSelector("button[id$='_default-submit-button']")).isDisplayed();
        Assert.assertTrue(visible);
    }
    @Test(expectedExceptions = NoSuchElementException.class)
    public void findElementThatsNotThere()
    {
        element = factoryPage.instantiatePageElement(driver, Navigation.class);
        element.findAndWait(By.cssSelector("input.null"));
    }
}
