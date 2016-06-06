package org.alfresco.po.share.site.document;

import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.enums.ViewType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Alfresco Share factory, creates the appropriate page object that corresponds
 * to the document library view type.
 * 
 * @author Chiran
 * @since 4.3
 */
public final class FactoryShareFileDirectoryInfo
{
    /**
     * Gets the FileDirectoryInfo based on the given view type.
     * 
     * @param driver
     *            {@link org.alfresco.po.WebDriver}
     * @param viewType
     * @return FileDirectoryInfo
     */
    public static FileDirectoryInfo getPage(final String nodeRef, final WebElement webElement, final WebDriver driver, final ViewType viewType, FactoryPage factoryPage)
    {
        try
        {
            PageElement pe = null;
            switch (viewType)
            {
                case SIMPLE_VIEW:
                    pe = factoryPage.instantiatePageElement(driver, SimpleViewFileDirectoryInfo.class);
                    break;
                case DETAILED_VIEW:
                    pe = factoryPage.instantiatePageElement(driver, DetailedViewFileDirectoryInfo.class);
                    break;
                case TABLE_VIEW:
                    pe = factoryPage.instantiatePageElement(driver, DetailedTableViewFileDirectoryInfo.class);
                    break;
                default:
                    throw new PageException(String.format("%s does not match any known file directory view name", viewType.name()));
            }
            FileDirectoryInfoImpl fdi = (FileDirectoryInfoImpl)pe;
            fdi.setNodeRef(nodeRef);
            fdi.setWrappedElement(webElement);
            return fdi;
        }
        catch (Exception ex)
        {
            throw new PageException("FileDirecotyInfo View object can not be matched: " + viewType.name(), ex);
        }
    }
}
