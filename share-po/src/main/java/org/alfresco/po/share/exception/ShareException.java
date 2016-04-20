package org.alfresco.po.share.exception;

/**
 * Thrown when handling the Share Error Message (Popup) by {@link ShareException}.
 */
public class ShareException extends RuntimeException
{
    private static final long serialVersionUID = 850985590207217016L;
    private static final String DEFAULT_MESSAGE = "Share Error: Failed to create entity, Entity already exists";

    public ShareException(String reason)
    {
        super(reason);
    }

    public ShareException(String reason, Throwable cause)
    {
        super(reason, cause);
    }

    public ShareException()
    {
        super(DEFAULT_MESSAGE);
    }

}
