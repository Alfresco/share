/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.po.share.systemsummary.directorymanagement;

import static org.alfresco.po.share.systemsummary.directorymanagement.AuthType.AD_LDAP;
import static org.alfresco.po.share.systemsummary.directorymanagement.AuthType.OPEN_LDAP;
import static org.alfresco.po.share.systemsummary.directorymanagement.DirectoryInfoRow.Action.EDIT;
import static org.alfresco.po.share.systemsummary.directorymanagement.DirectoryInfoRow.Action.REMOVE;
import static org.alfresco.po.share.systemsummary.directorymanagement.DirectoryInfoRow.Action.STATUS;
import static org.alfresco.po.share.systemsummary.directorymanagement.DirectoryInfoRow.Action.TEST_SYNC;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Object associated with AuthChain row in 'Authentication Chain' table.
 *
 * @author Aliaksei Boole
 */
public class DirectoryInfoRow extends PageElement
{
    private final static By NAME_BY = By.xpath("./td[2]");
    private final static By TYPE_BY = By.xpath("./td[3]");
    private final static By ENABLED_BY = By.xpath("./td[4]");
    private final static By SYNCHRONIZED_BY = By.xpath("./td[5]");
    private final static By ACTIONS_BY = By.xpath("./td[6]/a");

    private final static By STATUS_ROW = By.xpath("./td[6]/div/table[@class='sync-status']/tbody/tr");

    public enum Action
    {
        EDIT(0),
        TEST(1),
        RESET(2),
        REMOVE(3),
        TEST_SYNC(4),
        STATUS(5);

        Action(int index)
        {
            this.index = index;
        }

        public final int index;
    }

    public DirectoryInfoRow(WebElement webElement, WebDriver driver)
    {
        setWrappedElement(webElement);
    }

    /**
     * Return auth chain name.
     *
     * @return String
     */
    public String getName()
    {
        return findAndWait(NAME_BY).getText();
    }

    /**
     * Return auth chain type for this row.
     *
     * @return String
     */
    public String getType()
    {
        return findAndWait(TYPE_BY).getText();
    }

    /**
     * Return is Auth chain enabled or disabled.
     *
     * @return String
     */
    public String getEnabled()
    {
        return findAndWait(ENABLED_BY).getText();
    }

    /**
     * Return is Synchronized auth chain or not.
     *
     * @return String
     */
    public String getSyncStatus()
    {
        return findAndWait(SYNCHRONIZED_BY).getText();
    }

    /**
     * Click on Edit button for selected auth chain.
     *
     * @return SharePage
     */
    public SharePage clickEdit()
    {
        String typeText = getType();
        List<WebElement> actionsElem = driver.findElements(ACTIONS_BY);
        actionsElem.get(EDIT.index).click();
        driver.switchTo().frame("admin-dialog");
        if (typeText.equals(OPEN_LDAP.text) || typeText.equals(AD_LDAP.text))
        {
            return factoryPage.instantiatePage(driver, EditLdapFrame.class);
        }
        throw new PageOperationException("It is impossible to create an object-frame for this type. Please, add this variant.");
    }

    /**
     * Click on Status button and open 'Status' table.
     *
     * @return List<StatusRow>
     */
    public List<StatusRow> clickStatus()
    {
        List<WebElement> actionsElem = driver.findElements(ACTIONS_BY);
        actionsElem.get(STATUS.index).click();
        return getStatusRows();
    }

    /**
     * Return objects associated with status rows in table.
     *
     * @return List<StatusRow>
     */
    public List<StatusRow> getStatusRows()
    {
        List<WebElement> rowElements = driver.findElements(STATUS_ROW);
        List<StatusRow> statusRows = new ArrayList<StatusRow>(6);
        for (WebElement rowElem : rowElements)
        {
            statusRows.add(new StatusRow(rowElem, driver));
        }
        return statusRows;
    }

    /**
     * Click 'Remove' button.
     */
    public void clickRemove()
    {
        List<WebElement> actionsElem = driver.findElements(ACTIONS_BY);
        actionsElem.get(REMOVE.index).click();
    }

    /**
     * Click on 'Test Sync' button.
     */
    public void clickTestSync()
    {
        List<WebElement> actionsElem = driver.findElements(ACTIONS_BY);
        actionsElem.get(TEST_SYNC.index).click();
    }
}
