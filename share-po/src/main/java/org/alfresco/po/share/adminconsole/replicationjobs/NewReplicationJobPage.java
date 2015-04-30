package org.alfresco.po.share.adminconsole.replicationjobs;

import org.alfresco.po.share.site.document.AbstractEditProperties;
import org.alfresco.po.share.workflow.CompanyHome;
import org.alfresco.po.share.workflow.Content;
import org.alfresco.po.share.workflow.SelectContentPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Class holds elements related to NewReplicationJob page
 *
 * @author Marina.Nenadovets
 */
public class NewReplicationJobPage extends AbstractEditProperties
{
    private static final Logger logger = Logger.getLogger(NewReplicationJobPage.class);
    private final static String SUBMIT_BUTTONS = ".form-buttons .yui-submit-button";
    private final static By NAME_FIELD = By.cssSelector("input[id$='default-prop_name']");
    private final static By DESC_FIELD = By.cssSelector("textarea[id$='default-prop_description']");
    private final static By SOURCE_ITEMS_LIST = By.cssSelector(".object-finder-list");
    private final static By SELECT_BUTTON = By.cssSelector("div[id$='default-payloadContainer'] .show-picker>:first-child button");
    private final static By REMOVE_ALL_BUTTON = By.cssSelector("div[id$='default-payloadContainer'] .show-picker>:last-child button");
    private final static String TRANSFER_TARGET_CONTAINER = "div[id$='transferTargetContainer']";
    private final static By SCHEDULE_CONTAINER = By.cssSelector("div[id$='scheduleContainer']");
    private final static By SCHEDULED_JOB_CHKBOX = By.cssSelector("input[id$='default-scheduleEnabled']");
    private final static By DATE_ENTRY = By.cssSelector(".date-entry");
    private final static By TIME_ENTRY = By.cssSelector(".time-entry");
    private final static By REPEAT_EVERY_FIELD = By.cssSelector("input[name='schedule.intervalCount']");
    private final static String REPEAT_EVERY_DRP_DWN = "div[id$='scheduleContainer'] select";
    private final static By OTHER_OPTIONS_CONTAINER = By.cssSelector(".form-fields>div:last-child");
    private final static By OTHER_OPTIONS_SELECT_DRP_DWN = By.cssSelector(OTHER_OPTIONS_CONTAINER + " option");
    private final static String ENABLED_BOX = "input[id$='default-prop_enabled-entry']";
    private final static By okButton = By.cssSelector("span[id*='payload-cntrl-ok'] , span[id*='targetName-cntrl-ok']");

    private static String DATA_DICTIONARY;
    private static String TRANSFERS;
    private static String TRANSFER_TARGET_GROUP;
    private static String DEFAULT_GROUP;


    public NewReplicationJobPage(WebDrone drone)
    {
        super(drone);
        DATA_DICTIONARY = drone.getValue("system.folder.data.dictionary");
        TRANSFERS = drone.getValue("system.folder.transfers");
        TRANSFER_TARGET_GROUP = drone.getValue("system.folder.transfer.target.groups");
        DEFAULT_GROUP = drone.getValue("system.folder.transfer.default.group");
    }

