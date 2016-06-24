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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(id="alf-filters")
/**
 * Represent elements found on the HTML page relating to the document library
 * left hand menus that appear on the document library site page.
 * 
 * @author Jamie Allison
 * @since 4.3.0
 */
public class TreeMenuNavigation extends PageElement
{
    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * Use this to refer to a specific left hand menu tree in TreeMenuNavigation
     * 
     * @author Jamie Allison
     */
    public enum TreeMenu
    {
        DOCUMENTS("//div[contains(@class,'doclib-filter')]", false),
        LIBRARY("//div[contains(@class,'treeview')]", true),
        CATEGORIES("//div[contains(@class,'categoryview')]", true),
        TAGS("//div[contains(@id,'tags')]/div[@class='filter']", false);

        private final String xpath;

        private final boolean hirearchy;

        private TreeMenu(String xpath, boolean hirearchy)
        {
            this.xpath = xpath;
            this.hirearchy = hirearchy;
        }

        public String getXpath()
        {
            return xpath;
        }

        private boolean isHirearchy()
        {
            return hirearchy;
        }
    }

    /**
     * Use this to refer to items in the Documents menu.
     * 
     * @author Jamie Allison
     */
    public enum DocumentsMenu
    {
        ALL_DOCUMENTS("all"),
        IM_EDITING("editingMe"),
        OTHERS_EDITING("editingOthers"),
        RECENTLY_MODIFIED("recentlyModified"),
        RECENTLY_ADDED("recentlyAdded"),
        MY_FAVORITES("favourites"),
        SYNCED_CONTENT("synced");

        private final String elClass;

        private DocumentsMenu(String elClass)
        {
            this.elClass = elClass;
        }

        private String getXpath()
        {
            return String.format("//span[@class='%s']/a", elClass);
        }
    }

    public static final String REPOSITORY_ROOT_PROPERTY = "repository.tree.root";
    public static final String DOCUMENT_LIBRARY_ROOT_PROPERTY = "doclib.tree.root";
    public static final String CATEGORY_ROOT_PROPERTY = "categories.tree.root";
    public static final String SHARED_FILES_ROOT_PROPERTY = "sharedfiles.tree.root";
    
    private static final String TREE_NODE_ELEMENT = "//span[text()='%s']";
    private static final String TREE_NODE_TWISTY = "//span[text()='%s']/../../td[last()-1]";
    private static final String TREE_NODE_LOADING = "//span[text()='%s']/../../td[contains(@class,'ygtloading')]";
    private static final String TREE_NODE_CHILDREN = "/../../../../../div//span[contains(@class,'ygtvlabel')]";
    private static final String TAG_NODE = "//a[text()='%s']";
    private static final long FOLDER_LOAD_TIME = 5000;

