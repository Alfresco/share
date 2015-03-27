package org.alfresco.po.share.admin;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.adminconsole.replicationjobs.ReplicationJobsPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * The Class AdminConsolePage.
 */
@SuppressWarnings("unchecked")
public class AdminConsolePage extends SharePage implements HtmlPage
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

    /**
     * Instantiates a new admin console page.
     * 
     * @param drone WebDriver browser client
     */
    public AdminConsolePage(WebDrone drone)
    {
        super(drone);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(long)
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
     * @see org.alfresco.webdrone.Render#render()
     */
    @Override
    public AdminConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @Override
    public AdminConsolePage render(long maxPageLoadingTime)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Select theme and apply selected one
     * 
     * @param theme
     * @return {@link AdminConsolePage}
     */
    public AdminConsolePage selectTheme(ThemeType theme) throws TimeoutException, InterruptedException
    {
        checkNotNull(theme.text);
        Select themeType = new Select(drone.find(THEME_MENU_SELECT));
        themeType.selectByValue(theme.text);
        clickApplyButton();
        return new AdminConsolePage(drone).render();
    }

    public void clickApplyButton()
    {
        WebElement applay = drone.findAndWait(APPLY_BUTTON);
        applay.click();
        drone.waitForPageLoad(maxPageLoadingTime);

    }

    /**
     * Select that theme was applied
     * Verify Admin Console Page, Application option is selected
     * 
     * @param theme
     * @return boolean
     */
    public boolean isThemeSelected(ThemeType theme)
    {
        AdminConsolePage adminConsolePage = drone.getCurrentPage().render();
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
     * @param filePath
     * @return {@link AdminConsolePage}
     */
    public AdminConsolePage uploadPicture(String filePath)
    {

        try
        {
            UploadFilePage uploadFilePage = new UploadFilePage(drone);
            if (!alfrescoVersion.isFileUploadHtml5())
            {
                setSingleMode();
            }
            WebElement button = drone.findAndWait(FILE_UPLOAD_BUTTON);
            button.click();
            WebElement element = drone.findAndWait(By.cssSelector("#template_x002e_dnd-upload_x002e_console_x0023_default-title-span"));
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

        return new AdminConsolePage(drone).render();
    }

    /**
     * Method to click 'Replication Jobs' link
     *
     * @return ReplicationJobsPage
     */
    public ReplicationJobsPage navigateToReplicationJobs()
    {
        WebElement replicationJobsPage = drone.find(REPLICATION_JOBS_LINK);
        replicationJobsPage.click();
        drone.waitForPageLoad(5);
        logger.info("Navigating to Replication Jobs page");
        return drone.getCurrentPage().render();
    }
}
