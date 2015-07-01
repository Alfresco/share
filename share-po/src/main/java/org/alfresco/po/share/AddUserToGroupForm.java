package org.alfresco.po.share;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author Olga Antonik
 */
public class AddUserToGroupForm extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final By USER_FINDER_INPUT = By.cssSelector("input[id*='search-peoplefinder-search-text']");
    private static final By USER_SEARCH_BUTTON = By.cssSelector("button[id*='search-peoplefinder-search-button-button']");
    private static final By ADD_USER_FORM = By.cssSelector("div[id*='peoplepicker_c']");
    private static final String ADD_BUTTON = "//tbody[@class='yui-dt-data']/tr//a[contains(text(),'%s')]/../../../..//button";
    private static final By CLOSE_X = By.xpath("//div[contains(@id,'-peoplepicker')]/a");

    /**
     * Instantiates a Add User form.
     *
     * @param drone WebDriver browser client
     */
    protected AddUserToGroupForm(WebDrone drone)
    {
        super(drone);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public AddUserToGroupForm render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(USER_FINDER_INPUT), getVisibleRenderElement(USER_SEARCH_BUTTON));
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public AddUserToGroupForm render(long maxPageLoadingTime)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * (non-Javadoc)
     *
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public AddUserToGroupForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Search user
     *
     * @param user String
     * @return AddUserToGroupForm
     */
    public AddUserToGroupForm searchUser(String user)
    {
        try
        {
            WebElement searchField = drone.findAndWait(USER_FINDER_INPUT);
            searchField.clear();
            searchField.sendKeys(user);
            drone.findAndWait(USER_SEARCH_BUTTON).click();

        }
        catch (TimeoutException te)
        {
            logger.error("Failed to find user search input field");
        }
        return new AddUserToGroupForm(drone).render();
    }

    /**
     * Click to the Add button
     *
     * @param user String
     * @return GroupsPage
     */
    private GroupsPage clickAddButton(String user)
    {

        try
        {
            WebElement addButton = drone.findAndWait(By.xpath(String.format(ADD_BUTTON, user)));
            addButton.click();

        }
        catch (TimeoutException e)
        {
            throw new PageException("Not found Element: Add User", e);
        }

        return new GroupsPage(drone).render();
    }

    /**
     * Search and add user to the another group
     *
     * @param user String
     * @return GroupsPage
     */
    public GroupsPage addUser(String user)
    {
        try
        {
            checkNotNull(user);
            drone.findAndWait(ADD_USER_FORM);
            AddUserToGroupForm addUserForm = searchUser(user).render();
            return addUserForm.clickAddButton(user).render();

        }
        catch (TimeoutException e)
        {
            throw new PageException("Add User form is not open", e);
        }

    }

    public void closeForm()
    {
        drone.findAndWait(CLOSE_X).click();
    }

}
