package org.alfresco.po.share;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SharePropertiesTest 
{
    @Test
    public void createProperties()
    {
        ShareProperties prop = new ShareProperties();
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop.getVersion(), AlfrescoVersion.Share);
        Assert.assertEquals(prop.getLocale().toString(), "en");
        Assert.assertEquals(prop.getElement("id.login.button"), "page_x002e_components_x002e_slingshot-login_x0023_default-submit-button");
    }
    @Test
    public void createPropertiesWithFrenchLocale()
    {
        ShareProperties prop = new ShareProperties("Enterprise41","fr");
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop.getVersion(), AlfrescoVersion.Enterprise41);
        Assert.assertEquals(prop.getLocale().toString(), "fr");
        Assert.assertEquals(prop.getElement("id.login.button"), "btn-login");
    }
    @Test
    public void createSharePropertiesVersion41()
    {
        ShareProperties prop = new ShareProperties("Enterprise41");
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop.getVersion(), AlfrescoVersion.Enterprise41);
        Assert.assertEquals(prop.getElement("id.login.button"), "btn-login");
    }
    @Test
    public void createSharePropertiesVersion42()
    {
        ShareProperties prop = new ShareProperties("Enterprise42");
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop.getVersion(), AlfrescoVersion.Enterprise42);
        Assert.assertEquals(prop.getElement("id.login.button"), "page_x002e_components_x002e_slingshot-login_x0023_default-submit-button");
    }
    
    @Test
    public void createSharePropertiesVersion43()
    {
        ShareProperties prop = new ShareProperties("Enterprise43");
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop.getVersion(), AlfrescoVersion.Enterprise43);
        Assert.assertEquals(prop.getElement("id.login.button"), "page_x002e_components_x002e_slingshot-login_x0023_default-submit-button");
    }
    @Test
    public void createSharePropertiesVersion5()
    {
    	ShareProperties prop = new ShareProperties("Enterprise5");
    	Assert.assertNotNull(prop);
    	Assert.assertEquals(prop.getVersion(), AlfrescoVersion.Enterprise5);
        Assert.assertEquals(prop.getElement("id.login.button"), "page_x002e_components_x002e_slingshot-login_x0023_default-submit-button");
    }
    
    @Test
    public void createSharePropertiesWithNull()
    {
        ShareProperties prop = new ShareProperties(null);
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop.getVersion(), AlfrescoVersion.Share);
    }
    @Test
    public void createSharePropertiesWithNull2()
    {
        ShareProperties prop = new ShareProperties("",null);
        Assert.assertEquals(prop.getVersion(), AlfrescoVersion.Share);
    }
    @Test
    public void createSharePropertiesWithBlanks()
    {
        ShareProperties prop = new ShareProperties("","");
        Assert.assertEquals(prop.getVersion(), AlfrescoVersion.Share);

    }
}
