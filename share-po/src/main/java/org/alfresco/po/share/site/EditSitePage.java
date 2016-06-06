package org.alfresco.po.share.site;

import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;

/**
 * Edit site page object, holds all element of the HTML page relating to
 * share's edit site page.
 * 
 * @author Michael Suzuki
 * @since 1.5
 */
public class EditSitePage extends CreateSitePage
{
    private static final By EDIT_SITE_FORM = By.cssSelector("form#alfresco-editSite-instance-form");

    @SuppressWarnings("unchecked")
    public EditSitePage render()
    {
    	RenderTime timer = new RenderTime(maxPageLoadingTime);
    	MODERATED_CHECKBOX_HELP_TEXT = By.cssSelector("span[id$='moderated-help-text']");
        PRIVATE_CHECKBOX_HELP_TEXT = By.cssSelector("span[id$='private-help-text']");
        PUBLIC_CHECKBOX_HELP_TEXT = By.cssSelector("span[id$='public-help-text']");
        elementRender(timer, RenderElement.getVisibleRenderElement(EDIT_SITE_FORM));

        return this;
    }

}
