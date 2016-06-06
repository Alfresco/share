/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.site.document;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.dashlet.AdvancedTinyMceEditor;
import org.alfresco.po.share.site.SitePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * In Line Edit Page Object, Where user edit the content.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class InlineEditPage extends SitePage
{
    protected static final By NAME = By.cssSelector("input[id$='default_prop_cm_name']");
    protected static final By TITLE = By.cssSelector("input[id$='default_prop_cm_title']");
    protected static final By DESCRIPTION = By.cssSelector("textarea[id$='default_prop_cm_description']");
    protected static final By SUBMIT_BUTTON = By.cssSelector("button[id$='form-submit-button']");
    protected static final String CONTENT_IFRAME="template_x002e_inline-edit_x002e_inline-edit_x0023_default_prop_cm_content_ifr";
    
    private AdvancedTinyMceEditor contentTinyMceEditor;

    @SuppressWarnings("unchecked")
    @Override
    public InlineEditPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(NAME), getVisibleRenderElement(TITLE), getVisibleRenderElement(DESCRIPTION),
                getVisibleRenderElement(SUBMIT_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InlineEditPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Pass MimeType to get appropriate document page object.
     */
    public HtmlPage getInlineEditDocumentPage(MimeType mimeType)
    {
        switch (mimeType)
        {
            case HTML:
                return factoryPage.instantiatePage(driver,EditHtmlDocumentPage.class);
            default:
                return factoryPage.instantiatePage(driver, EditTextDocumentPage.class);
        }
    }

    /**
     * Clear the input field and inserts the new value.
     *
     * @param input {@link org.openqa.selenium.WebElement} represents the form input
     * @param value String input value to enter
     */
    public void setInput(final WebElement input, final String value)
    {
        input.clear();
        input.sendKeys(value);
    }

    /**
     * Enters a value in to the properties form.
     *
     * @param name String name input
     */
    public void setName(final String name)
    {
        setInput(findAndWait(NAME), name);
    }
    
    /**
     * Enters a value in to the properties form.
     *
     * @param name String name input
     */
    public void setTitle(final String name)
    {
        setInput(findAndWait(TITLE), name);
    }
    
    /**
     * Get TinyMCEEditor object to navigate TinyMCE functions.
     * 
     * @return AdvancedTinyMceEditor
     */
    public AdvancedTinyMceEditor getContentTinyMCEEditor()
    {
        contentTinyMceEditor.setTinyMce();
        return contentTinyMceEditor;
    }
    
    /**
     * Method for inserting text into the Reply form
     * 
     * @param txtLines String
     */
    public void insertTextInContent(String txtLines)
    {
        try
        {
            contentTinyMceEditor.setTinyMce();
            contentTinyMceEditor.addContent(txtLines);
        }
        catch (TimeoutException toe)
        {
            throw new PageException("Time out finding #tinymce content", toe);
        }
    }
}
