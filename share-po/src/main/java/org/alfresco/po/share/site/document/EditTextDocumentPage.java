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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * In Line Edit Page Object, Where user edit the content.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class EditTextDocumentPage extends CreatePlainTextContentPage
{
    private static final Log logger = LogFactory.getLog(EditTextDocumentPage.class);

    @SuppressWarnings("unchecked")
    @Override
    public EditTextDocumentPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditTextDocumentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Save the {@link ContentDetails}.
     * 
     * @param details - The {@link ContentDetails} to be saved.
     * @return {@link DocumentDetailsPage}
     */
    public HtmlPage save(ContentDetails details)
    {
        return create(details);
    }

    /**
     * Save the {@link ContentDetails}.
     * 
     * @param details - The {@link ContentDetails} to be saved.
     * @return {@link DocumentDetailsPage}
     */
    public HtmlPage saveWithValidation(ContentDetails details)
    {
        createWithValidation(details);
        return getCurrentPage();
    }

    /**
     * Get the {@link ContentDetails} when try to do in line edit.
     * 
     * @return {@link ContentDetails}
     */
    public ContentDetails getDetails()
    {
        ContentDetails details = null;
        WebElement element = findAndWait(NAME);
        if (element != null)
        {
            details = new ContentDetails();
            details.setName(element.getAttribute("value"));

            element = findAndWait(TITLE);
            if (element != null)
            {
                details.setTitle(element.getAttribute("value"));
            }

            element = findAndWait(DESCRIPTION);
            if (element != null)
            {
                details.setDescription(element.getText());
            }

            element = findAndWait(CONTENT);

            if (element != null)
            {
                details.setContent(element.getAttribute("value"));
            }
        }
        return details;
    }

    @SuppressWarnings("unchecked")
    /**
     * Method to select Cancel button
     * @return {@Link DocumentDetailsPage or @Link DocumentLibraryPage}
     */
    public <T extends SharePage> T selectCancel()
    {
        try
        {
            findAndWait(By.cssSelector("button[id$='_default-form-cancel-button']")).click();
            return (T) getCurrentPage();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Cancel button doesn't exist", te);
            }
        }
        throw new PageException();
    }
}
