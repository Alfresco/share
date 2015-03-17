package org.alfresco.po.share.adminconsole;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.testng.annotations.Test;

import java.util.Random;
import static org.testng.Assert.*;

/**
 * @author Olga Antonik
 */
public class TagManagerPageTest extends AbstractTest
{

    TagManagerPage tagManagerPage;

    @Test(groups = "Enterprise-only")
    public void checkThatFactoryReturnTagManagerPage() throws Exception
    {
        SharePage page = loginAs("admin", "admin");
        page.getNav().getTagManagerPage().render();
        drone.getCurrentPage().render();
    }

    @Test(dependsOnMethods = "checkThatFactoryReturnTagManagerPage", groups = "Enterprise-only")
    public void checkNoSearchResults() throws Exception
    {
        tagManagerPage = drone.getCurrentPage().render();
        tagManagerPage.fillSearchField(new Random(1000).toString());
        tagManagerPage.clickSearchButton();
        assertFalse(tagManagerPage.isSearchResults());
        assertTrue(drone.isElementDisplayed(tagManagerPage.NO_RESULT));
    }


}
