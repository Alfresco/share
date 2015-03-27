package org.alfresco.po.share.site.contentrule.createrules.selectors;

import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * User: aliaksei.bul Date: 08.07.13 Time: 12:12
 */
public abstract class AbstractIfSelector
{
    private WebDrone drone;

    private static final By IF_OPTIONS_SELECT = By.cssSelector("ul[id$=ruleConfigIfCondition-configs] select[class$='config-name']");
    private static final By SIMILARITY_IF_SELECT = By.cssSelector("select[param='operation']");
    private static final By IS_SELECT = By.cssSelector("select[title='is']");

    protected static final By COMPARE_FIELD = By.cssSelector("input[param='value']");
    private static final By COMPARE_DATE_FIELD = By.cssSelector("input[class='datepicker-date']");
    private static final By COMPARE_TIME_FIELD = By.cssSelector("input[class='datepicker-time']");
    private static final By SELECT_BUTTON = By.cssSelector("span[class*='has-tag'] button");

    public enum SizeCompareOption
    {
        EQUALS(0), GREATER_THAN(1), GREATER_THAN_EQUAL(2), LESS_THAN(3), LESS_THAN_EQUAL(4);

        private final int numberPosition;

        SizeCompareOption(int numberPosition)
        {
            this.numberPosition = numberPosition;
        }
    }

    public enum StringCompareOption
    {
        BEGINS(0), CONTAINS(1), ENDS(2), EQUALS(3);

        private final int numberPosition;

        StringCompareOption(int numberPosition)
        {
            this.numberPosition = numberPosition;
        }
    }

    protected AbstractIfSelector(WebDrone drone)
    {
        this.drone = drone;
    }

    public void selectIFOption(int ifOptionNumber)
    {
        List<WebElement> ifOptions = drone.findAndWaitForElements(IF_OPTIONS_SELECT);
        List<Select> ifSelects = new ArrayList<Select>();
        for (WebElement ifOption : ifOptions)
        {
            ifSelects.add(new Select(ifOption));
        }
        ifSelects.get(ifSelects.size() - 1).selectByIndex(ifOptionNumber);
    }

    private void selectIfSimilarity(int similarityNumber)
    {
        List<WebElement> similarityInStringOptions = drone.findAndWaitForElements(SIMILARITY_IF_SELECT);
        List<Select> similaritySelects = new ArrayList<Select>();
        for (WebElement similarityInStringOption : similarityInStringOptions)
        {
            similaritySelects.add(new Select(similarityInStringOption));
        }
        similaritySelects.get(similaritySelects.size() - 1).selectByIndex(similarityNumber);
    }

    public void fillField(By selector, String text)
    {
        List<WebElement> similarityStringFields = drone.findAndWaitForElements(selector);
        similarityStringFields.get(similarityStringFields.size() - 1).sendKeys(text);
    }

    protected void selectAllItems(int optionNumber)
    {
        selectIFOption(optionNumber);
    }

    protected void selectCreatedDate(int optionNumber, SizeCompareOption sizeCompareOption, String date, String time)
    {
        checkInputForDate(sizeCompareOption, date, time);
        selectIFOption(optionNumber);
        selectIfSimilarity(sizeCompareOption.numberPosition);
        fillField(COMPARE_DATE_FIELD, date);
        fillField(COMPARE_TIME_FIELD, time);
    }

    public void selectModifiedDate(int optionNumber, SizeCompareOption sizeCompareOption, String date, String time)
    {
        checkInputForDate(sizeCompareOption, date, time);
        selectIFOption(optionNumber);
        selectIfSimilarity(sizeCompareOption.numberPosition);
        fillField(COMPARE_DATE_FIELD, date);
        fillField(COMPARE_TIME_FIELD, time);
    }

    private void checkInputForDate(SizeCompareOption sizeCompareOption, String date, String time)
    {
        if (sizeCompareOption == null)
        {
            throw new UnsupportedOperationException("sizeCompareOption is required.");
        }
        if (date == null)
        {
            throw new UnsupportedOperationException("date is required.");
        }
        if (time == null)
        {
            throw new UnsupportedOperationException("date is required.");
        }
    }

    protected void selectStringCompare(int optionNumber, StringCompareOption stringCompareOption, String compareString)
    {
        checkInputForCompare(stringCompareOption, compareString);
        selectIFOption(optionNumber);
        selectIfSimilarity(stringCompareOption.numberPosition);
        fillField(COMPARE_FIELD, compareString);
    }

    protected void selectSize(int optionNumber, SizeCompareOption sizeCompareOption, String compareSize)
    {
        checkInputForCompare(sizeCompareOption, compareSize);
        selectIFOption(optionNumber);
        selectIfSimilarity(sizeCompareOption.numberPosition);
        fillField(COMPARE_FIELD, compareSize);
    }

    private void checkInputForCompare(Object option, String compareString)
    {
        if (option == null)
        {
            throw new UnsupportedOperationException("CompareOption is required.");
        }
        if (compareString == null || "".equals(compareString))
        {
            throw new UnsupportedOperationException("compareString is required");
        }
    }

    protected void selectIs(int optionNumber, String visibleText)
    {
        if (visibleText == null || "".equals(visibleText))
        {
            throw new UnsupportedOperationException("visibleText is required");
        }
        selectIFOption(optionNumber);
        List<WebElement> mimeTypeElements = drone.findAndWaitForElements(IS_SELECT);
        List<Select> mimeTypeSelects = new ArrayList<Select>();
        for (WebElement mimeTypeElement : mimeTypeElements)
        {
            mimeTypeSelects.add(new Select(mimeTypeElement));
        }
        mimeTypeSelects.get(mimeTypeSelects.size() - 1).selectByVisibleText(visibleText);
    }

    protected void selectWithButton(int optionNumber)
    {
        selectIFOption(optionNumber);
        List<WebElement> selectButtons = drone.findAndWaitForElements(SELECT_BUTTON);
        selectButtons.get(selectButtons.size() - 1).click();
        // todo need add logic for work with Popup menu
    }

}
