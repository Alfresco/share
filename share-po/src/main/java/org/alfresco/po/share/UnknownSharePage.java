package org.alfresco.po.share;

import org.alfresco.po.HtmlPage;
import org.openqa.selenium.WebDriver;

/**
 * An unknown page that will, at time of {@link #render()} produce a strongly-typed page.
 * <p/>
 * By using this page, source pages do not need to be responsible for determining the target page in functional methods.
 * 
 * <pre>
 *      public HtmlPage selectItem(Integer number)
 *     {
 *             ...
 *             item.click();
 *             ...
 *         return FactorySharePage.getUnknownPage(driver);
 *     }
 * </pre>
 * 
 * @author Derek Hulley
 * @since 1.8.0
 * @see FactorySharePage#getUnknownPage(WebDriver)
 */
public class UnknownSharePage extends SharePage
{

    /**
     * @see factoryPage.getPage(WebDriver)
     * @return the real page based on what is on the browser
     */
    private HtmlPage getActualPage()
    {
        return getCurrentPage();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends HtmlPage> T render()
    {
        HtmlPage actualPage = getActualPage();
        if (actualPage instanceof UnknownSharePage)
        {
            return (T) actualPage;
        }
        return (T) actualPage.render();
    }

}
