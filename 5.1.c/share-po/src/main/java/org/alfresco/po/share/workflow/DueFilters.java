package org.alfresco.po.share.workflow;

import org.openqa.selenium.By;

/**
 * @author Aliaksei Boole
 */
public enum DueFilters
{
    TODAY("//a[@rel='today']"),
    TOMORROW("//a[@rel='tomorrow']"),
    NEXT_7_DAYS("//a[@rel='next7Days']"),
    OVERDUE("//a[@rel='overdue']"),
    NO_DATE("//a[@rel='noDate']");

    public final By by;

    DueFilters(String xpath)
    {
        this.by = By.xpath(xpath);
    }
}
