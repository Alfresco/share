package org.alfresco.po.share.site.document;

import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.workflow.SelectContentPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract of edit properties
 *
 * @author Michael Suzuki
 * @since 1.4
 */
@SuppressWarnings("unchecked")
public abstract class AbstractEditProperties extends ShareDialogue
{
    protected AbstractEditProperties(WebDrone drone)
    {
        super(drone);
    }

    private Log logger = LogFactory.getLog(this.getClass());

    protected static final By INPUT_NAME_SELECTOR = By.cssSelector("input[id$='prop_cm_name']");
    protected static final By INPUT_TITLE_SELECTOR = By.cssSelector("input[id$='prop_cm_title']");
    protected static final By INPUT_DESCRIPTION_SELECTOR = By.cssSelector("textarea[id$='prop_cm_description']");
    protected static final By INPUT_AUTHOR_SELECTOR = By.cssSelector("input[id$='prop_cm_author']");
    protected static final By INPUT_RESOLUTION_UNIT_SELECTOR = By.cssSelector("input[id$='prop_exif_resolutionUnit']");
    protected static final By INPUT_VERTICAL_RESOLUTION_SELECTOR = By.cssSelector("input[id$='_prop_exif_yResolution']");
    protected static final By INPUT_ORIENTATION_SELECTOR = By.cssSelector("input[id$='prop_exif_orientation']");
    protected static final By BUTTON_SELECT_TAG = By.cssSelector("div[id$='cntrl-itemGroupActions']");
    protected static final By CATEGORY_BUTTON_SELECT_TAG = By.cssSelector("div[id$='categories-cntrl-itemGroupActions']");
    protected static final By BUTTON_ALL_PROPERTIES = By.cssSelector("a[id$='editMetadata-button']");
    protected static final By CHECK_BOX_MODEL_ACTIVE = By.cssSelector("input[id$='_modelActive-entry']");
    protected static final By CHECK_BOX_WORKFLOW_DEPLOYED = By.cssSelector("input[id$='_definitionDeployed-entry']");
    protected static final By INPUT_PUBLISHER_SELECTOR = By.cssSelector("input[id$='prop_cm_publisher']");
    protected static final By INPUT_CONTRIBUTOR_SELECTOR = By.cssSelector("input[id$='prop_cm_contributor']");
    protected static final By INPUT_TYPE_SELECTOR = By.cssSelector("input[id$='prop_cm_type']");
    protected static final By INPUT_IDENTIFIER_SELECTOR = By.cssSelector("input[id$='prop_cm_identifier']");
    protected static final By INPUT_SOURCE_SELECTOR = By.cssSelector("input[id$='prop_cm_dcsource']");
    protected static final By INPUT_COVERAGE_SELECTOR = By.cssSelector("input[id$='prop_cm_coverage']");
    protected static final By INPUT_RIGHTS_SELECTOR = By.cssSelector("input[id$='prop_cm_rights']");
    protected static final By INPUT_SUBJECT_SELECTOR = By.cssSelector("input[id$='prop_cm_subject']");
    protected static final By INPUT_ENDPOINT_HOST_SELECTOR = By.cssSelector("input[id$='endpointhost']");
    protected static final By INPUT_ENDPOINT_PORT_SELECTOR = By.cssSelector("input[id$='endpointport']");
    protected static final By SELECT_BTN = By.cssSelector(".show-picker button");
    protected static final By TRANSFER_ENABLED = By.cssSelector("input[id$='enabled-entry']");
    protected static final By USER_NAME = By.cssSelector("input[id$='username']");
    protected static final By PASSWORD = By.cssSelector("input[id$='password']");
    protected static final By INPUT_SITE_CONFIGURATION_SELECTOR = By.cssSelector("textarea[id$='siteConfig']");
    protected static final By INPUT_SITE_HOSTNAME_SELECTOR = By.cssSelector("input[id$='hostName']");
    protected static final By WEB_ASSETS_LIST = By.cssSelector("div[id$='webassets-cntrl-currentValueDisplay']");
    protected static final By REDITION_CONFIG = By.cssSelector("textarea[id$='prop_ws_renditionConfig']");
    protected static final By INPUT_RECORD_LOCATION = By.cssSelector("input[id$='prop_rma_location']");
    protected static final By INPUT_EMAIL_ALIAS = By.cssSelector("input[id$='prop_emailserver_alias']");
    /**
     * Clear the input field and inserts the new value.
     *
     * @param input {@link WebElement} represents the form input
     * @param value String input value to enter
     */
    public void setInput(final WebElement input, final String value)
    {
        input.clear();
        input.sendKeys(value);
    }

    /**
     * Gets the value of the input field
     *
     * @param by input field descriptor
     * @return String input value
     */
    protected String getValue(By by)
    {
        return drone.findAndWait(by).getAttribute("value");
    }

