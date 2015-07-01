package org.alfresco.po.share.site.calendar;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * @author Sergey Kardash
 */
public class InformationEventForm extends AbstractEventForm
{
    private Log logger = LogFactory.getLog(this.getClass());

    private final static By EDIT_BUTTON = By.cssSelector("button[id$='edit-button-button']");
    private final static By DELETE_BUTTON = By.cssSelector("button[id$='delete-button-button']");
    private final static By TAG = By.xpath("//div[text()='Tags:']/following-sibling::div");
    private final static By WHAT_DETAIL = By.xpath("//div[contains(text(),'What:')]/following-sibling::div");
    private final static By WHERE_DETAIL = By.xpath("//div[contains(text(),'Where:')]/following-sibling::div");
    private final static By DESCRIPTION_DETAIL = By.xpath("//div[contains(text(),'Description:')]/following-sibling::div");
    @RenderWebElement
    private final static By OK_BUTTON = By.cssSelector("button[id$='_defaultContainer-cancel-button-button']");
    @RenderWebElement
    private final static By START_DATE_TIME = By.cssSelector("div[id$='_defaultContainer-startdate']");
    @RenderWebElement
    private final static By END_DATE_TIME = By.cssSelector("div[id$='_defaultContainer-enddate']");
    private final static By RECURRENCE_DETAIL = By.xpath("//div[contains(text(),'Recurrence:')]/following-sibling::div");

    public InformationEventForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public InformationEventForm render(RenderTime timer)
    {

        basicRender(timer);

        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public InformationEventForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public InformationEventForm render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for click on 'Edit' on information event form
     *
     * @return EditEventForm
     */
    public EditEventForm clickOnEditEvent()
    {

        drone.findAndWait(EDIT_BUTTON).click();
        if (logger.isDebugEnabled())
        {
            logger.info("Click edit event button");
        }
        return new EditEventForm(drone);
    }

    /**
     * Method for click on 'Delete' on information event form
     *
     * @return DeleteEventForm
     */
    public DeleteEventForm clickOnDeleteEvent()
    {
        drone.findAndWait(DELETE_BUTTON).click();
        if (logger.isDebugEnabled())
        {
            logger.info("Click delete event button");
        }
        return new DeleteEventForm(drone);

    }

    /**
     * Method to verify whether Edit button is present
     *
     * @return boolean
     */
    public boolean isEditButtonPresent()
    {
        return drone.isElementDisplayed(EDIT_BUTTON);

    }

    /**
     * Method to verify whether Delete button is present
     *
     * @return boolean
     */
    public boolean isDeleteButtonPresent()
    {
        boolean isPresent;
        isPresent = drone.isElementDisplayed(DELETE_BUTTON);
        return isPresent;
    }

    /**
     * Method to retrieve tag added to Calendar Event
     *
     * @return String
     */
    public String getTagName()
    {
        try
        {
            String tagName = drone.findAndWait(TAG).getText();
            if (!tagName.isEmpty())
                return tagName;
            else
                throw new IllegalArgumentException("Cannot find tag");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the tag");
        }
    }

    /**
     * Method to retrieve what Detail added to Calendar Event
     *
     * @return String
     */
    public String getWhatDetail()
    {
        try
        {
            String whatDetail = drone.findAndWait(WHAT_DETAIL).getText();
            if (!whatDetail.isEmpty())
                return whatDetail;
            else
                throw new IllegalArgumentException("Cannot find what Detail");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the what Detail");
        }
    }

    /**
     * Method to retrieve where Detail added to Calendar Event
     *
     * @return String
     */
    public String getWhereDetail()
    {
        try
        {
            String whatDetail = drone.findAndWait(WHERE_DETAIL).getText();
            if (!whatDetail.isEmpty())
                return whatDetail;
            else
                throw new IllegalArgumentException("Cannot find where Detail");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the where Detail");
        }
    }

    /**
     * Method to retrieve description Detail added to Calendar Event
     *
     * @return String
     */
    public String getDescriptionDetail()
    {
        try
        {
            String whatDetail = drone.findAndWait(DESCRIPTION_DETAIL).getText();
            return whatDetail;
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the description Detail");
        }
    }

    /**
     * Method for close information event form
     *
     * @return CalendarPage
     */
    public CalendarPage closeInformationForm()
    {
        drone.findAndWait(OK_BUTTON).click();
        if (logger.isDebugEnabled())
        {
            logger.info("Click ok event button");
        }

        return drone.getCurrentPage().render();
    }

    /**
     * Method to retrieve Start Date Time of event
     *
     * @return String
     * <br/><br/>author Bogdan.Bocancea
     */
    public String getStartDateTime()
    {
        try
        {
            String whatDetail = drone.findAndWait(START_DATE_TIME).getText();
            if (!whatDetail.isEmpty())
                return whatDetail;
            else
                throw new IllegalArgumentException("Cannot find Start Date");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the Start Date");
        }
    }

    /**
     * Method to retrieve End Date Time of event
     *
     * @return String
     * <br/><br/>author Bogdan.Bocancea
     */
    public String getEndDateTime()
    {
        try
        {
            String whatDetail = drone.findAndWait(END_DATE_TIME).getText();
            if (!whatDetail.isEmpty())
                return whatDetail;
            else
                throw new IllegalArgumentException("Cannot find End Date");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the End Date");
        }
    }

    /**
     * Method to verify whether Delete button is enabled
     *
     * @return boolean
     */
    public boolean isDeleteButtonEnabled()
    {
        try
        {
            return drone.find(DELETE_BUTTON).isEnabled();
        }
        catch (NoSuchElementException te)
        {
            return false;
        }
    }

    /**
     * Method to verify whether Delete button is enabled
     *
     * @return boolean
     */
    public boolean isOkButtonEnabled()
    {
        try
        {
            return drone.find(OK_BUTTON).isEnabled();
        }
        catch (NoSuchElementException te)
        {
            return false;
        }
    }

    /**
     * Method to verify whether Recurrence is present
     *
     * @return boolean
     */
    public boolean isRecurrencePresent()
    {
        try
        {
            return drone.find(RECURRENCE_DETAIL).isEnabled();
        }
        catch (NoSuchElementException te)
        {
            return false;
        }
    }

    /**
     * Method to retrieve description Detail added to Calendar Event
     *
     * @return String
     */
    public String getRecurrenceDetail()
    {
        if (isRecurrencePresent())
        {
            try
            {
                String recurrenceDetail = drone.findAndWait(RECURRENCE_DETAIL).getText();
                return recurrenceDetail;
            }
            catch (TimeoutException te)
            {
                throw new ShareException("Unable to retrieve the description Detail");
            }
        }
        else
        {
            return "";
        }
    }

}
