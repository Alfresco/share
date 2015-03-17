package org.alfresco.po.share.wqs;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;

public class WcmqsLoginPage extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final By USERNAME_INPUT = By.cssSelector("input[id='wef-login-panel-username']");
    private static final By PASSWORD_INPUT = By.cssSelector("input[id='wef-login-panel-password']");
    private static final By LOGIN_BUTTON = By.cssSelector("button[id='wef-login-panel-btn-login-button']");

    /**
     * Constructor.
     */
    public WcmqsLoginPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsLoginPage render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(USERNAME_INPUT), getVisibleRenderElement(PASSWORD_INPUT), getVisibleRenderElement(LOGIN_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsLoginPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsLoginPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for input username
     * 
     * @param userName
     * @return
     */
    public void inputUserName(String userName)
    {
        drone.findAndWait(USERNAME_INPUT).clear();
        drone.findAndWait(USERNAME_INPUT, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).sendKeys(userName);
    }

    /**
     * Method for input password
     * 
     * @param password
     * @return
     */
    public void inputPassword(String password)
    {
        drone.findAndWait(PASSWORD_INPUT).clear();
        drone.findAndWait(PASSWORD_INPUT, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).sendKeys(password);
    }

    /**
     * Method for click Login Button
     * 
     * @param
     * @return
     */

    public void clickLoginButton()
    {
        drone.findAndWait(LOGIN_BUTTON).click();
    }

    /**
     * Method for login to Alfresco Web Editor
     * 
     * @param userName
     * @param password
     * @return My Alfresco Page
     */
    public void login(String userName, String password)
    {
        try
        {
            logger.info("Login to Alfresco Web Editor");
            inputUserName(userName);
            inputPassword(password);
            clickLoginButton();

        }
        catch (UnsupportedOperationException uso)
        {
            throw new PageOperationException("Can not navigate to Wcmqs Home Page");
        }
    }

}
