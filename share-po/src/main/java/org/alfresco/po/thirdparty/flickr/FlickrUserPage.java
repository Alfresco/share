package org.alfresco.po.thirdparty.flickr;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.Page;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Aliaksei Boole
 */
public class FlickrUserPage extends Page
{
    private final static By YOU_LINK = By.xpath("//a[@class='gn-link']/span[text()='You']");
    private final static By CONFIRM_AUTHORIZE_BUTTON = By.cssSelector("input[value='OK, I\\'LL AUTHORIZE IT']");
    private final static By PHOTO_STREAM_TITLE = By.xpath("//ul[contains(@class,'nav-links')]/li/a[text()='Photostream']");
    private final static String UPLOADED_FILE_XPATH = "//div[contains(@id,'photo_')]/div/div/span/a/img[contains(@alt, '%s')]";

    @SuppressWarnings("unchecked")
    @Override
    public FlickrUserPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(YOU_LINK));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FlickrUserPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public void confirmAlfrescoAuthorize()
    {
        click(CONFIRM_AUTHORIZE_BUTTON);
    }

    public boolean isFileUpload(String fileName)
    {
        click(YOU_LINK);
        waitForElement(PHOTO_STREAM_TITLE, getDefaultWaitTime());
        return driver.findElements(By.xpath(String.format(UPLOADED_FILE_XPATH, fileName))).size() > 0;
    }

    private void click(By locator)
    {
        WebElement element = findAndWait(locator);
        element.click();
    }
}
