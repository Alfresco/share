package org.alfresco.po.share.exception;

/**
 * Thrown when an Unexpected Share Page is presented and the Share Action can not be performed
 * @author mbhave
 */
public class UnexpectedSharePageException extends RuntimeException
{
    private static final long serialVersionUID = -5559559799354579197L;
    private static final String DEFAULT_MESSAGE = "User not on the appropriate Share page for this Action.";

    public UnexpectedSharePageException(String reason)
    {
        super(reason);
    }

    public UnexpectedSharePageException(String reason, Throwable cause)
    {
        super(reason, cause);
    }
    
    public UnexpectedSharePageException(Object expectedSharePageName, Throwable cause)
    {
        super(String.format("%s Expected Page: %s", DEFAULT_MESSAGE, expectedSharePageName.toString()), cause);
    }

    public UnexpectedSharePageException()
    {
        super(DEFAULT_MESSAGE);
    }

}
