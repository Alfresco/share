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

import static com.google.common.base.Preconditions.checkNotNull;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Class associated with PopUp frame in admin console system summary page 'Directory Manage'
 * for edit LDAP AuthChain. Methods with 'fill' - fill associated input.
 *
 * @author Aliaksei Boole
 */
public class EditLdapFrame extends SharePage
{
    @RenderWebElement
    private final static By TITLE = By.xpath("//div[@class='title']/h1");
    @RenderWebElement
    private final static By SAVE_BUTTON = By.xpath("//input[@value='Save']");
    @RenderWebElement
    private final static By CLOSE_BUTTON = By.xpath("//input[@class='cancel']");
    @RenderWebElement
    private final static By USERNAME_FORMAT = By.xpath("//input[contains(@name,'userNameFormat')]");
    @RenderWebElement
    private final static By LDAP_SERVER_URL = By.xpath("//input[contains(@name,'provider.url')]");
    @RenderWebElement
    private final static By DEF_ADMIN_USERNAME = By.xpath("//input[contains(@name,'defaultAdministratorUserNames')]");
    @RenderWebElement
    private final static By SECURITY_PRINCIPAL_NAME = By.xpath("//input[contains(@name,'security.principal')]");
    @RenderWebElement
    private final static By SECURITY_CREDENTIALS = By.xpath("//input[contains(@name,'security.credentials')]");
    @RenderWebElement
    private final static By GROUP_QUERY = By.xpath("//input[contains(@name,'groupQuery')]");
    @RenderWebElement
    private final static By USER_SEARCH_BASE = By.xpath("//input[contains(@name,'userSearchBase')]");
    @RenderWebElement
    private final static By PERSON_QUERY = By.xpath("//input[contains(@name,'personQuery')]");
    @RenderWebElement
    private final static By GROUP_SEARCH_BASE = By.xpath("//input[contains(@name,'groupSearchBase')]");
    @RenderWebElement
    private final static By ADV_SETTINGS = By.cssSelector(".action.toggler");

    public class AdvSettings extends SharePage
    {
        @RenderWebElement
        private final By GROUP_DIFF_QUERY = By.xpath("//input[contains(@name,'groupDifferentialQuery')]");
        @RenderWebElement
        private final By GROUP_DISPLAY_NAME_ATTR = By.xpath("//input[contains(@name,'groupDisplayNameAttributeName')]");
        @RenderWebElement
        private final By GROUP_ID_ATTR = By.xpath("//input[contains(@name,'groupIdAttributeName')]");
        @RenderWebElement
        private final By GROUP_MEMBER_ATTR = By.xpath("//input[contains(@name,'groupMemberAttributeName')]");
        @RenderWebElement
        private final By GROUP_TYPE = By.xpath("//input[contains(@name,'.groupType')]");
        @RenderWebElement
        private final By PERSON_TYPE = By.xpath("//input[contains(@name,'.personType')]");
        @RenderWebElement
        private final By USER_EMAIL_ATTR = By.xpath("//input[contains(@name,'userEmailAttributeName')]");
        @RenderWebElement
        private final By USER_ID_ATTR = By.xpath("//input[contains(@name,'userIdAttributeName')]");
        @RenderWebElement
        private final By USER_FIRST_NAME_ATTR = By.xpath("//input[contains(@name,'userFirstNameAttributeName')]");
        @RenderWebElement
        private final By USER_ORG_ID_ATTR = By.xpath("//input[contains(@name,'userOrganizationalIdAttributeName')]");
        @RenderWebElement
        private final By USER_LAST_NAME_ATTR = By.xpath("//input[contains(@name,'userLastNameAttributeName')]");
        @RenderWebElement
        private final By MODIFY_TIMESTAMP_ATTR = By.xpath("//input[contains(@name,'modifyTimestampAttributeName')]");
        @RenderWebElement
        private final By TIMESTAMP_FORMAT = By.xpath("//input[contains(@name,'timestampFormat')]");

        @SuppressWarnings("unchecked")
        @Override
        public AdvSettings render(RenderTime timer)
        {
            webElementRender(timer);
            return this;
        }


        @SuppressWarnings("unchecked")
        @Override
        public AdvSettings render()
        {
            return render(new RenderTime(maxPageLoadingTime));
        }

        public void fillGroupDiffQuery(String groupDiffQuery)
        {
            fillField(GROUP_DIFF_QUERY, groupDiffQuery);
        }

