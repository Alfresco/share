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

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.po.share.systemsummary.directorymanagement.AuthType.AD_LDAP;
import static org.alfresco.po.share.systemsummary.directorymanagement.AuthType.OPEN_LDAP;
import static org.alfresco.po.share.systemsummary.directorymanagement.DirectoryInfoRow.Action.*;

/**
 * Object associated with AuthChain row in 'Authentication Chain' table.
 *
 * @author Aliaksei Boole
 */
public class DirectoryInfoRow extends HtmlElement
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

    public DirectoryInfoRow(WebElement webElement, WebDrone drone)
    {
        super(webElement, drone);
    }

    /**
     * Return auth chain name.
     *
     * @return
     */
    public String getName()
    {
        return findAndWait(NAME_BY).getText();
    }

    /**
     * Return auth chain type for this row.
     *
     * @return
     */
    public String getType()
    {
        return findAndWait(TYPE_BY).getText();
    }

    /**
     * Return is Auth chain enabled or disabled.
     *
     * @return
     */
    public String getEnabled()
    {
        return findAndWait(ENABLED_BY).getText();
    }

    /**
     * Return is Synchronized auth chain or not.
     *
     * @return
     */
    public String getSyncStatus()
    {
        return findAndWait(SYNCHRONIZED_BY).getText();
    }

    /**
     * Click on Edit button for selected auth chain.
     *
     * @return
     */
    public SharePage clickEdit()
    {
        String typeText = getType();
        List<WebElement> actionsElem = findAllWithWait(ACTIONS_BY);
        actionsElem.get(EDIT.index).click();
        drone.switchToFrame("admin-dialog");
        if (typeText.equals(OPEN_LDAP.text) || typeText.equals(AD_LDAP.text))
        {
            return new EditLdapFrame(drone);
        }
        throw new PageOperationException("It is impossible to create an object-frame for this type. Please, add this variant.");
    }

    /**
     * Click on Status button and open 'Status' table.
     *
     * @return
     */
    public List<StatusRow> clickStatus()
    {
        List<WebElement> actionsElem = findAllWithWait(ACTIONS_BY);
        actionsElem.get(STATUS.index).click();
        return getStatusRows();
    }

    /**
     * Return objects associated with status rows in table.
     *
     * @return
     */
    public List<StatusRow> getStatusRows()
    {
        List<WebElement> rowElements = findAllWithWait(STATUS_ROW);
        List<StatusRow> statusRows = new ArrayList<StatusRow>(6);
        for (WebElement rowElem : rowElements)
        {
            statusRows.add(new StatusRow(rowElem, drone));
        }
        return statusRows;
    }

    /**
     * Click 'Remove' button.
     */
    public void clickRemove()
    {
        List<WebElement> actionsElem = findAllWithWait(ACTIONS_BY);
        actionsElem.get(REMOVE.index).click();
    }

    /**
     * Click on 'Test Sync' button.
     */
    public void clickTestSync()
    {
        List<WebElement> actionsElem = findAllWithWait(ACTIONS_BY);
        actionsElem.get(TEST_SYNC.index).click();
    }
}
