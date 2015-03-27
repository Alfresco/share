/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

/**
 * Share Dialogue page object, holds all the methods relevant to Share Dialogue Page
 * 
 * @author Meenal Bhave
 * @since 4.3.0HBF
 */
public class ShareDialogue extends SharePage
{
    private static Log logger = LogFactory.getLog(ShareDialogue.class);

    private static final By SHARE_DIALOGUE_PARENT = By.xpath("//div[@class='hd']/..");
    private static final By CLOSE_BUTTON = By.cssSelector("a.container-close");
    private static final By TITLE_TEXT_UPLOAD_FILE = By.cssSelector("span");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id*='cancel']");
    private static final By SHARE_DIALOGUE_HEADER = By.cssSelector("div.hd");
    // TODO: Fix for localisation
    private static final String cloudSignInDialogueHeader =  "Sign in to Alfresco in the cloud";
    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public ShareDialogue(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ShareDialogue render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException e)
                {
                }
            }
            if (isShareDialogueDisplayed())
            {
                break;
            }
            timer.end();
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ShareDialogue render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ShareDialogue render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Helper method to click on the Close button to return to the original page
     */
    public HtmlPage clickClose()
    {
        try
        {
            drone.findAndWait(CLOSE_BUTTON).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not able find the close button " + e);
        }
        catch (StaleElementReferenceException ser)
        {
            return clickClose();
        }
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Helper method to click on the Cancel button to return to the original page
     */
    public HtmlPage clickCancel()
    {
        WebElement button = drone.findFirstDisplayedElement(CANCEL_BUTTON);
        button.click();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Helper method to get the Dialogue title
     * 
     * @return String
     */
    public String getDialogueTitle()
    {
        String title = "";
        try
        {
            WebElement dialogue = getDialogueHeader();
            title = dialogue.getText();
            if (title.isEmpty())
            {
                WebElement dialogueUploadFile = dialogue.findElement(TITLE_TEXT_UPLOAD_FILE);
                title = dialogueUploadFile.getText();
            }
            return title;
        }
        catch (NoSuchElementException nse)
        {
        }
        return title;
    }

    /**
     * Helper method to return true if Share Dialogue is displayed
     * 
     * @return boolean <tt>true</tt> is Share Dialogue is displayed
     */
    public boolean isShareDialogueDisplayed()
    {
        try
        {
            WebElement dialogue = getDialogue();
            if (dialogue != null && dialogue.isDisplayed())
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Helper method to return Parent WebElement for the Share Dialogue
     * 
     * @return WebElement
     */
    private WebElement getDialogue()
    {
        try
        {
            WebElement shareDialogue = drone.findFirstDisplayedElement(SHARE_DIALOGUE_PARENT);

            return shareDialogue;
        }
        catch (NoSuchElementException nse)
        {
            return null;
        }
    }

    /**
     * Helper method to return right PageName for Share Dialogue displayed
     * 
     * @return String
     */
    public String getShareDialoguePageName()
    {
        String pageName = "";

        try
        {
            WebElement dialogue = getDialogueHeader();
            if (dialogue != null && dialogue.isDisplayed())
            {
                String dialogueID = dialogue.getAttribute("id");
                if (dialogueID.contains("createSite"))
                {
                    pageName = "Create Site Page";
                }
                else if (dialogueID.contains("createFolder"))
                {
                    pageName = "Create Folder Page";
                }
                else if (dialogueID.contains("upload"))
                {
                    pageName = "Upload File Page";
                }
                else if (dialogueID.contains("editDetails"))
                {
                    pageName = "Edit Properties Page";
                }
                else if (dialogueID.contains("taggable-cntrl-picker"))
                {
                    pageName = "Tags Page";
                }
                else if (dialogueID.contains("copyMoveTo"))
                {
                    pageName = "CopyOrMoveContent Page";
                }
                else if (cloudSignInDialogueHeader.equals(dialogue.getText()))                        
                {
                    pageName = "CloudSignin Page";
                }
            }
        }
        catch (NoSuchElementException nse)
        {
        }

        logger.info(pageName);

        return pageName;
    }

    /**
     * Helper method to return WebElement for the Share Dialogue
     * 
     * @return WebElement
     */
    public WebElement getDialogueHeader()
    {
        try
        {
            WebElement shareDialogueHeader = drone.findFirstDisplayedElement(SHARE_DIALOGUE_HEADER);
            return shareDialogueHeader;
        }
        catch (NoSuchElementException nse)
        {
            throw new NoSuchElementException("Unable to find the css ", nse);
        }
    }

}
