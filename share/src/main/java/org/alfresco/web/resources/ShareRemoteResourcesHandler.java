package org.alfresco.web.resources;

import org.springframework.extensions.surf.RemoteResourcesHandler;

/**
 * <p>This class extends the default Surf {@link RemoteResourcesHandler} to process the supplied path.
 * It has been provided primarily to provide support for AMD requests. It converts the AMD path
 * to a location in the Data Dictionary.</p>
 */
public class ShareRemoteResourcesHandler extends RemoteResourcesHandler
{
    /**
     * This represents the prefix where the AMD resources will be stored. We need
     * to add this as a prefix to each resource request to ensure that look within the 
     * Data Dictionary. This assumes that the Repository's ShareResources WebScript controller
     * is configured to use "Company Home" as a root (which it is by default).
     */
    private String repositoryPrefix;
    
    public String getRepositoryPrefix()
    {
        return repositoryPrefix;
    }

    public void setRepositoryPrefix(String repositoryPrefix)
    {
        this.repositoryPrefix = repositoryPrefix;
    }

    /**
     * This represents the filter that MUST be on the path in order for the request to 
     * have reached this handler (this is the default Spring configured filter - if its
     * changed then this will need to be updated)
     * 
     * TODO: Can we get this value from the Surf or Share configuration? 
     * It should be possible to look up the "alfresco" package in the Surf configuration
     */
    public static final String FILTER = "js/alfresco/";
    
    public static final int FILTER_LENGTH = FILTER.length();
    
    @Override
    protected String processPath(String path)
    {
        StringBuilder processedPath = new StringBuilder(this.getRepositoryPrefix());
        if (path.startsWith(FILTER))
        {
            processedPath.append(path.substring(FILTER_LENGTH));
        }
        return processedPath.toString();
    }
}
