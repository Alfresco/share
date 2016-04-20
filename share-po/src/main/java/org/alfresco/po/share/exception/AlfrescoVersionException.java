package org.alfresco.po.share.exception;

/**
 * Alfresco version exception, thrown when an operation fail to execute
 * due an unsupported operation in that version.
 * 
 * @author Michael Suzuki
 * @version 1.7.0
 */
public class AlfrescoVersionException extends RuntimeException
{
    private static final long serialVersionUID = -5035419828674599203L;
    private static final String DEFAULT_MESSAGE = "This operation is not supported in this version";

    public AlfrescoVersionException(String reason)
    {
        super(reason);
    }

    public AlfrescoVersionException(String reason, Throwable cause)
    {
        super(reason, cause);
    }

    public AlfrescoVersionException()
    {
        super(DEFAULT_MESSAGE);
    }
}