    public NewReplicationJobPage render(RenderTime renderTime)
    {
        elementRender(renderTime,
            getVisibleRenderElement(By.cssSelector(SUBMIT_BUTTONS)));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewReplicationJobPage render(long l)
    {
        checkArgument(l > 0);
        return render(new RenderTime(l));
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewReplicationJobPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method to set Name field
     *
     * @param name String name input
     */
    public void setName(String name)
    {
        drone.clearAndType(NAME_FIELD, name);
    }

    /**
     * Method to set description field
     *
     * @param description String description input
     */
    public void setDescription(String description)
    {
        drone.clearAndType(DESC_FIELD, description);
    }

    /**
     * Method to select payload (files that will be transfered)
     *
     * @param driver
     * @param companyHome
     * @return NewReplicationJobPage
     */
    public NewReplicationJobPage selectPayLoad(WebDrone driver, CompanyHome companyHome)
    {
        SelectContentPage selectContentPage = clickSelectBtn();
        selectContentPage.addItems(companyHome);
        selectContentPage.selectOKButton();
        logger.info("Payload items were selected");
        return new NewReplicationJobPage(driver);
    }

    private SelectContentPage clickSelectBtn()
    {
        drone.find(SELECT_BTN).click();
        waitUntilAlert();
        return new SelectContentPage(drone).render();
    }

    /**
     * Method to select Transfer target folder
     *
     * @param driver
     * @param transferName
     * @return NewReplicationJobPage
     */
    public NewReplicationJobPage selectTransferTarget(WebDrone driver, String transferName)
    {
        CompanyHome companyHome = createTransferTargetSet(transferName);
        SelectContentPage selectContentPage = clickSelectTransferTarget().render();
        selectContentPage.addItems(companyHome);
        selectContentPage.selectOKButton();
        logger.info("Transfer target was selected");
        return new NewReplicationJobPage(driver);
    }

    /**
     * Method to delete existent transfer target
     * @param driver
     *
     * @return NewReplicationJobPage
     */
    public NewReplicationJobPage deleteTransferTarget(WebDrone driver)
    {
        SelectContentPage selectContentPage = clickSelectTransferTarget().render();
        selectContentPage.removeItem(selectContentPage.getAddedItems().get(0));
        drone.find(By.cssSelector("span[id*='targetName-cntrl-ok']")).click();
        logger.info("Transfer target was deleted");
        return new NewReplicationJobPage(driver);
    }

    /**
     * Method to click Select on Transfer target section
     *
     * @return SelectContentPage
     */
    private SelectContentPage clickSelectTransferTarget()
    {
        drone.find(By.cssSelector(TRANSFER_TARGET_CONTAINER + " button")).click();
        waitUntilAlert();
        return new SelectContentPage(drone);
    }

    /**
     * Method to set Scheduling
     */
    public void setScheduling()
    {
        drone.find(SCHEDULED_JOB_CHKBOX).click();
    }

    /**
     * Method to set due date field
     *
     * @param dueDate
     */
    public void setDueDate(String dueDate)
    {
        String datePattern = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
        Pattern pattern = Pattern.compile(datePattern);
        Matcher matcher = pattern.matcher(dueDate);
        if (StringUtils.isNotEmpty(dueDate))
        {
            if (matcher.matches())
            {
                drone.clearAndType(DATE_ENTRY, dueDate);
            }
            else
            {
                throw new UnsupportedOperationException("Date pattern should match dd/mm/yyyy");
            }
        }
    }

    /**
     * Method to set time field
     *
     * @param time
     */
    public void setTime(String time)
    {
        String timePattern = "([2][0-3]|[0-1][0-9]|[1-9]):([0-5][0-9])";
        Pattern pattern = Pattern.compile(timePattern);
        Matcher matcher = pattern.matcher(time);
        if (StringUtils.isNotEmpty(time))
            if (matcher.matches())
            {
                drone.clearAndType(TIME_ENTRY, time);
            }
            else
            {
                throw new UnsupportedOperationException("Time pattern should match HH:MM");
            }
    }

    /**
     * Method to set repeat every field
     *
     * @param value
     */
    public void setRepeatEveryField(String value)
    {
        drone.clearAndType(REPEAT_EVERY_FIELD, value);
    }

    /**
     * Method to get value from 'Repeat Every' field
     *
     * @return int
     */
    public int getRepeatEveryPeriod()
    {
        return Integer.parseInt(drone.find(REPEAT_EVERY_FIELD).getAttribute("value"));
    }

    /**
     * Method to return interval period
     *
     * @return String
     */
    public String getIntervalPeriod()
    {
        return drone.find(By.cssSelector(REPEAT_EVERY_DRP_DWN + " option[selected='selected']")).getAttribute("value");
    }

    /**
     * MEthod to select Interval period (Seconds, Minutes, Hours etc.)
     *
     * @param intervalValue
     */
    public void selectIntervalPeriod(RepeatEveryValue intervalValue)
    {
        WebElement drpDwn = drone.findAndWait(By.cssSelector(REPEAT_EVERY_DRP_DWN));
        Select select = new Select(drpDwn);
        select.selectByIndex(intervalValue.ordinal()+1);
    }

    /**
     * Method to check Enabled box
     */
    public void setEnabled()
    {
        drone.find(By.cssSelector(ENABLED_BOX)).click();
    }

    public boolean isEnabledSet()
    {
        return drone.find(By.cssSelector("input[id$='default-prop_enabled']")).getAttribute("value").equals("true");
    }

    public NewReplicationJobPage deleteSourceItems(WebDrone driver, String [] itemNames)
    {
        logger.info("Deleting source items");
        SelectContentPage selectContentPage = clickSelectBtn();
        List <String> addedItem = selectContentPage.getAddedItems();
        String [] addedItemsArr;
        addedItemsArr = addedItem.toArray(new String[addedItem.size()]);
        for(int i = 0 ; i < addedItemsArr.length ; i++)
        {
            for(int j = 0; j < itemNames.length; j++)
            {
                if(addedItemsArr[i].equalsIgnoreCase(itemNames[j]))
                {
                    selectContentPage.removeItem(itemNames[j]);
                }
            }
        }
        selectContentPage.selectOKButton();
        logger.info("Source items were deleted");
        return new NewReplicationJobPage(driver).render();
    }

    private CompanyHome createTransferTargetSet(String transferName)
    {
        Content dataDictionary = new Content();
        dataDictionary.setName(DATA_DICTIONARY);
        dataDictionary.setFolder(true);

        Content transfers = new Content();
        transfers.setName(TRANSFERS);
        transfers.setFolder(true);

        Content transferGr = new Content();
        transferGr.setName(TRANSFER_TARGET_GROUP);
        transferGr.setFolder(true);

        Content defaultGroup = new Content();
        defaultGroup.setName(DEFAULT_GROUP);
        defaultGroup.setFolder(true);

        Content transferTarget = new Content();
        transferTarget.setName(transferName);
        transferTarget.setFolder(false);

        Set<Content> transferFolderSet = new HashSet<>();
        transferFolderSet.add(transferTarget);
        defaultGroup.setContents(transferFolderSet);

        Set<Content> defaulGrSet = new HashSet<>();
        defaulGrSet.add(defaultGroup);
        transferGr.setContents(defaulGrSet);

        Set<Content> transferGrSet = new HashSet<>();
        transferGrSet.add(transferGr);
        transfers.setContents(transferGrSet);

        Set<Content> transfersSet = new HashSet<>();
        transfersSet.add(transfers);
        dataDictionary.setContents(transfersSet);

        Set<Content>contentsToAdd = new HashSet<>();
        contentsToAdd.add(dataDictionary);
        CompanyHome companyHomeTR = new CompanyHome();
        companyHomeTR.setContents(contentsToAdd);

        return companyHomeTR;
    }
}
