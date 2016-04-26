package org.alfresco.po;

import org.openqa.selenium.WebDriver;

/**
 * Mixin webdriver interface use to access webdriver from html elements which currently
 * does not support access to webdriver.
 * 
 * @author Michael Suzuki
 *
 */
public interface WebDriverAware
{
    public void setWebDriver(WebDriver driver);
}
