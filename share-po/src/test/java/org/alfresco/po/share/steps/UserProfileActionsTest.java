package org.alfresco.po.share.steps;

import java.io.File;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.exception.UnexpectedSharePageException;
import org.alfresco.po.share.user.TrashCanValues;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.exception.PageOperationException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserProfileActionsTest extends AbstractTest
{
    private SiteActions siteActions = new SiteActions();
    private UserProfileActions userActions = new UserProfileActions();
    private String siteName;
    private File file;

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        file = SiteUtil.prepareFile();;
        
        loginAs("admin", "admin");
        
        siteActions.createSite(drone, siteName, siteName, "Public");
        
        siteActions.openDocumentLibrary(drone);
        
        siteActions.uploadFile(drone, file);
        
        siteActions.deleteContentInDocLib(drone, file.getName());
    }
    
    @Test(priority=1)
    public void testNavigateToTrashCan() throws Exception
    {
        try
        {
            // Without navigating to MyProfile page, this action should return UnexpectedSharePageException
            userActions.navigateToTrashCan(drone);
        }
        catch(UnexpectedSharePageException e)
        {
            Assert.assertTrue(e.getMessage().contains("TrashCanPage"));
        }
    }
    
    @Test(priority=2, expectedExceptions=PageOperationException.class)
    public void testDeleteFromTrashCanNoFile() throws Exception
    {
        String fileName = "file" + System.currentTimeMillis();
        userActions.navigateToTrashCan(drone);
        userActions.deleteFromTrashCan(drone, TrashCanValues.FILE, fileName, "documentLibrary");
    }

    
    @Test(priority=3)
    public void testDeleteFromTrashCanFileFound() throws Exception
    {
        userActions.navigateToTrashCan(drone);
        userActions.deleteFromTrashCan(drone, TrashCanValues.FILE, file.getName(), "documentLibrary");
    }
        
}
