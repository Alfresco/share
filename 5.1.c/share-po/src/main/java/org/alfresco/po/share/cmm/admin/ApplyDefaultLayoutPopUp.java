/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
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
