package org.alfresco.po.share;

import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.WebDriver;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;


@SuppressWarnings("unused")
public class DeleteGroupFromGroupPage extends SharePage
{
    private static final String CONFIRM_MESSAGE = "div[class='yui-module yui-overlay yui-panel' ]>div[class='bd']";
    private static final String DELETE_BUTTON = "button[id*='remove-button']";
    private static final String CANCEL_BUTTON = "div[id*='deletegroupdialog_c'] button[id*='cancel-button']";
    private static Log logger = LogFactory.getLog(CopyOrMoveContentPage.class);

    public enum Action
    {
        Yes, No
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteGroupFromGroupPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteGroupFromGroupPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    /**
     * Confirmation (or not) of deleting any group from Group page
     * 
     * @param groupButton Enum Action
     * @return html page
     */
    public HtmlPage clickButton(Action groupButton)
    {
        switch (groupButton)
        {
            case Yes:
                findAndWait(By.cssSelector(DELETE_BUTTON)).click();
                canResume();
                return factoryPage.instantiatePage(driver, GroupsPage.class);

            case No:
                findAndWait(By.cssSelector(CANCEL_BUTTON)).click();
                canResume();
                return factoryPage.instantiatePage(driver, EditGroupPage.class);

        }
        throw new PageException("Wrong Page");

    }

    /**
     * Get the Title in Delete group pop up window
     * 
     * @return - String
     */
    public String getTitle()
    {
        try
        {
            return findAndWait(By.cssSelector("div[id*='deletegroupdialog_h']")).getText();
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Delete group window isn't pop up", toe);
        }

    }

}
