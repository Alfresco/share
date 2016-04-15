package org.alfresco.wcm.client.exception;

/**
 * This exception indicates that an asset, template etc has not been found for the requested URL
 * @author Chris Lack
 */
public class PageNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = 126826782878L;

    public PageNotFoundException(String message)
    {
        super(message);
    }
    
}
