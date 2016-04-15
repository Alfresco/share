package org.alfresco.po.share.site.links;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.PageElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author Aliaksei Boole
 */
public class LinksListFilter extends PageElement
{

    private final static By BASE_FILTER_ELEMENT = By.xpath("//ul[@class='filterLink']");
    private final static By TAGS_LINK = By.xpath("//ul[@class='filterLink']//span[@class='tag']/a");


    public enum FilterOption
    {
        ALL_LINKS(".//span[@class='all']/a"),
        MY_LINKS(".//span[@class='user']/a"),
        RECENTLY_ADDED(".//span[@class='recent']/a");

        FilterOption(String xpath)
        {
            this.by = By.xpath(xpath);
        }

        public final By by;
    }

    public LinksListFilter(WebDriver driver)
    {
        setWrappedElement(findAndWait(BASE_FILTER_ELEMENT));
    }


    /**
     * Return List with visible tags name
     *
     * @return List<String>
     */
    public List<String> getTags()
    {
        List<String> tags = new ArrayList<String>();
        List<WebElement> tagElements = driver.findElements(TAGS_LINK);
        for (WebElement tagElement : tagElements)
        {
            tags.add(tagElement.getText());
        }
        return tags;
    }
}
