package org.alfresco.po.share.site.document;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.enums.Encoder;
import org.openqa.selenium.By;

/**
 * Create html content page object, Where user can create content.
 * 
 * @author Jamie Allison
 * @since  4.3.0
 */
public class CreateHtmlContentPage extends CreatePlainTextContentPage
{
    public CreateHtmlContentPage()
    {
        CONTENT = By.cssSelector("iframe[id$='default_prop_cm_content_ifr']");
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateHtmlContentPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateHtmlContentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    TinyMceEditor tinyMCEEditor;
    @Override
    protected void createContentField(ContentDetails details)
    {
        if (details != null && details.getContent() != null)
        {
            tinyMCEEditor.setTinyMce();
            tinyMCEEditor.setText(details.getContent(), Encoder.ENCODER_HTML);
        }
    }
}
