package org.alfresco.po;

/**
 * An HTML page interface, the basic shell of an HTML page.
 * Every page object renders itself by calling the render
 * method which defines what Page object it is, up to that point
 * it is treated as a generic page. The render signifies a response
 * is required and will force the object to wait until all HTML elements
 * specified in the render method are found and displayed.
 * 
 * @author Michael Suzuki
 * @since 1.0
 *
 */
public interface HtmlPage extends Render
{
    /**
     * Page title.
     * @return String page title
     */
    String getTitle();
    /**
     * Close browser action.
     */
    void close();
}
