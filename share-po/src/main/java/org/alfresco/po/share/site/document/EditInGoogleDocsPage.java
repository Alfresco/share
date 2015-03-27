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

package org.alfresco.po.share.site.document;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Edit in google docs page, holds all element of the HTML page relating to
 * share's edit document properties page.
 *
 * @author Subashni Prasanna
 * @since 1.5
 */
public class EditInGoogleDocsPage extends SitePage
{
    protected static final By BUTTON_SAVE_TO_ALFRESCO = By.cssSelector("button[id$='default-googledocs-save-button']");
    private static final By BUTTON_DISCARD_CHANGES = By.cssSelector("button[id$='default-googledocs-discard-button']");
    private static final By BUTTON_BACK_TO_SHARE = By.cssSelector("button[id$='default-googledocs-back-button']");
    private static final By GOOGLEDOCS_FRAME = By.tagName("iframe");
    @SuppressWarnings("unused")
    private static final By EDIT_GOOGLE_DOCS = By.cssSelector("span[class$='goog-inline-block kix-lineview-text-block']");
    private static final By GOOGLE_DOC_TITLE = By.cssSelector("div[id$='docs-title-inner']");
    private static final By DOCS_BRANDING_CONTAINER = By.cssSelector("#docs-branding-container");
    private static final By DOCS_EDITOR_CONTAINER = By.cssSelector("#docs-editor-container");

    private final String documentVersion;
    private final boolean editOfflineFlag = false;
    private boolean isGoogleCreate;

    public void setGoogleCreate(boolean isGoogleCreate)
    {
        this.isGoogleCreate = isGoogleCreate;
    }

    /**
     * Constructor.
     */

    public EditInGoogleDocsPage(WebDrone drone, Boolean isGoogleCreate)
    {
        this(drone, null, isGoogleCreate);
    }

    /**
     * Constructor used by SharePageFactory
     *
     * @param drone {@link WebDrone}
     */
    public EditInGoogleDocsPage(WebDrone drone)
    {
        super(drone);
        this.documentVersion = "";
        this.isGoogleCreate = false;
    }

