/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
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

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.TinyMceColourCode;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test site content dashlet page elements.
 * 
 * @author Chiran
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2", "Cloud2" })
public class SiteNoticeDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_NOTICE = "site-notice";
    private static final String expectedHelpBallonMsg = "This dashlet displays a custom message on the dashboard, specified by the site manager";
    // ACE-1883
    // private final String linkContent = "<a target=\"_blank\" title=\"Test\" href=\"https://google.co.uk\" data-mce-href=\"https://google.co.uk\">%s</a>";
    String image_src_content = "src=\"%s\"";
    String image_data_src_content = "data-mce-src=\"%s\"";
    String image_align_content = "align=\"bottom\"";
    String image_height_content = "height=\"%s\"";
    String image_width_content = "width=\"%s\"";
    String image_alt_content = "alt=\"%s\"";
    private SiteNoticeDashlet noticeDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private ConfigureSiteNoticeDialogBoxPage configureSiteNoticeDialog = null;
    private ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = null;
    private String titleAndText = null;
    private String fontAtt = "<span data-mce-style=\"color: #0000ff;\" style=\"color: rgb(0, 0, 255);\">";
    // ACE-1883
    // private final String linkContent = "<a target=\"_blank\" title=\"Test\" href=\"https://google.co.uk\" data-mce-href=\"https://google.co.uk\">%s</a>";
    
    @BeforeTest
    public void prepare()
    {
        siteName = "sitenoticedashlettest" + System.currentTimeMillis();
     }
    
    @BeforeClass
    public void loadFile() throws Exception
    {
        uploadDocument();
        navigateToSiteDashboard();
    }
    
    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.SITE_NOTICE, 1).render();
        noticeDashlet = siteDashBoard.getDashlet(SITE_NOTICE).render();
        Assert.assertNotNull(noticeDashlet);
    }

    @Test(dependsOnMethods="instantiateDashlet")
    public void verifyHelpAndConfigureIcons()
    {
        Assert.assertTrue(noticeDashlet.isHelpIconDisplayed());
        Assert.assertTrue(noticeDashlet.isConfigureIconDisplayed());
    }

    @Test(dependsOnMethods="verifyHelpAndConfigureIcons")
    public void selectHelpIcon() 
    {
        noticeDashlet.clickOnHelpIcon();
        Assert.assertTrue(noticeDashlet.isBalloonDisplayed());

        String actualHelpBallonMsg = noticeDashlet.getHelpBalloonMessage();
        Assert.assertEquals(actualHelpBallonMsg, expectedHelpBallonMsg);
    }

    @Test(dependsOnMethods = "selectHelpIcon")
    public void selectConfigureIcon() 
    {
        noticeDashlet.closeHelpBallon();
        configureSiteNoticeDialog = noticeDashlet.clickOnConfigureIcon().render();
        
        Assert.assertNotNull(configureSiteNoticeDialog);
    }

    @Test(dependsOnMethods="selectConfigureIcon")
    public void configureWithDetailsAndClickOK() 
    {
        titleAndText = siteName + System.currentTimeMillis();
        
        configureSiteNoticeDialog.setTitle(titleAndText);
        configureSiteNoticeDialog.setText(titleAndText);
        siteDashBoard = configureSiteNoticeDialog.clickOnOKButton().render();
        noticeDashlet = siteDashBoard.getDashlet(SITE_NOTICE).render();
        
        Assert.assertEquals(noticeDashlet.getTitle(), titleAndText);
        Assert.assertEquals(noticeDashlet.getContent(), titleAndText); 
    }
    
    @Test(dependsOnMethods="configureWithDetailsAndClickOK")
    public void configureWithDetailsAndClickCancel()
    {
        titleAndText = siteName + System.currentTimeMillis();
        
        configureSiteNoticeDialog = noticeDashlet.clickOnConfigureIcon().render();
        configureSiteNoticeDialog.setTitle(titleAndText);
        configureSiteNoticeDialog.setText(titleAndText);
        configureSiteNoticeDialog.clickOnCancelButton();
        noticeDashlet = siteDashBoard.getDashlet(SITE_NOTICE).render();
        
        Assert.assertNotEquals(noticeDashlet.getTitle(), titleAndText);
        Assert.assertNotEquals(noticeDashlet.getContent(), titleAndText); 
    }
    
    @Test(dependsOnMethods="configureWithDetailsAndClickCancel")
    public void configureWithDetailsAndClickClose()
    {
        titleAndText = siteName + System.currentTimeMillis();
        configureSiteNoticeDialog = noticeDashlet.clickOnConfigureIcon().render();
        configureSiteNoticeDialog.setTitle(titleAndText);
        configureSiteNoticeDialog.setText(titleAndText);
        configureSiteNoticeDialog.clickOnCloseButton();
        noticeDashlet = siteDashBoard.getDashlet(SITE_NOTICE).render();
        
        Assert.assertNotEquals(noticeDashlet.getTitle(), titleAndText);
        Assert.assertNotEquals(noticeDashlet.getContent(), titleAndText); 
    }

    @Test(dependsOnMethods = "configureWithDetailsAndClickClose", groups = {"bug"})
    //TODO ACE-3776
    public void getTextFromEditor()
    {
        configureSiteNoticeDialog = noticeDashlet.clickOnConfigureIcon().render();
        siteNoticeEditor = configureSiteNoticeDialog.getContentTinyMceEditor();
        
        // test text as blue color
        siteNoticeEditor.clickColorCode(TinyMceColourCode.BLUE);
        Assert.assertEquals(titleAndText, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains(fontAtt +titleAndText+"</span>"));
    }
}