package org.alfresco.po.share;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * @author Olga Antonik
 */
public class AddGroupForm extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final By GROUP_FINDER_INPUT = By.cssSelector("input[id*='search-groupfinder-search-text']");
    private static final By GROUP_SEARCH_BUTTON = By.cssSelector("button[id*='search-groupfinder-group-search-button-button']");
    private static final By ADD_GROUP_FORM = By.cssSelector("div[id*='grouppicker_c']");
    private static final String ADD_BUTTON = "//tbody[@class='yui-dt-data']/tr//h3[text()='%s']/../../..//button";

    /**
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public AddGroupForm render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(GROUP_FINDER_INPUT), getVisibleRenderElement(GROUP_SEARCH_BUTTON));
        return this;
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public AddGroupForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify that Add Group form is displayed
     * 
     * @return boolean true if Add Group form dispalyed on the page
     */
    private boolean isAddGroupFormDisplayed()
    {
        try
        {
            return driver.findElement(ADD_GROUP_FORM).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Add Group form is not displayed");
        }
        return false;
    }

    /**
     * Search group
     * 
     * @param group String
     * @return AddGroupForm
     */
    
    AddGroupForm addGroupForm;
    private AddGroupForm searchGroup(String group)
    {
        WebElement searchField = findAndWait(GROUP_FINDER_INPUT);
        searchField.sendKeys(group);
        findAndWait(GROUP_SEARCH_BUTTON).click();

        return addGroupForm.render();
    }
    GroupsPage groupsPage;
    /**
     * Click to the Add button
     * 
     * @param groupName String
     * @return GroupsPage
     */
    private GroupsPage clickAddButton(String groupName)
    {

        try
        {
            WebElement addButton = findAndWait(By.xpath(String.format(ADD_BUTTON, groupName)));
            addButton.click();

        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Not found Element: Add Group", e);
        }

        return groupsPage.render();
    }

    /**
     * Search and add group to the another group
     * 
     * @param groupName String
     * @return GroupsPage
     */
    public GroupsPage addGroup(String groupName)
    {
        if (isAddGroupFormDisplayed())
        {
            checkNotNull(groupName);
            AddGroupForm addGroupForm = searchGroup(groupName).render();
            return addGroupForm.clickAddButton(groupName);

        }
        else
            return groupsPage.render();
    }

}