    /**
     * Constructor.
     *
     * @param drone           {@link WebDrone}
     * @param documentVersion String original document version
     */
    protected EditInGoogleDocsPage(WebDrone drone, final String documentVersion, Boolean isGoogleCreate)
    {
        super(drone);
        this.documentVersion = documentVersion;
        this.isGoogleCreate = isGoogleCreate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditInGoogleDocsPage render(RenderTime timer) throws PageRenderTimeException
    {
        basicRender(timer);
        // elementRender(timer, getVisibleRenderElement(BUTTON_SAVE_TO_ALFRESCO), getVisibleRenderElement(BUTTON_DISCARD_CHANGES),
        // getVisibleRenderElement(BUTTON_BACK_TO_SHARE), getVisibleRenderElement(GOOGLEDOCS_FRAME));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditInGoogleDocsPage render(long time) throws PageRenderTimeException
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditInGoogleDocsPage render() throws PageRenderTimeException
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if WebElement Save To Alfresco is visible.
     *
     * @return true if displayed
     */
    public boolean isSaveToAlfrescoVisible()
    {
        try
        {
            return drone.findAndWait(BUTTON_SAVE_TO_ALFRESCO).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Verify if WebElement Discard Changes is visible.
     *
     * @return true if displayed
     */
    public boolean isDiscardChangesVisible()
    {
        try
        {
            return drone.findAndWait(BUTTON_DISCARD_CHANGES).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Verify if WebElement back to Share "<" visible.
     *
     * @return true if displayed
     */
    public boolean isBackToSharevisible()
    {
        try
        {
            return drone.findAndWait(BUTTON_BACK_TO_SHARE).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Verify if WebElement back to Share "<" visible.
     *
     * @return true if displayed
     */
    public boolean isGoogleDocsIframeVisible()
    {
        try
        {
            String frameId = drone.findAndWait(GOOGLEDOCS_FRAME).getAttribute("id");
            if (frameId != null)
            {
                return true;
            }
            else
            {
                throw new NoSuchElementException("the frame is not found");
            }

        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Select Discard Changes button.
     *
     * @return GoogleDocsDiscardChanges
     */
    public GoogleDocsDiscardChanges selectDiscard()
    {
        drone.switchToDefaultContent();
        drone.findAndWait(BUTTON_DISCARD_CHANGES).click();
        return new GoogleDocsDiscardChanges(drone, isGoogleCreate);
    }

    /**
     * Selects the save to Alfresco button that triggers the form submit.
     *
     * @return - GoogleDocsUpdateFilePage
     */
    public HtmlPage selectSaveToAlfresco()
    {
        drone.switchToDefaultContent();
        WebElement saveButton = drone.findAndWait(BUTTON_SAVE_TO_ALFRESCO);
        String saveButtonId = saveButton.getAttribute("id");
        saveButton.click();
        if (isGoogleCreate)
        {
            drone.waitUntilElementDeletedFromDom(By.cssSelector("span[class='message']"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            drone.waitUntilElementDeletedFromDom(By.id(saveButtonId), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return new DocumentLibraryPage(drone);
        }
        else
        {
            return new GoogleDocsUpdateFilePage(drone, documentVersion, editOfflineFlag);
        }
    }

    /**
     * Selects the save to Alfresco button that triggers the form submit.
     *
     * @return - SharePage
     */
    public HtmlPage selectBackToShare()
    {
        WebElement saveButton = drone.findAndWait(BUTTON_BACK_TO_SHARE);
        String saveButtonId = saveButton.getAttribute("id");
        saveButton.click();
        drone.waitUntilElementDeletedFromDom(By.id(saveButtonId), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        drone.waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Switch to the frame to edit the document in google docs.
     */
    private void googledocsframe()
    {
        String frameId = drone.findAndWait(GOOGLEDOCS_FRAME).getAttribute("id");
        drone.switchToFrame(frameId);
    }

    /**
     * Switch to the frame to edit the document in google docs.
     */
    public void edit(final String content)
    {

        try
        {
            for (int i = 0; i < 5; i++)
            {
                googledocsframe();
            }
        }
        catch (NoSuchElementException e)
        {
        }
        ((WebDroneImpl) drone).getDriver().findElements(By.xpath("//*")).get(0).sendKeys(content);
        drone.switchToDefaultContent();
    }

    /**
     * Rename the document inside Google Docs.
     *
     * @return GoogleDocsRenamePage
     */
    public GoogleDocsRenamePage renameDocumentTitle()
    {
        try
        {
            if (drone.find(GOOGLEDOCS_FRAME).isDisplayed())
            {
                googledocsframe();
                drone.waitForPageLoad(15);
                wait(5);
            }
            drone.findAndWait(GOOGLE_DOC_TITLE).click();
            return new GoogleDocsRenamePage(drone, isGoogleCreate);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("time out in doing a rename of doc title", te);
        }
    }

    /**
     * Title of the document inside Google Docs.
     *
     * @return String
     */
    public String getDocumentTitle()
    {
        try
        {
            String title;
            if (drone.find(GOOGLEDOCS_FRAME).isDisplayed())
            {
                googledocsframe();
                title = drone.findAndWait(GOOGLE_DOC_TITLE).getText();
                drone.switchToDefaultContent();
            }
            else
            {
                title = drone.findAndWait(GOOGLE_DOC_TITLE).getText();
            }
            return title;
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("time out in finding the name of doc title", te);
        }
    }

    private static void wait(int seconds)
    {
        long time0;
        long time1;
        time0 = System.currentTimeMillis();
        do
        {
            time1 = System.currentTimeMillis();
        }
        while (time1 - time0 < seconds * 1000);
    }

    /**
     * Method to determine whether is Spreadsheet editor
     *
     * @return boolean
     */
    public boolean isSpreadSheetEditor()
    {
        boolean isEditor;
        if (drone.find(GOOGLEDOCS_FRAME).isDisplayed())
        {
            googledocsframe();
            isEditor = drone.findAndWait(DOCS_BRANDING_CONTAINER).getAttribute("class").contains("docs-branding-spreadsheets") &&
                drone.findAndWait(DOCS_EDITOR_CONTAINER).isDisplayed();
            drone.switchToDefaultContent();
        }
        else
        {
            return drone.findAndWait(DOCS_BRANDING_CONTAINER).getAttribute("class").contains("docs-branding-spreadsheets") &&
                drone.findAndWait(DOCS_EDITOR_CONTAINER).isDisplayed();
        }
        return isEditor;
    }

    /**
     * Method to determine whether is Document editor
     *
     * @return boolean
     */
    public boolean isDocumentEditor()
    {
        boolean isEditor;
        if (drone.find(GOOGLEDOCS_FRAME).isDisplayed())
        {
            googledocsframe();
            isEditor = drone.findAndWait(DOCS_BRANDING_CONTAINER).getAttribute("class").contains("docs-branding-documents") &&
                drone.findAndWait(DOCS_EDITOR_CONTAINER).isDisplayed();
            drone.switchToDefaultContent();
        }
        else
        {
            return drone.findAndWait(DOCS_BRANDING_CONTAINER).getAttribute("class").contains("docs-branding-documents") &&
                drone.findAndWait(DOCS_EDITOR_CONTAINER).isDisplayed();
        }
        return isEditor;
    }

    /**
     * Method to determine whether is Presentation editor
     *
     * @return boolean
     */
    public boolean isPresentationEditor()
    {
        boolean isEditor;
        if (drone.find(GOOGLEDOCS_FRAME).isDisplayed())
        {
            googledocsframe();
            isEditor = drone.findAndWait(DOCS_BRANDING_CONTAINER).getAttribute("class").contains("docs-branding-presentations") &&
                drone.findAndWait(DOCS_EDITOR_CONTAINER).isDisplayed();
            drone.switchToDefaultContent();
        }
        else
        {
            return drone.findAndWait(DOCS_BRANDING_CONTAINER).getAttribute("class").contains("docs-branding-presentations") &&
                drone.findAndWait(DOCS_EDITOR_CONTAINER).isDisplayed();
        }
        return isEditor;
    }
}
