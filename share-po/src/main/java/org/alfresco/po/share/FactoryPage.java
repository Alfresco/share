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
package org.alfresco.po.share;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.dashlet.Dashlet;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContextAware;

/**
 * Alfresco page factory interface, creates the appropriate page object that corresponds
 * to the browser view.
 *
 * @author Michael Suzuki
 * @version 1.7.1
 */
public interface FactoryPage extends ApplicationContextAware 
{
	/**
	 * Provides a page object that corresponds to the current page.
	 * @param driver {@link WebDriver} 
	 * @return {@link HtmlPage} page representing the current page
	 */
	HtmlPage getPage(final WebDriver driver);
	/**
	 * Get dashlet by name.
	 * @param driver selenium webdriver
	 * @param name name of dashlet
	 * @return Dashlet element
	 */
	Dashlet getDashlet(final WebDriver driver, final String name);
	/**
     * Instantiates the page object matching the argument.
     * @param <T>
     *
     * @param z            {@link WebDriver}
     * @param pageClassToProxy expected Page object
     * @return {@link SharePage} page response
     * @throws Exception if error 
     */
    <T> T instantiatePage(WebDriver driver,Class<T> pageClassToProxy) throws PageException;
    /**
     * Instantiates the page elements 
     * @param driver Selenium webdriver
     * @param pageClassToProxy expected page object
     * @return page
     */
    PageElement instantiatePageElement(WebDriver driver, Class<?> pageClassToProxy);
    /**
     * Get the value of key from properties file.
     * @param key mapping to value
     */
    String getValue(String key);
}
