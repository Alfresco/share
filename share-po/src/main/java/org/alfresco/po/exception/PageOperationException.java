package org.alfresco.po.exception;
/**
 * Alfresco page operation exception thrown when an operation fail to execute
 * due page elements not being found.
 * 
 * @author Michael Suzuki
 * @version 1.7.0
 *
 */
public class PageOperationException extends RuntimeException 
{
    private static final long serialVersionUID = -3035419828674599203L;
    private static final String DEFAULT_MESSAGE = "The operation failed to complete";
    
    public PageOperationException(String reason)
    {
        super(reason);
    }

    public PageOperationException(String reason, Throwable cause)
    {
        super(reason, cause);
    }
    
    public PageOperationException()
    {
        super(DEFAULT_MESSAGE);
    }
}
