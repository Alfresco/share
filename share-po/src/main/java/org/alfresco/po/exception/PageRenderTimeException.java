package org.alfresco.po.exception;

/**
 * Throws an exception if the operation of RenderTime has exceeded
 * the max wait time.
 */
public class PageRenderTimeException extends RuntimeException
{
    private static final long serialVersionUID = 850985590207217016L;
    private static final String DEFAULT_MESSAGE = "The operation has exceded maximum wait time";
    
    public PageRenderTimeException(String reason)
    {
        super(reason);
    }

    public PageRenderTimeException(String reason, Throwable cause)
    {
        super(reason, cause);
    }
    
    public PageRenderTimeException()
    {
        super(DEFAULT_MESSAGE);
    }

}
