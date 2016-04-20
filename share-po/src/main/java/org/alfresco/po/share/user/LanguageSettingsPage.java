
package org.alfresco.po.share.user;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;

/**
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public class LanguageSettingsPage extends SharePage
{
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(LanguageSettingsPage.class);
    protected static final By LANGUAGE_DROP_DOWN = By.cssSelector("select[id$='_default-language']");
    protected static final By OK_BUTTON = By.cssSelector("button[id$='_default-button-ok-button']");
    protected static final By CANCEL_BUTTON = By.cssSelector("button[id$='_default-button-cancel-button']");

    /*
     * Render logic
     */
    @SuppressWarnings("unchecked")
    public LanguageSettingsPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(LANGUAGE_DROP_DOWN), getVisibleRenderElement(OK_BUTTON), getVisibleRenderElement(CANCEL_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LanguageSettingsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public ProfileNavigation getProfileNav()
    {
        return new ProfileNavigation(driver, factoryPage);
    }

    /**
     * Method to change the language from Language settings page
     * 
     * @param language
     * @return {@link HtmlPage}
     */
    public HtmlPage changeLanguage(Language language)
    {
        try
        {
            Select languageDropDown = new Select(driver.findElement(LANGUAGE_DROP_DOWN));
            languageDropDown.selectByValue(language.getLanguageValue());
            driver.findElement(OK_BUTTON).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to find Element", nse);
        }
        return this;
    }

    /**
     * Method to check if the given language is selected or not
     * 
     * @param language
     * @return True if the given language is selected
     */
    public boolean isLanguageSelected(Language language)
    {
        try
        {
            Select languageDropDown = new Select(driver.findElement(LANGUAGE_DROP_DOWN));
            return languageDropDown.getFirstSelectedOption().getAttribute("value").equals(language.getLanguageValue());
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to find Element", nse);
        }
    }

    /**
     * Method to get the selected language
     * 
     * @return {@link Language}
     */
    public Language getSelectedLanguage()
    {
        try
        {
            Select languageDropDown = new Select(driver.findElement(LANGUAGE_DROP_DOWN));
            return Language.getLanguageFromValue(languageDropDown.getFirstSelectedOption().getAttribute("value"));
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to find Element", nse);
        }
    }
}
