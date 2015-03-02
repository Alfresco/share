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

package org.alfresco.po.share.search;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Advance search CRM page object, holds all element of the CRM attachment.
 * This page helps to search for the content which are saved in cloud via Salesforce.
 * 
 * @author Subashni Prasanna
 * @since 1.6
 */
public class AdvanceSearchCRMPage extends AdvanceSearchPage
{
    protected static final By CRM_ACCOUNT_IDENTIFIER = By.cssSelector("input[id$='prop_crm_accountId']");
    protected static final By CRM_ACCOUNT_NAME = By.cssSelector("input[id$='prop_crm_accountName']");
    protected static final By CRM_OPPOR_NAME = By.cssSelector("input[id$='prop_crm_opportunityName']");
    protected static final By CRM_CONTRACT_NUMBER = By.cssSelector("input[id$='prop_crm_contractNumber']");
    protected static final By CRM_CONTRACT_NAME = By.cssSelector("input[id$='prop_crm_contractName']");
    protected static final By CRM_CASE_NUMBER = By.cssSelector("input[id$='prop_crm_caseNumber']");
    protected static final By CRM_CASE_NAME = By.cssSelector("input[id$='prop_crm_caseName']");

    public AdvanceSearchCRMPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    public AdvanceSearchCRMPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(CRM_ACCOUNT_IDENTIFIER), getVisibleRenderElement(CRM_ACCOUNT_NAME),
                    getVisibleRenderElement(CRM_OPPOR_NAME), getVisibleRenderElement(CRM_CONTRACT_NUMBER), getVisibleRenderElement(CRM_CONTRACT_NAME),
                    getVisibleRenderElement(CRM_CASE_NUMBER), getVisibleRenderElement(CRM_CASE_NAME));
        }
        catch (NoSuchElementException e)
        {
        }
        catch (TimeoutException e)
        {
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvanceSearchCRMPage render(long time)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvanceSearchCRMPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Enter the text value in the CrmAccountIdentifier field.
     * 
     * @param String accountIdentifier
     */
    public void inputCrmAccountId(final String accountIdentifier)
    {
        if (accountIdentifier == null || accountIdentifier.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement nameElement = findElementDisplayed(CRM_ACCOUNT_IDENTIFIER);
        nameElement.clear();
        nameElement.sendKeys(accountIdentifier);
    }

    /**
     * Enter the text value in the CrmAccountName field.
     * 
     * @param String accountName
     */
    public void inputCrmAccountName(final String accountName)
    {
        if (accountName == null || accountName.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement nameElement = findElementDisplayed(CRM_ACCOUNT_NAME);
        nameElement.clear();
        nameElement.sendKeys(accountName);
    }

    /**
     * Enter the text value in the CrmOpportunityName field.
     * 
     * @param String opportunityName
     */
    public void inputCrmOpporName(final String opporName)
    {
        if (opporName == null || opporName.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement nameElement = findElementDisplayed(CRM_OPPOR_NAME);
        nameElement.clear();
        nameElement.sendKeys(opporName);
    }

    /**
     * Enter the text value in the CrmContractNumber field.
     * 
     * @param String contractNumber
     */
    public void inputCrmContractNumber(final String contractNumber)
    {
        if (contractNumber == null || contractNumber.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement nameElement = findElementDisplayed(CRM_CONTRACT_NUMBER);
        nameElement.clear();
        nameElement.sendKeys(contractNumber);
    }

    /**
     * Enter the text value in the CrMContractName field.
     * 
     * @param String ContractName
     */
    public void inputCrmContractName(final String contractName)
    {
        if (contractName == null || contractName.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement nameElement = findElementDisplayed(CRM_CONTRACT_NAME);
        nameElement.clear();
        nameElement.sendKeys(contractName);
    }

    /**
     * Enter the text value in the CrmCaseNumber field.
     * 
     * @param String CaseNumber
     */
    public void inputCrmCaseNumber(final String caseNumber)
    {
        if (caseNumber == null || caseNumber.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement nameElement = findElementDisplayed(CRM_CASE_NUMBER);
        nameElement.clear();
        nameElement.sendKeys(caseNumber);
    }

    /**
     * Enter the text value in the CrmCaseName field.
     * 
     * @param String caseName
     */
    public void inputCrmCaseName(final String caseName)
    {
        if (caseName == null || caseName.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement nameElement = findElementDisplayed(CRM_CASE_NAME);
        nameElement.clear();
        nameElement.sendKeys(caseName);
    }

}
