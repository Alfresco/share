package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.ShareDialogue;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Page object to represent Configure External Blog dialogue
 *
 * @author Marina Nenadovets
 */
@SuppressWarnings("unused")
public class ConfigureBlogPage extends ShareDialogue
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final By TYPE_SELECT_DRPDWN = By.cssSelector("[id$='default-configblog-blogType']");
    private static final By TYPE_SELECT_OPTIONS = By.cssSelector("[id$='default-configblog-blogType'] option");
    private static final By ID = By.cssSelector("[id$='configblog-blogid']");
    private static final By NAME = By.cssSelector("[id$='configblog-title']");
    private static final By DESCRIPTION = By.cssSelector("[id$='configblog-description']");
    private static final By URL = By.cssSelector("[id$='configblog-url']");
    private static final By USER_NAME = By.cssSelector("[id$='configblog-username']");
    private static final By PASSWORD = By.cssSelector("[id$='configblog-password']");
    private static final By OK_BTN = By.cssSelector("[id$='configblog-ok-button']");
    private static final By CANCEL_BTN = By.cssSelector("[id$='configblog-cancel-button']");

    /**
     * Constructor
     *
     * @param drone
     */
    protected ConfigureBlogPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfigureBlogPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(TYPE_SELECT_DRPDWN),
            getVisibleRenderElement(ID),
            getVisibleRenderElement(NAME),
            getVisibleRenderElement(DESCRIPTION),
            getVisibleRenderElement(URL),
            getVisibleRenderElement(USER_NAME),
            getVisibleRenderElement(PASSWORD),
            getVisibleRenderElement(OK_BTN));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ConfigureBlogPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfigureBlogPage render(long time)
    {
        return render(new RenderTime(time));
    }

    public static enum TypeOptions
    {
        EMPTY(0), WORDPRESS(1), TYPEPAD(2);

        private final int numberPosition;

        TypeOptions(int numberPosition)
        {
            this.numberPosition = numberPosition;
        }
    }

    /**
     * Method for selecting the type of the blog
     *
     * @param option
     */
    public void selectTypeOption(ConfigureBlogPage.TypeOptions option)
    {
        WebElement selectElem = drone.findAndWait(TYPE_SELECT_DRPDWN);
        Select selectType = new Select(selectElem);
        selectType.selectByIndex(option.ordinal());
    }

    /**
     * Method for setting input into the fields
     *
     * @param input
     * @param value
     */
    private void setInput(final WebElement input, final String value)
    {
        try
        {
            input.clear();
            input.sendKeys(value);
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to find " + input);
        }
    }

    protected void inputNameField(String name)
    {
        setInput(drone.findAndWait(NAME), name);
    }

    protected void inputDescriptionField(String description)
    {
        setInput(drone.findAndWait(DESCRIPTION), description);
    }

    protected void inputURL(String url)
    {
        setInput(drone.findAndWait(URL), url);
    }

    protected void inputUserName(String userName)
    {
        setInput(drone.findAndWait(USER_NAME), userName);
    }

    protected void inputPassword(String password)
    {
        setInput(drone.findAndWait(PASSWORD), password);
    }

    /**
     * Method for clicking OK button
     *
     * @return Blog page object
     */
    protected void clickOk()
    {
        try
        {
            drone.findAndWait(OK_BTN).click();
            waitUntilAlert();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find " + OK_BTN);
        }
    }
}
