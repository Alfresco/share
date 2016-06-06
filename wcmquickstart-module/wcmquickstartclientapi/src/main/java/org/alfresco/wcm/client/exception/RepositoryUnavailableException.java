package org.alfresco.wcm.client.exception;

/**
 * This exception indicates that the repository is not currently available.
 * @author Chris Lack
 */
public class RepositoryUnavailableException extends RuntimeException
{
    private static final long serialVersionUID = 3686782878L;

    public RepositoryUnavailableException(Throwable t)
    {
    	super(t);
    }
}
