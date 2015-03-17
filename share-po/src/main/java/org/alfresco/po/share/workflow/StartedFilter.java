package org.alfresco.po.share.workflow;

import org.openqa.selenium.By;

/**
 * @author Aliaksei Boole
 */
public enum StartedFilter
{
    LAST_7_DAYS("//a[@rel='last7Days']"),
    LAST_14_DAYS("//a[@rel='last14Days']"),
    LAST_28_DAYS("//a[@rel='last28Days']");

    public final By by;

    StartedFilter(String xpath)
    {
        this.by = By.xpath(xpath);
    }
}
