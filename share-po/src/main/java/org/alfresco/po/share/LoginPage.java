package org.alfresco.po.share;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * Login Page object that holds all information and methods that can be found on
 * the login page.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public class LoginPage extends SharePage
{
    @RenderWebElement 
        @FindBy(css="[id$='default-username']") 
            private TextInput usernameInput;
    @RenderWebElement
        @FindBy(css="[id$='default-password']") 
            private TextInput passwordInput;
    @RenderWebElement
        @FindBy(css="[id$='default-submit-button']") 
            private Button submit;


    /**
     * Login action, completes the login form by submitting user name and password.
     * @param username String user identifier
     * @param password String user password
     * @return Alfresco share page, Dashboard page.
     * @throws Exception if error
     */
    public HtmlPage loginAs(String username, String password) throws Exception
    {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        submit.click();
        return getCurrentPage();
    }

    /**
     * Verify if error message is displayed.
     *
     * @return true if div.bd is displayed
     */
    public boolean hasErrorMessage()
    {
        return isDisplayed(By.cssSelector("div.error"));
    }

    public String getErrorMessage()
    {
        return driver.findElement(By.cssSelector("div.error")).getText();
    }
}
