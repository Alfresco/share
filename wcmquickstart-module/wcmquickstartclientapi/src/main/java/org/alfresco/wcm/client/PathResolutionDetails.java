package org.alfresco.wcm.client;

public interface PathResolutionDetails
{
    Asset getAsset();

    Section getSection();

    boolean isRedirect();

    String getRedirectLocation();
}
