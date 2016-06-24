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
package org.alfresco.po.share.preview;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.ElementState;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class PdfJsPlugin extends SharePage
{
    private static Log logger = LogFactory.getLog(PdfJsPlugin.class);

    // Controls container
    private static final String VIEWER_CONTROLS_DIV = ".controls";

    // <span> containing the total number of pages
    private static final String CONTROLS_NUM_PAGES = ".controls .numPages";

    // <input> containing the current page number
    private static final String CONTROLS_PAGE_NUM = ".controls input[id$=\"pageNumber\"]";

    // Sidebar container
    private static final String SIDEBAR_DIV = ".sidebar";

    // Main document container
    private static final String VIEWER_MAIN_DIV = ".viewer.documentView";

    // Main document container
    private static final String VIEWER_MAIN_PAGES = ".viewer.documentView .page";

    // Big spinner is present while the document is being loaded / transformed
    private static final String VIEWER_MAIN_SPINNER = ".viewer.documentView .spinner";

    // Thumbnails container
    private static final String SIDEBAR_PAGES = ".sidebar .documentView .page";

    // Generic button, can add [id$="-blah"] to get a specific one
    private static final String VIEWER_CONTROLS_BUTTON = ".controls span.yui-button";

    @SuppressWarnings("unchecked")
    @Override
    public PdfJsPlugin render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verifies if the page has rendered completely by checking the page load is
     * complete and in addition it will observe key HTML elements have rendered.
     *
     * @param timer Max time to wait
     * @return {@link PdfJsPlugin}
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfJsPlugin render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(By.cssSelector(VIEWER_MAIN_DIV)),
                getVisibleRenderElement(By.cssSelector(VIEWER_CONTROLS_DIV)),
                new RenderElement(By.cssSelector(VIEWER_MAIN_SPINNER), ElementState.INVISIBLE)); // Spinner disappears when the content is loaded
        return this;
    }


    /**
     * Checks if sidebar is displayed
     * 
     * @return boolean
     */
    public boolean isSidebarVisible()
    {
        try
        {
            WebElement sidebar = driver.findElement(By.cssSelector(SIDEBAR_DIV));
            return sidebar.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No sidebar container " + nse);
            throw new PageException("Unable to find sidebar container.", nse);
        }
    }

    public WebElement getToolbarButton(String btnId)
    {
        try
        {
            return driver.findElement(By.cssSelector(VIEWER_CONTROLS_BUTTON + "[id$=\"-" + btnId + "\"] button"));
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No button with ID " + btnId, nse);
            return null;
        }
    }

    public boolean isToolbarButtonEnabled(String btnId)
    {
        WebElement button = getToolbarButton(btnId);
        if (button != null)
        {
            logger.info("id: " + button.getAttribute("id") + ", disabled: " + button.getAttribute("disabled"));
            return !("disabled".equalsIgnoreCase(button.getAttribute("disabled")) || "true".equalsIgnoreCase(button.getAttribute("disabled")));
        }
        else
        {
            logger.error("No button with ID " + btnId);
            throw new PageException("Unable to find button with ID " + btnId);
        }
    }

    public void clickToolbarButton(String btnId)
    {
        WebElement button = getToolbarButton(btnId);
        if (button != null)
        {
            logger.info("id: " + button.getAttribute("id") + ", disabled: " + button.getAttribute("disabled"));
            button.click();
        }
        else
        {
            logger.error("No button with ID " + btnId);
            throw new PageException("Unable to find button with ID " + btnId);
        }
    }

    /**
     * Return the current page number
     * 
     * @return int
     */
    public int getCurrentPageNum()
    {
        try
        {
            WebElement span = findFirstDisplayedElement(By.cssSelector(CONTROLS_PAGE_NUM));
            return Integer.parseInt(span.getAttribute("value"), 10);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No pages found " + nse);
            throw new PageException("Unable to find page number field.", nse);
        }
    }

    /**
     * Return the claimed number of pages shown in the toolbar
     * 
     * @return int
     */
    public int getNumClaimedPages()
    {
        try
        {
            WebElement span = findFirstDisplayedElement(By.cssSelector(CONTROLS_NUM_PAGES));
            return Integer.parseInt(span.getText(), 10);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No pages found " + nse);
            throw new PageException("Unable to find any pages.", nse);
        }
    }

    /**
     * Return the number of pages present within the main view
     * 
     * @return int
     */
    public int getNumDisplayedPages(String viewerClass)
    {
        try
        {
            List<WebElement> pages = findDisplayedElements(By.cssSelector(viewerClass));
            return pages.size();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No pages found " + nse);
            throw new PageException("Unable to find any pages.", nse);
        }
    }

    /**
     * Return the number of pages present within the main view
     * 
     * @return int
     */
    public int getMainViewNumDisplayedPages()
    {
        return getNumDisplayedPages(VIEWER_MAIN_PAGES);
    }

    /**
     * Return the number of pages present within the main view
     * 
     * @return int
     */
    public int getSidebarNumDisplayedPages()
    {
        return getNumDisplayedPages(SIDEBAR_PAGES);
    }

}