    /**
     * Get the String value of name input value.
     */
    public String getName()
    {
        return getValue(INPUT_NAME_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     *
     * @param name String name input
     */
    public void setName(final String name)
    {
        setInput(drone.findAndWait(INPUT_NAME_SELECTOR), name);
    }

    /**
     * Get value seen on the title input value.
     */
    public String getDocumentTitle()
    {
        return getValue(INPUT_TITLE_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     *
     * @param title String name input
     */
    public void setDocumentTitle(final String title)
    {
        setInput(drone.findAndWait(INPUT_TITLE_SELECTOR), title);
    }

    /**
     * Get value seen on the description input value.
     */
    public String getDescription()
    {
        return getValue(INPUT_DESCRIPTION_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     *
     * @param description String name input
     */
    public void setDescription(final String description)
    {
        setInput(drone.findAndWait(INPUT_DESCRIPTION_SELECTOR), description);
    }

    /**
     * Click on Select button to go to Tag page
     *
     * @return TagPage
     */
    public TagPage getTag()
    {
        WebElement tagElement = drone.findAndWait(BUTTON_SELECT_TAG);
        tagElement.findElement(By.tagName("button")).click();
        return new TagPage(drone);
    }

    /**
     * Click on Select button to go to Category page
     *
     * @return CategoryPage
     */
    public CategoryPage getCategory()
    {
        WebElement tagElement = drone.findAndWait(CATEGORY_BUTTON_SELECT_TAG);
        tagElement.findElement(By.tagName("button")).click();
        return new CategoryPage(drone);
    }

    /**
     * Get the {@link List} of added {@link Categories}.
     *
     * @return {@link List} of {@link Categories}
     * @deprecated Use {@link #getCategoryList()} instead.
     */
    @Deprecated
    public List<Categories> getCategories()
    {
        List<Categories> categories = new ArrayList<Categories>();
        try
        {
            List<WebElement> categoryElements = drone.findAndWaitForElements(By.cssSelector("div[class='itemtype-cm:category']"));
            for (WebElement webElement : categoryElements)
            {
                categories.add(Categories.getCategory(webElement.getText()));
            }
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not able to find categories", e);
        }
        return categories;
    }

    /**
     * Get the {@link List} of added categories.
     *
     * @return {@link List} of categories
     */
    public List<String> getCategoryList()
    {
        List<String> categories = new ArrayList<>();
        try
        {
            List<WebElement> categoryElements = drone.findAndWaitForElements(By.cssSelector("div[class='itemtype-cm:category']"));
            for (WebElement webElement : categoryElements)
            {
                categories.add(webElement.getText());
            }
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not able to find categories", e);
        }
        return categories;
    }

    /**
     * Select cancel button.
     */
    public void clickOnCancel()
    {
        drone.findAndWait(By.cssSelector("button[id$='form-cancel-button']")).click();
    }

    /**
     * Selects the save button that posts the form.
     */
    public void clickSave()
    {
        WebElement saveButton = drone.findAndWait(By.cssSelector("button[id$='form-submit-button']"));
        if (saveButton.isDisplayed())
        {
            String id = saveButton.getAttribute("id");
            saveButton.click();
            waitUntilAlert();
        }
    }

    /**
     * Select all properties button.
     */
    public void clickAllProperties()
    {
        drone.findAndWait(BUTTON_ALL_PROPERTIES).click();
    }

    /**
     * Method to set Model Active check box
     */

    public void setModelActive()
    {
        drone.findAndWait(CHECK_BOX_MODEL_ACTIVE).click();

    }
    
    /**
     * Method to check if Model is Active
     */

    public boolean isModelActive()
    {
        try
        {
            return drone.find(CHECK_BOX_MODEL_ACTIVE).isSelected();
        }
        catch (TimeoutException te)
        {

        }
        return false;
    }

    /**
     * Method to set Workflow Deployed check box
     */

    public void setWorkflowDeployed()
    {
        drone.findAndWait(CHECK_BOX_WORKFLOW_DEPLOYED).click();

    }

    public void setEndpointHost(String endpointHost)
    {
        setInput(drone.findAndWait(INPUT_ENDPOINT_HOST_SELECTOR), endpointHost);
    }

    public void setEndpointPort(String endpointPort)
    {
        setInput(drone.findAndWait(INPUT_ENDPOINT_PORT_SELECTOR), endpointPort);
    }

    public void setUserName(String username)
    {
        drone.clearAndType(USER_NAME, username);
    }

    public void setPassword(String password)
    {
        drone.clearAndType(PASSWORD, password);
    }

    public SelectContentPage clickSelect()
    {
        WebElement selectBtn = drone.findAndWait(SELECT_BTN);
        selectBtn.click();
        return new SelectContentPage(drone);
    }

    public void selectTransferEnabled()
    {
        drone.findAndWait(TRANSFER_ENABLED).click();
    }
    
    /**
     * Enters a value in to the record properties.
     *
     * @param location String location
     */
    public void setLocation(final String location)
    {
        setInput(drone.findAndWait(INPUT_RECORD_LOCATION), location);
    }

    /**
     * Enters a value in to the record properties.
     *
     * @param alias
     */
    public void setEmailAlias(String alias)
    {
        setInput(drone.findAndWait(INPUT_EMAIL_ALIAS), alias);
    }

}
