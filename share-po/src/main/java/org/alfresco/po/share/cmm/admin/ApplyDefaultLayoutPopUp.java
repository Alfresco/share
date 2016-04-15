package org.alfresco.po.share.cmm.admin;

/**
 * Class represents ApplyDefaultLayoutPopUp
 * 
 * @author mbhave
 */

import org.alfresco.po.RenderWebElement;
import org.alfresco.po.share.ShareDialogueAikau;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ApplyDefaultLayoutPopUp extends ShareDialogueAikau
{
    @RenderWebElement @FindBy(css="span#CMM_EDITOR_DEFAULT_LAYOUT_DIALOG_title") WebElement popupTitle;
    @FindBy(css="div.dialog-body") WebElement popupBody;
    
	public WebElement getPopupTitle() 
	{
		return popupTitle;
	}
	public WebElement getPopupBody()
	{
		return popupBody;
	}
    
}
