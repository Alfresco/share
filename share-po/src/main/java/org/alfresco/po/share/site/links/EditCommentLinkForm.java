package org.alfresco.po.share.site.links;

import org.openqa.selenium.By;

/**
 * @author Aliaksei Boole
 */
public class EditCommentLinkForm extends AddCommentLinkForm
{
    private static final By SUBMIT_BTN = By.xpath("//button[text()='Save']");

    @Override
    protected By getSubmitBtnBy()
    {
        return SUBMIT_BTN;
    }
}
