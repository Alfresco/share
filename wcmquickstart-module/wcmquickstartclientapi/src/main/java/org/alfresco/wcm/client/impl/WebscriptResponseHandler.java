package org.alfresco.wcm.client.impl;

import java.io.InputStream;

public interface WebscriptResponseHandler
{
    void handleResponse(InputStream in);
}
