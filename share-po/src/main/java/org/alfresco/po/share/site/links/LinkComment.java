package org.alfresco.po.share.site.links;

import static com.google.common.base.Preconditions.checkNotNull;

import org.alfresco.po.PageElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author Aliaksei Boole
 */
public class LinkComment extends PageElement
{

    private static Log logger = LogFactory.getLog(LinkComment.class);

    private static final By TEXT = By.xpath(".//div[@class='comment-content']/p");
    private static final By EDIT_BUTTON = By.xpath(".//a[contains(@class,'edit-comment')]");
    private static final By DELETE_BUTTON = By.xpath(".//a[contains(@class,'delete-comment')]");
    private static final By CONFIRM_DELETE_BUTTON = By.xpath("//span[@class='button-group']/span[1]//button");

    public LinkComment(WebElement webElement, WebDriver driver)
    {
        setWrappedElement(webElement);
    }

    private void focusOn()
    {
        mouseOver(getWrappedElement());
    }

    public LinksDetailsPage editComment(String newText)
    {
        checkNotNull(newText);
        focusOn();
        findAndWait(EDIT_BUTTON).click();
        EditCommentLinkForm editCommentLinkForm = new EditCommentLinkForm();
        editCommentLinkForm.insertText(newText);
        editCommentLinkForm.clickSubmit();
        return factoryPage.instantiatePage(driver,LinksDetailsPage.class).waitUntilAlert().render();
    }

    public LinksDetailsPage deleteComment()
    {
        focusOn();
        findAndWait(DELETE_BUTTON).click();
        findAndWait(CONFIRM_DELETE_BUTTON).click();
        return factoryPage.instantiatePage(driver,LinksDetailsPage.class).waitUntilAlert().render();
    }

    public String getText()
    {
        return findAndWait(TEXT).getText();
    }


    public boolean isCorrect()
    {
        try
        {
            focusOn();
            WebElement text = findAndWait(TEXT);
            WebElement editBtn = findAndWait(EDIT_BUTTON);
            WebElement deleteBtn = findAndWait(DELETE_BUTTON);
            boolean isCorrect = editBtn.isDisplayed() && editBtn.isEnabled();
            isCorrect = isCorrect && deleteBtn.isDisplayed() && deleteBtn.isDisplayed();
            isCorrect = isCorrect && text.isDisplayed();
            return isCorrect;
        }
        catch (Exception e)
        {
            logger.error("LinkComment don't correct.");
            return false;
        }
    }


}
