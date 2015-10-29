/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.admin;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * The Class AdminConsolePage.
 */
@SuppressWarnings("unchecked")
public class AdminConsolePage extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private final static By APPLY_BUTTON = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-apply-button-button");
    private final static By UPLOAD_BUTTON = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-upload-button-button");
    private final static By RESET_BUTTON = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-reset-button-button");
    private final static By THEME_MENU_SELECT = By.cssSelector("#console-options-theme-menu");
    private final static By APPLICATION_MENU = By.cssSelector(".selected");
    private static final By FILE_UPLOAD_BUTTON = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-upload-button-button");
    public static final By LOGO_PICTURE = By.xpath("//div[@class='logo']/img");
    private final static By REPLICATION_JOBS_LINK = By.cssSelector(".tool a[href='replication-jobs']");
    private static final By CMM_LINK = By.cssSelector("a[href='custom-model-manager']");

    public enum ThemeType
    {
        green("greenTheme", "#d4f8c4", "#00ae42"),
        blue("default", "#dceaf4", "#6ca5ce"),
        light("lightTheme", "#eeeeee", "#333333"),
        yellow("yellowTheme", "#ffee9e", "#f6931c"),
        googleDocs("gdocs", "#dee6ff", "#0600cc"),
        highContrast("hcBlack", "#000000", "#000000");

        public final String text;
        public final String hexColor;
        public final String hexTextColor;

        ThemeType(String text, String hexColor, String hexTextColor)
        {
            this.text = text;
            this.hexColor = hexColor;
            this.hexTextColor = hexTextColor;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.Render#render(long)
     */
    @Override
    public AdminConsolePage render(RenderTime renderTime)
    {
        elementRender(renderTime,
                getVisibleRenderElement(APPLY_BUTTON),
                getVisibleRenderElement(UPLOAD_BUTTON),
                getVisibleRenderElement(RESET_BUTTON),
                getVisibleRenderElement(THEME_MENU_SELECT));
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.Render#render()
     */
    @Override
    public AdminConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Select theme and apply selected one
     * 
     * @param theme ThemeType
     * @return {@link AdminConsolePage}
     */
    public HtmlPage selectTheme(ThemeType theme) throws TimeoutException, InterruptedException
    {
        checkNotNull(theme.text);
        Select themeType = new Select(driver.findElement(THEME_MENU_SELECT));
        themeType.selectByValue(theme.text);
        clickApplyButton();
        return getCurrentPage();
    }

    public void clickApplyButton()
    {
        WebElement applay = findAndWait(APPLY_BUTTON);
        applay.click();
        waitForPageLoad(maxPageLoadingTime);

    }

    /**
     * Select that theme was applied
     * Verify Admin Console Page, Application option is selected
     * 
     * @param theme ThemeType
     * @return boolean
     */
    public boolean isThemeSelected(ThemeType theme)
    {
        AdminConsolePage adminConsolePage = getCurrentPage().render();
        String hex = "";
        try
        {
            hex = adminConsolePage.getColor(APPLICATION_MENU, true);

        }
        catch (StaleElementReferenceException e)
        {
            isThemeSelected(theme);
        }

        return hex.equals(theme.hexColor);
    }

    /**
     * Upload new logo picture
     * admin user is logged in, Application option is selected
     * 
     * @param filePath String
     * @return {@link AdminConsolePage}
     */
    public HtmlPage uploadPicture(String filePath)
    {

        try
        {
            UploadFilePage uploadFilePage = getCurrentPage().render();
            WebElement button = findAndWait(FILE_UPLOAD_BUTTON);
            button.click();
            WebElement element = findAndWait(By.cssSelector("#template_x002e_dnd-upload_x002e_console_x0023_default-title-span"));
            if (element.isDisplayed())
            {
                uploadFilePage.upload(filePath).render();
                clickApplyButton();
            }

        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the Upload button css " + te);
            }
        }

        return getCurrentPage();
    }

    /**
     * Method to click 'Replication Jobs' link
     *
     * @return ReplicationJobsPage
     */
    public HtmlPage navigateToReplicationJobs()
    {
        WebElement replicationJobsPage = driver.findElement(REPLICATION_JOBS_LINK);
        replicationJobsPage.click();
        waitForPageLoad(5);
        logger.info("Navigating to Replication Jobs page");
        return getCurrentPage();
    }
    /**
     * Clicks on Manage Custom Models link.
     * 
     * @return {@link HtmlPage}
     */
    public HtmlPage selectCustomModelManager()
    {
        driver.findElement(CMM_LINK).click();
        return factoryPage.getPage(driver);

    }

    /**
     * Does the current page have a manage-custom-models link in the Left Panel
     * 
     * @return boolean
     */
    public boolean hasManageModelsLink()
    {
        List<WebElement> elements = driver.findElements(CMM_LINK);
        return !elements.isEmpty();
    }
}
