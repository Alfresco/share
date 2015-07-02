package org.alfresco.po.share;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.WebElement;

/**
 * Represents a generic link, a warpper for {@link WebElement}
 * 
 * @author Michael Suzuki
 * @since 1.3
 */
public class ShareLink
{
    private final String description;
    private final String href;
    private final WebElement link;
    private final WebDrone drone;

    public ShareLink(final WebElement link, WebDrone drone)
    {
        this.drone = drone;
        this.link = link;
        this.href = link.getAttribute("href");
        this.description = link.getText();
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ShareLink [description=");
        builder.append(description);
        builder.append(", href=");
        builder.append(href);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Gets text value of link
     */
    public String getDescription()
    {
        return description;
    }

    public String getHref()
    {
        return href;
    }

    public WebElement getLink()
    {
        return link;
    }

    /**
     * Actions a click on the link element.
     * 
     * @return {@link HtmlPage} page response of clicking the link
     */
    public HtmlPage click()
    {
        link.click();
        return FactorySharePage.resolvePage(drone);
    }
    
    /**
     * Actions a click on the link element without verifying the page.
     * 
     */
    public void openLink()
    {
        link.click();
    }
}
