package org.alfresco.web.resolver.doclib;

import java.util.HashMap;

/**
 * Resolves which data url if to use when asking the repository for nodes in the document library's document list.
 *
 * @author ewinlof
 */
public interface DoclistDataUrlResolver
{
    /**
     * Returns the url to the repository doclist webscript to use.
     *
     * @param webscript The repository doclib2 webscript tp use, i.e. doclist or node
     * @param params doclib2 webscript specific parameters
     * @param args url parameters, i.e. pagination parameters
     * @return The url to use when asking the repository doclist webscript.
     */
    public String resolve(String webscript, String params, HashMap<String, String> args);
}
