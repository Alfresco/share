package org.alfresco.po.share.user;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object to reflect Edit user profile page
 *
 * @author Marina.Nenadovets
 */
public class EditProfilePage extends SharePage
{
    private static Log logger = LogFactory.getLog(EditProfilePage.class);

    private static final By SAVE_CHANGES = By.cssSelector("button[id$=default-button-save-button]");
    private static final By UPLOAD_AVATAR_BUTTON = By.xpath("//button[contains(@id,'-button-upload-button')]");
    private static final By CANCEL_BUTTON = By.xpath("//button[contains(@id,'-button-cancel-button')]");
    private final static By lastName = By.cssSelector ("input[id$='-input-lastName']");

    /*
     * Render logic
     */
    @SuppressWarnings("unchecked")
    public EditProfilePage render(RenderTime timer)
    {
        elementRender(timer, RenderElement.getVisibleRenderElement(SAVE_CHANGES));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditProfilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Upload new avatar image.
     *
     * @param file
     */
    public void uploadAvatar(File file)
    {
        WebElement uploadButton = findAndWait(UPLOAD_AVATAR_BUTTON);
        uploadButton.click();
        UploadFilePage uploadFilePage = factoryPage.instantiatePage(driver, UploadFilePage.class);
        uploadFilePage.upload(file.getAbsolutePath());
        findAndWait(SAVE_CHANGES).click();
        logger.info("Avatar[" + file.getName() + "] uploaded.");
    }

    public HtmlPage clickCancel()
    {
        findAndWait(CANCEL_BUTTON).click();
        return getCurrentPage();
    }

    private void click(By locator)
    {
        checkNotNull(locator);
        WebElement element = findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = findAndWait(selector);
        inputField.clear();
        if (text != null)
        {
            inputField.sendKeys(text);
        }
    }

    public HtmlPage editLastName (String newLastName)
    {
        fillField(lastName, newLastName );
        click(SAVE_CHANGES);
        return getCurrentPage();
    }
}
