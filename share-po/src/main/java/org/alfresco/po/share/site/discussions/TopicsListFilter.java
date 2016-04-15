package org.alfresco.po.share.site.discussions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author Aliaksei Boole
 */
public class TopicsListFilter extends PageElement
{
    private final static By BASE_FILTER_ELEMENT = By.xpath("//ul[@class='filterLink']");
    private final static By TAGS_LINK = By.xpath("//ul[@class='filterLink']//span[@class='tag']/a");


    public enum FilterOption
    {
        NEW(".//span[@class='new']/a"),
        MOST_ACTIVE(".//span[@class='hot']/a"),
        ALL(".//span[@class='all']/a"),
        MY_TOPICS(".//span[@class='mine']/a");

        FilterOption(String xpath)
        {
            this.by = By.xpath(xpath);
        }

        public final By by;
    }

    public TopicsListFilter(WebDriver driver)
    {
        WebElement baseWebElement = findAndWait(BASE_FILTER_ELEMENT);
        setWrappedElement(baseWebElement);
    }

    /**
     * Mimic select filter type in upper left angle
     *
     * @param option FilterOption
     */
    public DiscussionsPage select(FilterOption option)
    {
        checkNotNull(option);
        findAndWait(option.by).click();
        return factoryPage.instantiatePage(driver, DiscussionsPage.class).waitUntilAlert().render();
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

    /**
     * Mimic select tag in left filter panel.
     *
     * @param tagName DiscussionsPage
     */
    public DiscussionsPage clickOnTag(String tagName)
    {
        checkNotNull(tagName);
        List<WebElement> tagElements = driver.findElements(TAGS_LINK);
        for (WebElement tagElement : tagElements)
        {
            if (tagName.equals(tagElement.getText()))
            {
                tagElement.click();
                return factoryPage.instantiatePage(driver, DiscussionsPage.class).waitUntilAlert().render();
            }
        }
        throw new PageException(String.format("Tag with name[%s] don't found.", tagName));
    }

}
