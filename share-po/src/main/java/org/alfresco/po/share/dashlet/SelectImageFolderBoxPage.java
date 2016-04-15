package org.alfresco.po.share.dashlet;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;

/**
 * Page object holds the elements of Select Image box
 *
 * @author Marina.Nenadovets
 */
public class SelectImageFolderBoxPage extends SharePage
{
    private static final By DESTINATION_CONTAINER = By.cssSelector("div[id$='default-rulesPicker-modeGroup']");
    private static final By SITES_CONTAINER = By.cssSelector("div[id$='default-rulesPicker-sitePicker']");
    private static final By PATH_CONTAINER = By.cssSelector("div[id$='default-rulesPicker-treeview']");
    private static final By OK_BUTTON = By.cssSelector("button[id$='default-rulesPicker-ok-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='default-rulesPicker-cancel-button']");
    private static final By CLOSE_BUTTON = By.cssSelector(".container-close");

    @SuppressWarnings("unchecked")
    @Override
    public SelectImageFolderBoxPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(DESTINATION_CONTAINER),
                getVisibleRenderElement(SITES_CONTAINER),
                getVisibleRenderElement(PATH_CONTAINER),
                getVisibleRenderElement(OK_BUTTON),
                getVisibleRenderElement(CANCEL_BUTTON),
                getVisibleRenderElement(CLOSE_BUTTON));
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectImageFolderBoxPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public void clickCancel()
    {
        findAndWait(CANCEL_BUTTON).click();
    }
}
