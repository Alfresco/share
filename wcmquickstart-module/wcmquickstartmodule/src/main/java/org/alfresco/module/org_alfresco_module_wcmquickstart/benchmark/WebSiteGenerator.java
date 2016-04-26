
package org.alfresco.module.org_alfresco_module_wcmquickstart.benchmark;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author Nick Smith
 * @since 4.0
 *
 */
public interface WebSiteGenerator
{
    NodeRef generateWebSite(String siteName);

}
