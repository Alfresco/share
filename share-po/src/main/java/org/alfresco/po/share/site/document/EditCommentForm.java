package org.alfresco.po.share.site.document;

import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * @author Aliaksei Boole
 */
public class EditCommentForm extends AbstractCommentForm
{
    private final static String FORM_DIV_CSS = "DIV[class='comments-list']>DIV[class='comment-form']";
    private final static By FORM_DIV = By.cssSelector(FORM_DIV_CSS);
    private final static By AVATAR = By.cssSelector(FORM_DIV_CSS + ">img");
    private final static By CANCEL_BUTTON = By.cssSelector(FORM_DIV_CSS + " span[class~='yui-reset-button']>span>button");
    public final static By SUBMIT_BUTTON = By.cssSelector(FORM_DIV_CSS + " span[class~='yui-submit-button']>span>button");

    public EditCommentForm(WebDrone drone)
    {
        super(drone);
    }

    public void clickSaveCommentButton()
    {
        click(SUBMIT_BUTTON);
    }

    public void clickCancelButton()
    {
        click(CANCEL_BUTTON);
    }

    public boolean isDisplay()
    {
        return super.isDisplay(FORM_DIV);
    }

    public boolean isAvatarDisplay()
    {
        return super.isDisplay(AVATAR);
    }

    public boolean isButtonsEnable()
    {
        return super.isButtonsEnable(SUBMIT_BUTTON, CANCEL_BUTTON);
    }
}
