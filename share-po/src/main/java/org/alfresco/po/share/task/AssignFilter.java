package org.alfresco.po.share.task;

import org.openqa.selenium.By;

/**
 * @author Aliaksei Boole
 */
public enum AssignFilter
{
    ME("//a[@rel='me']"),
    UNASSIGNED("//a[@rel='unassigned']");

    AssignFilter(String xpath)
    {
        this.by = By.xpath(xpath);
    }

    public final By by;
}