    /**
     * Checks if the menu node is expanded.
     * 
     * @param node By
     * @return boolean
     */
    private boolean isNodeExpanded(By node)
    {
        try
        {
            String elClass = driver.findElement(node).getAttribute("class");
            if (elClass.contains("ygtvtm") || elClass.contains("ygtvlm"))
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
     * Checks if the menu is open
     * 
     * @param treeMenu TreeMenu
     * @return boolean
     */
    private boolean isMenuExpanded(TreeMenu treeMenu)
    {
        if (!isMenuTreeVisible(treeMenu))
        {
            throw new IllegalArgumentException(treeMenu + " is not visible on the page.");
        }

        By menuLocator = By.xpath(treeMenu.getXpath() + "/h2");
        try
        {
            String elClass = driver.findElement(menuLocator).getAttribute("class");
            if (elClass.contains("alfresco-twister-open"))
            {
                return true;
            }
        }
        catch (NoSuchElementException e)
        {
            logger.error("Unable to find element " + menuLocator, e);
        }
        return false;
    }

    /**
     * Expands the menu tree in order of the list
     * 
     * @param treeMenu TreeMenu
     * @param nodePath List<String>
     */
    private void expandNode(TreeMenu treeMenu, List<String> nodePath)
    {
        expandMenu(treeMenu);

        if (treeMenu.isHirearchy())
        {
            String xpath = treeMenu.getXpath() + TREE_NODE_TWISTY;
            for (String node : nodePath)
            {
                By nodeLocator = By.xpath(String.format(xpath, node));
                By loadingLocator = By.xpath(treeMenu.getXpath() + String.format(TREE_NODE_LOADING, node));
                if (!isNodeExpanded(nodeLocator))
                {
                    try
                    {
                        findAndWait(nodeLocator).click();

                        try
                        {
                            findAndWait(loadingLocator);
                        }
                        catch (TimeoutException e)
                        {
                        }

                        waitUntilElementDeletedFromDom(loadingLocator, FOLDER_LOAD_TIME);
                    }
                    catch (TimeoutException e)
                    {
                        throw new PageException("Unable to find element " + nodeLocator, e);
                    }
                }
            }
        }
    }

    /**
     * Opens the menu tree
     * 
     * @param treeMenu TreeMenu
     */
    private void expandMenu(TreeMenu treeMenu)
    {
        if (!isMenuExpanded(treeMenu))
        {
            By menuLocator = By.xpath(treeMenu.getXpath());
            try
            {
                driver.findElement(menuLocator).click();
            }
            catch (NoSuchElementException e)
            {
                throw new PageException("Unable to find element " + menuLocator, e);
            }
        }
    }

    /**
     * Expands the Documents menu and selects the given link.
     * 
     * @param docMenu DocumentsMenu
     * @return The page loaded when the node is selected.
     */
    public HtmlPage selectDocumentNode(DocumentsMenu docMenu)
    {
        if (docMenu == null)
        {
            throw new IllegalArgumentException("DocumentsMenu is required.");
        }

        String xpath = TreeMenu.DOCUMENTS.getXpath() + docMenu.getXpath();
        By nodeLocator = By.xpath(xpath);
        try
        {
            expandMenu(TreeMenu.DOCUMENTS);
            driver.findElement(nodeLocator).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find element " + nodeLocator, e);
        }

        return getCurrentPage().render();
    }

    /**
     * Expands the Tags menu and selects the given link.
     * 
     * @param tagName String
     * @return The page loaded when the node is selected.
     */

    /**
     * Expands the menu tree and selects the last node in the supplied node
     * path. For Documents menu use selectDocumentNode(DocumentsMenu)
     * 
     * @param treeMenu TreeMenu
     * @param nodePath String...
     * @return The page loaded when the node is selected.
     */
    public HtmlPage selectNode(TreeMenu treeMenu, String... nodePath)
    {
        if (treeMenu == null)
        {
            throw new IllegalArgumentException("TreeMenu is required.");
        }

        if (nodePath == null)
        {
            throw new IllegalArgumentException("Node path is required.");
        }

        if (treeMenu == TreeMenu.DOCUMENTS || treeMenu == TreeMenu.TAGS)
        {
            throw new IllegalArgumentException("Not supported for menu " + treeMenu);
        }

        List<String> nodes = new ArrayList<>(Arrays.asList(nodePath));

        String node = nodes.get(nodes.size() - 1);
        nodes.remove(nodes.size() - 1);

        expandNode(treeMenu, nodes);

        String xpath = treeMenu.getXpath() + String.format(TREE_NODE_ELEMENT, node);
        By nodeLocator = By.xpath(xpath);
        try
        {
            driver.findElement(nodeLocator).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Unable to find element " + nodeLocator, e);
        }

        return getCurrentPage();
    }

    /**
     * Expands the menu tree and returns a list of the immediate children text.
     * 
     * @param treeMenu TreeMenu
     * @param nodePath String...
     * @return List<String>
     */
    public List<String> getNodeChildren(TreeMenu treeMenu, String... nodePath)
    {
        if (treeMenu == null)
        {
            throw new IllegalArgumentException("TreeMenu is required.");
        }
        if (nodePath == null)
        {
            throw new IllegalArgumentException("node path is required.");
        }

        if (!treeMenu.hirearchy)
        {
            throw new IllegalArgumentException(treeMenu + " does not support child nodes.");
        }

        List<String> nodes = new ArrayList<>(Arrays.asList(nodePath));

        String node = nodes.get(nodes.size() - 1);

        expandNode(treeMenu, nodes);

        List<String> children = new ArrayList<>();

        String xpath = treeMenu.getXpath() + String.format(TREE_NODE_ELEMENT, node) + TREE_NODE_CHILDREN;
        By nodeLocator = By.xpath(xpath);
        try
        {
            List<WebElement> results = driver.findElements(nodeLocator);

            for (WebElement element : results)
            {
                children.add(element.getText());
            }
        }
        catch (NoSuchElementException e)
        {
            // Do nothing
        }

        return children;
    }

    /**
     * Retuns true if the given menu tree is visible.
     * 
     * @return boolean
     */
    public boolean isMenuTreeVisible(TreeMenu treeMenu)
    {
        if (treeMenu == null)
        {
            throw new UnsupportedOperationException("TreeMenu is required.");
        }

        try
        {
            return findAndWait(By.xpath(treeMenu.getXpath())).isDisplayed();
        }
        catch (TimeoutException e)
        {
            logger.trace("Exceeded time to find the " + treeMenu + " tree." + e.getMessage());
        }
        return false;
    }

    /**
     * Checks if the given menu item in the Documents menu is visible.
     * 
     * @param docMenu DocumentsMenu
     * @return <code>true</code> if visible.
     */
    public boolean isDocumentNodeVisible(DocumentsMenu docMenu)
    {
        if (docMenu == null)
        {
            throw new UnsupportedOperationException("DocumentsMenu is required.");
        }

        try
        {
            return driver.findElement(By.xpath(TreeMenu.DOCUMENTS.getXpath() + docMenu.getXpath())).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            logger.trace("Unable to find the " + docMenu + " tree." + e.getMessage());
        }
        return false;
    }

    /**
     * This method returns the count for the given tag string.
     *
     * @param tagName String
     * @return int
     */
    public int getTagCount(String tagName)
    {
        if (tagName == null)
        {
            throw new IllegalArgumentException("TagName is required.");
        }

        //Tag names are lower case so change tagNAme to lower case to allow matching.
        tagName = tagName.toLowerCase();

        try
        {
            String xpath = TreeMenu.TAGS.getXpath() + String.format(TAG_NODE, tagName) + "/..";
            String count = findAndWait(By.xpath(xpath)).getText();
            
            return Integer.parseInt(count.substring(count.indexOf("(") + 1, count.indexOf(")")));
        }
        catch (TimeoutException te)
        {
            logger.error("Exceeded time to find out the " + tagName + " count: ", te);
        }
        catch (NumberFormatException ne)
        {
            logger.error("Unable to convert tags count string value into int : ", ne);
        }

        throw new PageOperationException("Unable to find the given tag count : " + tagName);
    }
}
