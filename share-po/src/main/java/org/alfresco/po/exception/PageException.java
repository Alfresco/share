package org.alfresco.po.exception;

/**
 * Thrown by when page has not rendered in the set time.
 */
public class PageException extends RuntimeException
{
    private static final long serialVersionUID = 850985590207217016L;
    private static final String DEFAULT_MESSAGE = "Page has not rendered in time";
    
    public PageException(String reason)
    {
        super(reason);
    }

    public PageException(String reason, Throwable cause)
    {
        super(reason, cause);
    }
    
    public PageException()
    {
        super(DEFAULT_MESSAGE);
    }

}
