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
package org.alfresco.po.share.site.contentrule;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.site.SitePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * FolderRulesPage page object, holds all element of the HTML page relating to Folder Rule Page
 * if folder didn't have any rules.
 * 
 * @author Aliaksei Boole
 * @since 1.0
 */
public class FolderRulesPage extends SitePage
{

    protected static final By TITLE_SELECTOR = By.cssSelector(".yui-u.first.rules-title>h1");
    private static final By LINK_CREATE_RULE_PAGE_SELECTOR = By.cssSelector("div[class=dialog-option] a[href*='rule-edit']");
    private static final By LINK_TO_RULE_SET_SELECTOR = By.cssSelector("div[class=dialog-option] a[id*='linkToRuleSet']");
    private static final By INHERIT_RULES_TOGGLE = By.cssSelector("button[id$='_default-inheritButton-button']");
    private static final By THIS_FOLDER_INHERIT_RULES_MESSAGE = By.cssSelector("div[id$='_default-inheritedRules']");

    @SuppressWarnings("unchecked")
    @Override
    public FolderRulesPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(TITLE_SELECTOR), getVisibleRenderElement(LINK_CREATE_RULE_PAGE_SELECTOR),
                getVisibleRenderElement(LINK_TO_RULE_SET_SELECTOR));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FolderRulesPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public HtmlPage openCreateRulePage()
    {
        WebElement element = findAndWait(LINK_CREATE_RULE_PAGE_SELECTOR);
        element.click();
        return getCurrentPage();
    }

    /**
     * Clicks on the button to switch on/off inherit rules (if it is displayed)
     * 
     * @return FolderRulesPage
     */
    public HtmlPage toggleInheritRules()
    {
        WebElement element = findAndWait(INHERIT_RULES_TOGGLE);
        element.click();
        waitUntilAlert(5);
        return factoryPage.instantiatePage(driver, FolderRulesPage.class);
    }

    public boolean isPageCorrect(final String folderName)
    {
        return (isTitleCorrect(folderName) && isLinkCreateRuleAvailable() && isLinkToRuleSetAvailable());
    }

    protected boolean isTitleCorrect(final String folderName)
    {
        String expectedTitle = folderName + ": Rules";
        String titleOnPage = findAndWait(TITLE_SELECTOR).getText();
        return (expectedTitle.equals(titleOnPage));
    }

    private boolean isLinkCreateRuleAvailable()
    {
        WebElement linkCreateRule = findAndWait(LINK_CREATE_RULE_PAGE_SELECTOR);
        return (linkCreateRule.isDisplayed() && linkCreateRule.isEnabled());
    }

    private boolean isLinkToRuleSetAvailable()
    {
        WebElement linkToRuleSet = findAndWait(LINK_TO_RULE_SET_SELECTOR);
        return (linkToRuleSet.isDisplayed() && linkToRuleSet.isEnabled());
    }

    /**
     * Returns true if the button for switching inherit rules on and off is present
     * Should be always called before clicking the button
     * 
     * @return boolean
     */
    public boolean isInheritRuleToggleAvailable()
    {
        WebElement inheritRulesToggle = findAndWait(INHERIT_RULES_TOGGLE);
        return inheritRulesToggle.isDisplayed();
    }

    /**
     * Returns true if "This folder inherits Rules from its parent folder(s)." is displayed, otherwise false
     * 
     * @return boolean
     */
    public boolean isInheritRulesMessageDisplayed()
    {
        try
        {
            WebElement inheritRulesToggle = driver.findElement(THIS_FOLDER_INHERIT_RULES_MESSAGE);
            return inheritRulesToggle.isDisplayed();
        }
        catch (NoSuchElementException te)
        {
        }
        return false;

    }

    /**
     * Returns text displayed on the button for switching the rules on/off:
     * Inherit Rules or Don't Inherit Rules
     * 
     * @return String
     */
    public String getInheritRulesText()
    {
        String inheritRulesText = findAndWait(INHERIT_RULES_TOGGLE).getText();
        return inheritRulesText;
    }
}
