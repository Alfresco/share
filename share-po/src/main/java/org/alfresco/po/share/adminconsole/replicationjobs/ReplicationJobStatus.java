package org.alfresco.po.share.adminconsole.replicationjobs;

import org.openqa.selenium.By;

/**
 * enum that holds job statuses
 *
 * @author Marina.Nenadovets
 */
public enum ReplicationJobStatus
{
    COMPLETED("completed", " .completed"),
    FAILED("failed", " .failed"),
    CANCELLED("cancelled", " .cancelled"),
    CANCEL_REQUESTED("cancelrequested", " .cancelrequested"),
    RUNNING("running", " .running"),
    NEW("new", " .new"),
    PENDING ("pending", " .pending");

    private String value;
    private String cssSelector;

    ReplicationJobStatus(String value, String cssSelector)
    {
        this.value = value;
        this.cssSelector = cssSelector;
    }
    public String getCssSelector()
    {
        return cssSelector;
    }

    public String getValue()
    {
        return value;
    }
}
