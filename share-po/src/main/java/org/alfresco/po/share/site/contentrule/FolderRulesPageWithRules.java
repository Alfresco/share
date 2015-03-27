package org.alfresco.po.share.site.contentrule;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * FolderRulesPageWithRules page object, holds all element of the HTML page relating to Folder Rule Page
 * if some rule added.
 * 
 * @author Aliaksei Boole
 * @since 1.0
 */
public class FolderRulesPageWithRules extends FolderRulesPage
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final By EDIT_BUTTON = By.cssSelector("button[id*='edit-button-button']");
    private static final By DELETE_BUTTON = By.cssSelector("button[id*='delete-button-button']");
    private static final By NEW_RULE_BUTTON = By.cssSelector("button[id*='default-newRule-button-button']");
    private static final By RULE_DETAILS_BLOCK = By.cssSelector("div[id*='default-body']>div[id*='rule-details']");
    private static final By RULE_ITEMS = By.cssSelector("ul[class*='rules-list-container']>li[class*='rules-list-item']");

    private static final By ALERT_DELETE_BLOCK = By.cssSelector("div[id='prompt']");
    // Delete and Cancel button has same css.
    private static final By ALERT_DELETE_OK = By.xpath("//button[text()='Delete']");

    protected FolderRulesPageWithRules(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FolderRulesPageWithRules render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(TITLE_SELECTOR), getVisibleRenderElement(EDIT_BUTTON), getVisibleRenderElement(DELETE_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FolderRulesPageWithRules render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public FolderRulesPageWithRules render(final long time)
    {
        return render(new RenderTime(time));
    }

    private boolean isRuleDetailsDisplay()
    {
        if (drone.find(RULE_DETAILS_BLOCK).isDisplayed() && drone.find(EDIT_BUTTON).isDisplayed() && drone.find(DELETE_BUTTON).isDisplayed())
        {
            return true;
        }
        return false;
    }

    public <T extends FolderRulesPage> T deleteRule(String ruleName)
    {
        List<WebElement> ruleItems = drone.findAndWaitForElements(RULE_ITEMS);
        for (WebElement ruleItem : ruleItems)
        {
            if (ruleItem.getText().contains(ruleName))
            {
                ruleItem.click();
                render(new RenderTime(maxPageLoadingTime));
                click(DELETE_BUTTON);
                drone.findAndWait(ALERT_DELETE_BLOCK).findElement(ALERT_DELETE_OK).click();
                return drone.getCurrentPage().render();
            }
        }
        throw new PageOperationException("Rule with name:" + ruleName + " not found on Page");
    }

    public CreateRulePage clickNewRuleButton()
    {
        click(NEW_RULE_BUTTON);
        return drone.getCurrentPage().render();
    }

    public CreateRulePage clickEditButton()
    {
        click(EDIT_BUTTON);
        return drone.getCurrentPage().render();
    }

    private void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        drone.mouseOverOnElement(element);
        element.click();
    }

    public boolean isPageCorrect(String folderName)
    {
        return (super.isTitleCorrect(folderName) && isRuleDetailsDisplay());
    }

    // return folder name by inherited rule name
    // Example. If rule was created for folder and rule was applied for subfolder
    // this method return Folder's name by rulename at create rule page for subfolder
    public String getInheritedRulesFolderName(String ruleName)
    {
        if (ruleName == null)
        {
            throw new UnsupportedOperationException("Name of the rule is required");
        }

        try
        {
            String inheritedFolderXpath = String.format("//a[contains(text(),'%s')]/following-sibling::a[@class='inherited-folder']", ruleName);

            return drone.findAndWait(By.xpath(inheritedFolderXpath)).getText();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Not able to find the inherited rule.", e);
        }
        throw new PageException("Not able to find the inherited rule element on this page.");
    }

    /**
     * @param ruleName
     * @return true if ruleName is displayed or return false if rule isn't detected
     */
    public boolean isRuleNameDisplayed(String ruleName)
    {
        List<WebElement> ruleItems = drone.findAndWaitForElements(RULE_ITEMS);
        for (WebElement ruleItem : ruleItems)
        {
            if (ruleItem.getText().contains(ruleName))
            {
                return true;
            }
        }
        return false;
    }
}