        public void fillGroupDisplayNameAttr(String displayNameAttr)
        {
            fillField(GROUP_DISPLAY_NAME_ATTR, displayNameAttr);
        }

        public void fillGroupIdAttr(String groupIdAttr)
        {
            fillField(GROUP_ID_ATTR, groupIdAttr);
        }

        public void fillGroupMemberAttr(String groupMemberAttr)
        {
            fillField(GROUP_MEMBER_ATTR, groupMemberAttr);
        }

        public void fillGroupType(String groupType)
        {
            fillField(GROUP_TYPE, groupType);
        }

        public void fillPersonType(String personType)
        {
            fillField(PERSON_TYPE, personType);
        }

        public void fillUserEmailAttr(String userEmailAttr)
        {
            fillField(USER_EMAIL_ATTR, userEmailAttr);
        }

        public void fillUserIdAttr(String userIdAttr)
        {
            fillField(USER_ID_ATTR, userIdAttr);
        }

        public void fillUserFirstNameAttr(String userFirstNameAttr)
        {
            fillField(USER_FIRST_NAME_ATTR, userFirstNameAttr);
        }

        public void fillUserOrganisationId(String organizationId)
        {
            fillField(USER_ORG_ID_ATTR, organizationId);
        }

        public void fillUserLastNameAttr(String userLastNameAttr)
        {
            fillField(USER_LAST_NAME_ATTR, userLastNameAttr);
        }

        public void fillModifyTS(String timeStampAttr)
        {
            fillField(MODIFY_TIMESTAMP_ATTR, timeStampAttr);
        }

        public void fillTimeStampFormat(String timeStampFormat)
        {
            fillField(TIMESTAMP_FORMAT, timeStampFormat);
        }

        private void fillField(By selector, String text)
        {
            checkNotNull(selector);
            WebElement inputField = findAndWait(selector);
            inputField.clear();
            if (text != null)
            {
                inputField.sendKeys(text);
            }
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public EditLdapFrame render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    /**
     * Return Frame title.
     *
     * @return String
     */
    @Override
    public String getTitle()
    {
        return findAndWait(TITLE).getText();
    }


    @SuppressWarnings("unchecked")
    @Override
    public EditLdapFrame render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Click 'Save' button
     *
     * @return DirectoryManagementPage
     */
    public HtmlPage clickSave()
    {
        click(SAVE_BUTTON);
        driver.switchTo().defaultContent();
        return getCurrentPage();
    }

    /**
     * Click 'Close' button
     *
     * @return DirectoryManagementPage
     */
    public HtmlPage clickClose()
    {
        click(CLOSE_BUTTON);
        driver.switchTo().defaultContent();
        return getCurrentPage();
    }

    public void fillLdapUrl(String ldapUrl)
    {
        fillField(LDAP_SERVER_URL, ldapUrl);
    }

    public void fillUserNameFormat(String userNameFormat)
    {
        fillField(USERNAME_FORMAT, userNameFormat);
    }

    public void fillAdminUserName(String adminUserName)
    {
        fillField(DEF_ADMIN_USERNAME, adminUserName);
    }

    public void fillSecurityNamePrincipal(String securityNamePrincipal)
    {
        fillField(SECURITY_PRINCIPAL_NAME, securityNamePrincipal);
    }

    public void fillUserSearchBase(String userSearchBase)
    {
        fillField(USER_SEARCH_BASE, userSearchBase);
    }

    public void fillGroupSearchBase(String groupSearchBase)
    {
        fillField(GROUP_SEARCH_BASE, groupSearchBase);
    }

    public void fillSecurityCredentials(String securityCredentials)
    {
        fillField(SECURITY_CREDENTIALS, securityCredentials);
    }

    public void fillGroupQuery(String groupQuery)
    {
        fillField(GROUP_QUERY, groupQuery);
    }

    public void fillPersonQuery(String personQuery)
    {
        fillField(PERSON_QUERY, personQuery);
    }

    /**
     * Click on Open Adv settings arrow. Method return object associated with "adv options" inputs.
     *
     * @return AdvSettings
     */
    public AdvSettings openAdvSettings()
    {
        click(ADV_SETTINGS);
        return factoryPage.instantiatePage(driver, AdvSettings.class);
    }

    private void click(By locator)
    {
        checkNotNull(locator);
        WebElement element = findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(selector);
        WebElement inputField = findAndWait(selector);
        inputField.clear();
        if (text != null)
        {
            inputField.sendKeys(text);
        }
    }

}
