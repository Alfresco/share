package org.alfresco.po.share.site.datalist.items;

import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Page object to reflect Contact list item
 * 
 * @author Marina.Nenadovets
 */
public class ContactListItem extends ShareDialogue
{
    private static Log logger = LogFactory.getLog(ContactListItem.class);

    private static final By SAVE_BTN = By.cssSelector("button[id$='_default-createRow-form-submit-button']");
    private static final By CANCEL_BTN = By.cssSelector("button[id$='_default-createRow-form-cancel-button']");
    private static final By CLOSE_BUTTON = By.cssSelector("a.container-close");
    private static final By FIRST_NAME_FIELD = By.cssSelector("input[id$='createRow_prop_dl_contactFirstName']");
    private static final By LAST_NAME_FIELD = By.cssSelector("input[id$='createRow_prop_dl_contactLastName']");
    private static final By EMAIL_FIELD = By.cssSelector("input[id$='createRow_prop_dl_contactEmail']");
    private static final By COMPANY_FIELD = By.cssSelector("input[id$='createRow_prop_dl_contactJobTitle']");
    private static final By PHONE_OFFICE_FIELD = By.cssSelector("input[id$='createRow_prop_dl_contactPhoneOffice']");
    private static final By PHONE_MOBILE_FIELD = By.cssSelector("input[id$='createRow_prop_dl_contactPhoneMobile']");
    private static final By NOTES_FIELD = By.cssSelector("textarea[id$='createRow_prop_dl_contactNotes']");

    public ContactListItem(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ContactListItem render(RenderTime timer)
    {
        basicRender(timer);
        elementRender(timer, getVisibleRenderElement(SAVE_BTN));
        elementRender(timer, getVisibleRenderElement(NOTES_FIELD));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ContactListItem render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ContactListItem render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Check contact list form that all elements located
     * 
     * @return true if all elements were found, throw exception if any element not found
     */
    public boolean isAllFormFieldsPresented()
    {
        boolean isDisplayed;
        try
        {
            logger.info("Check that all elements presented");

            WebElement saveButton = drone.find(SAVE_BTN);
            WebElement cancelButton = drone.find(CANCEL_BTN);
            WebElement closeButton = drone.find(CLOSE_BUTTON);
            WebElement firstNameField = drone.find(FIRST_NAME_FIELD);
            WebElement lastNameField = drone.find(LAST_NAME_FIELD);
            WebElement emailField = drone.find(EMAIL_FIELD);
            WebElement companyField = drone.find(COMPANY_FIELD);
            WebElement officePhoneField = drone.find(PHONE_OFFICE_FIELD);
            WebElement mobilePhoneField = drone.find(PHONE_MOBILE_FIELD);
            WebElement notesField = drone.find(NOTES_FIELD);
            isDisplayed = saveButton.isDisplayed() && cancelButton.isDisplayed() && closeButton.isDisplayed() && firstNameField.isDisplayed()
                    && lastNameField.isDisplayed() && emailField.isDisplayed() && companyField.isDisplayed() && officePhoneField.isDisplayed()
                    && mobilePhoneField.isDisplayed() && notesField.isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            logger.debug("Unable to locate any element for contact list form");
            throw new ShareException("Unable to locate any element for contact list form");
        }
        if (!isDisplayed)
        {
            throw new ShareException("The operation has timed out");
        }
        return isDisplayed;

    }
}
