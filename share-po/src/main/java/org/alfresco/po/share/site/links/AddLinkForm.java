package org.alfresco.po.share.site.links;

import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Button;

@FindBy(tagName="form")
/**
 * Page object to represent Add Link Form
 *
 * @author Marina.Nenadovets
 */
public class AddLinkForm extends AbstractLinkForm
{

    @FindBy(css="button[id$='default-ok-button']") Button save;
    /**
     * Method for clicking Save button on Add Link form
     *
     * @return LinksDetailsPage
     */
    public void clickSaveBtn()
    {
        save.click();
    }
}
