package org.alfresco.po;
/**
 * A render interface that determines if elements are rendered.
 * 
 * Every page object renders itself by calling the render
 * method which defines what Page object it is, up to that point
 * it is treated as a generic page. The render signifies a response
 * is required and will force the object to wait until all HTML elements
 * specified in the render method are found and displayed.
 * 
 * @author Michael Suzuki
 * @since 1.6.3
 *
 */
public interface Render
{
    /**
     * Page renderer verifies the page has rendered
     * by checking all elements are visible and loaded.
     * 
     * To verify this assertion every page object that implements
     * the method will contain logic to determine if the page has 
     * loaded completely in the given time set by the {@link RenderTime}. 
     * 
     * @param timer {@link RenderTime} time to wait
     * @param <T> object that extends {@link HtmlPage}
     * @return {@link HtmlPage} page object response
     */
    <T extends HtmlPage> T render(final RenderTime timer);
    /**
     * Page renderer verifies the page has rendered
     * by checking java script page loaded status is complete.
     * @param <T> object that extends {@link HtmlPage}
     * @return {@link HtmlPage} object response
     */
    <T extends HtmlPage> T render();
}
