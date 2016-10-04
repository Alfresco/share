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
package org.alfresco.po.share.search;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.FactoryPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FacetedSearchBulkActions extends PageElement

{
	private static final By SELECTED_ITEMS = By.cssSelector(".dijitDisabled");
	private static final By SELECTED_ITEMS_MENU = By.cssSelector("#SELECTED_ITEMS_MENU>span[class$='arrow']");
	private static final By SELECT_DD_MENU = By.cssSelector("#SELECTED_LIST_ITEMS span[class$='arrow']");
	private static final By SELECT_LIST_MENU = By.cssSelector("#SELECTED_LIST_ITEMS");
	Log logger = LogFactory.getLog(this.getClass());

    private WebElement selectActionMenu;
    private WebElement selectItems;
    private WebElement selectedItems;
    private WebElement selectListMenu;
    
	    /**
	     * Instantiates a new faceted search form.
	     */
	    public FacetedSearchBulkActions(WebDriver driver, FactoryPage factoryPage)
	    {
	        this.driver = driver;
	        this.factoryPage = factoryPage;
	        this.selectActionMenu = driver.findElement(SELECTED_ITEMS_MENU);
	        this.selectItems = driver.findElement(SELECT_DD_MENU);
	        this.selectListMenu = driver.findElement(SELECT_LIST_MENU);

	    }
	    
	/**
	 * Mimics the action of Selected Items.
	 * 
	 * @return {@link FacetedSearchResult}
	 */
	public HtmlPage clickSelectedItems() 
	{
		try 
		{
			if (selectActionMenu.isEnabled()) 
			{
				selectActionMenu.click();
				return getCurrentPage();
			}
			
			throw new PageException("Selected Items Button found, but is not enabled please select one or more item");
		} 
		catch (TimeoutException e) 
		{
			logger.error("Selected Item not available : " + SELECTED_ITEMS_MENU.toString());
					
			throw new PageException("Not able to find the Selected Items drop down menu.", e);				
		}
	}

	/**
	 * @return true is Selected Item Menu Visible, else false.
	 */
	public boolean isSelectedItemsMenuEnabled() 
	{
		try 
		{			
			if(selectActionMenu.isDisplayed() && selectActionMenu.isEnabled())	
			
		    return true;			
			
		} 
		catch (TimeoutException e) 
		{
		}
		return false;
	}
	
	public boolean isSelectedItemsMenuDisabled() 
	{
		try 
		{			
			selectedItems = driver.findElement(SELECTED_ITEMS);
			if(selectedItems.isDisplayed())				
		    return true;			
			
		} 
		catch (TimeoutException e) 
		{
		}
		return false;
	}	
		
	/**
	 * Checks if Options menu item is displayed.
	 * 
	 * @return true if visible
	 */
	public boolean isSelectedItemsOptionDisplayed(SearchSelectedItemsMenu option) 
	{
		try 
		{
			clickSelectedItems();
			if (isSelectedItemsMenuEnabled()) 
			{
				WebElement bulkSelect = driver.findElement(By.cssSelector(option.getOption()));
				return bulkSelect.isDisplayed();
			}
		} 
		catch (NoSuchElementException nse) 
		{
			if (logger.isTraceEnabled()) 
			{
				logger.trace("Option is not present. ", nse);
			}
		}
		return false;
	}
	
	/**
     * Mimics the action select any option from selected Items Menu
     * 
     * @return {@link FacetedSearchResult}
     */
    public HtmlPage selectActionFromSelectedItemsMenu(SearchSelectedItemsMenu option)
    {
        try
        {
        	clickSelectedItems();
        	if (isSelectedItemsMenuEnabled()) 
        	{            
                driver.findElement(By.cssSelector(option.getOption())).click();
                return factoryPage.getPage(driver);
            }    
            throw new PageException("Select Option not visible");            
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the option";
            logger.error(exceptionMessage, e);
            throw new PageException(exceptionMessage);
        }
    }

	
    /**
	 * Mimics the action of Select Drop Down Menu
	 * 
	 * @return {@link FacetedSearchResult}
	 */	
	private HtmlPage clickSelectDropDownMenu() 
	{
		try {

			if(selectListMenu.isDisplayed() && selectListMenu.isEnabled() )
			{
				selectItems.click();
				return getCurrentPage();
			}
			throw new PageException("Select drop down menu found, but is not enabled please select one or more item");
		} 
		catch (TimeoutException e) 
		{
			logger.error("Select Item not available : " + SELECT_DD_MENU.toString());
			throw new PageException("Not able to find the Select Item DD Menu.", e);
		}
	}

	/**
	 * @return true is Select Drop Down Menu Visible, else false.
	 */
	public boolean isSelectMenuEnabled() 
	{
		try 
		{
			if(selectListMenu.isDisplayed() && selectListMenu.isEnabled() )
			return true;
		} 
		catch (NoSuchElementException nse) 
		{
		}
		return false;
	}

	/**
	 * Checks if Options menu item is displayed.
	 * 
	 * @return true if visible
	 */
	public boolean isSelectOptionEnabled(BulkSelectCheckBox option) 
	{
		try 
		{
			clickSelectDropDownMenu();
			if (isSelectMenuEnabled()) 
			{
            	WebElement bulkSelect = driver.findElement(By.cssSelector(option.getOption()));
				return bulkSelect.isEnabled();
			}
		} 
		catch (NoSuchElementException nse) 
		{
			if (logger.isTraceEnabled()) 
			{
				logger.trace("Option is not present. ", nse);
			}
		}
		return false;
	}
	
	/**
     * Mimics the action select any option (All/None/Invert) from drop down menu.
     * 
     * @return {@link FacetedSearchResult}
     */
    public HtmlPage bulkSelect(BulkSelectCheckBox option)
    {
        try
        {
        	clickSelectDropDownMenu();
            if (isSelectMenuEnabled())
            {
            	WebElement bulkSelect = driver.findElement(By.cssSelector(option.getOption()));
				bulkSelect.click();
                return getCurrentPage();
            }
            else
            {
                throw new PageException("Select dropdown menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find select All option";
            logger.error(exceptionMessage, e);
            throw new PageException(exceptionMessage);
        }
    }     

}