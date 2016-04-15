package org.alfresco.po.share.dashlet.sitecontent;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.openqa.selenium.WebDriver;

/**
 * Holds the information about the Simple View inside site content dashlet.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class SimpleViewInformation
{

    private ShareLink thumbnail;
    private ShareLink contentDetail;
    private String contentStatus;
    private ShareLink user;
    private boolean previewDisplayed;
    private WebDriver driver;
    private FactoryPage factoryPage;

    public SimpleViewInformation(WebDriver driver,
                                 final ShareLink thumbnail,
                                 final ShareLink contentDetail,
                                 final ShareLink user,
                                 final String contentStatus,
                                 final boolean previewDisplayed,
                                 FactoryPage factoryPage)
    {

        if (null == driver)
        {
            throw new UnsupportedOperationException("Drone is required, It can't be null.");
        }

        if (null == thumbnail)
        {
            throw new UnsupportedOperationException("Thumbnail link is required");
        }

        if (null == contentDetail)
        {
            throw new UnsupportedOperationException("Content Details link is required");
        }

        if (null == user)
        {
            throw new UnsupportedOperationException("User link is required");
        }

        this.driver = driver;
        this.thumbnail = thumbnail;
        this.contentDetail = contentDetail;
        this.user = user;
        this.contentStatus = contentStatus;
        this.previewDisplayed = previewDisplayed;
        this.factoryPage = factoryPage;
    }

    public ShareLink getThumbnail()
    {
        return thumbnail;
    }

    public ShareLink getContentDetail()
    {
        return contentDetail;
    }

    public ShareLink getUser()
    {
        return user;
    }

    public String getContentStatus()
    {
        return contentStatus;
    }

    public boolean isPreviewDisplayed()
    {
        return previewDisplayed;
    }

    /**
     * Mimics the action clicking the document link.
     * 
     * @return {@link DocumentDetailsPage}
     */
    public HtmlPage clickContentDetail()
    {
        this.contentDetail.click();
        return factoryPage.instantiatePage(driver, DocumentDetailsPage.class);
    }

    /**
     * Mimics the actions Click User Profile.
     * 
     * @return {@link MyProfilePage}
     */
    public HtmlPage clickUser()
    {
        this.user.click();
        return factoryPage.instantiatePage(driver, MyProfilePage.class);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleViewInformation [thumbnail=");
        builder.append(thumbnail.toString());
        builder.append(", contentDetail=");
        builder.append(contentDetail.toString());
        builder.append(", contentStatus=");
        builder.append(contentStatus);
        builder.append(", user=");
        builder.append(user.toString());
        builder.append(", previewDisplayed=");
        builder.append(previewDisplayed);
        builder.append("]");
        return builder.toString();
    }
}
