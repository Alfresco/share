package org.alfresco.po.alfresco.webdav;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class AdvancedWebDavPageTest extends AbstractTest
{
    WebDavPage webDavPage;

    @BeforeClass
    public void setup() throws Exception
    {
        loginAs(username, password).render();
        webDavPage = ShareUtil.navigateToWebDav(drone, username, password).render();

    }

    @Test(groups = "Enterprise-only", alwaysRun = true)
    public void getDirectoryTextTest() throws Exception
    {
        Assert.assertTrue(webDavPage.getDirectoryText().equals("Directory listing for /"), "Webdav page isn't opened");
    }

    @Test(dependsOnMethods = "getDirectoryTextTest", groups = "Enterprise-only", alwaysRun = true)
    public void checkDirectoryDisplayedTest() throws Exception
    {
        Assert.assertTrue(webDavPage.checkDirectoryDisplayed("Sites"), "Directory 'Sites' isn't displayed");
    }

    @Test(dependsOnMethods = "checkDirectoryDisplayedTest", groups = "Enterprise-only", alwaysRun = true)
    public void clickDirectoryTest() throws Exception
    {
        webDavPage.clickDirectory("Sites");
        Assert.assertTrue(webDavPage.checkUpToLevelDisplayed(), "Link 'Up to level' isn't displayed");
    }

    @Test(dependsOnMethods = "clickDirectoryTest", groups = "Enterprise-only", alwaysRun = true)
    public void checkUpToLevelDisplayedTest() throws Exception
    {
        Assert.assertTrue(webDavPage.checkUpToLevelDisplayed(), "Link 'Up to level' isn't displayed");

    }

    @Test(dependsOnMethods = "checkUpToLevelDisplayedTest", groups = "Enterprise-only", alwaysRun = true)
    public void clickUpToLevelTest() throws Exception
    {
        Assert.assertTrue(webDavPage.getDirectoryText().equals("Directory listing for /Sites"), "Current directory isn't 'Sites'");
        webDavPage.clickUpToLevel();
        Assert.assertTrue(webDavPage.getDirectoryText().equals("Directory listing for /"), "Directory 'one level up' isn't opened");
    }

}