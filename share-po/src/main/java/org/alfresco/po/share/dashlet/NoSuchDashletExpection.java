
package org.alfresco.po.share.dashlet;

import org.alfresco.po.exception.PageException;

/**
 * Thrown by {@link org.alfresco.po.WebDriver} when Dashlet has not rendered in the set time.
 * 
 * @author Shan Nagarajan
 * @since 1.6
 */
public class NoSuchDashletExpection extends PageException
{

    /**
     * The Serial Version UID.
     */
    private static final long serialVersionUID = -8942012569207507506L;

    private static final String DEFAULT_MESSAGE = "Not able find the given dashlet";

    public NoSuchDashletExpection(String reason)
    {
        super(reason);
    }

    public NoSuchDashletExpection(String reason, Throwable cause)
    {
        super(reason, cause);
    }

    public NoSuchDashletExpection()
    {
        super(DEFAULT_MESSAGE);
    }

}
