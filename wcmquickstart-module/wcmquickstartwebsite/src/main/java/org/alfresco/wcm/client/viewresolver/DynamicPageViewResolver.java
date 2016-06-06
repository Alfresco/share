package org.alfresco.wcm.client.viewresolver;

import java.util.Locale;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.exception.EditorialException;
import org.alfresco.wcm.client.view.StreamedAssetView;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.mvc.AbstractWebFrameworkViewResolver;
import org.springframework.extensions.surf.mvc.PageView;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Page;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * View resolver that uses the template defined for a page in the repository.
 * 
 * @author Chris Lack
 */

public class DynamicPageViewResolver extends AbstractWebFrameworkViewResolver
{
    public final static String RAW_TEMPLATE_NAME = "raw";

    public DynamicPageViewResolver()
    {
        // The default caching provided by the AbstractCachingViewResolver class
        // from which this
        // class is derived
        setCache(false);
    }

    /**
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#canHandle(java.lang.String,
     *      java.util.Locale)
     */
    protected boolean canHandle(String viewName, Locale locale)
    {
        // This view resolver is a catch-all which will handle all
        // remaining urls that correspond to an asset.
        RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
        Asset asset = (Asset) requestContext.getValue("asset");
        return asset != null;
    }

    /**
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#buildView(java.lang.String)
     */
    protected AbstractUrlBasedView buildView(String viewName) throws Exception
    {
        AbstractUrlBasedView view = null;
        RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
        Asset asset = (Asset) requestContext.getValue("asset");

        // Check if the template page name has been supplied as a URL parameter
        String template = requestContext.getParameter("view");
        if (template == null)
        {
            // else ask the asset what its template should be
            template = asset.getTemplate();
        }
        
        if (template == null || RAW_TEMPLATE_NAME.equalsIgnoreCase(template))
        {
            // If the asset doesn't have any template specified or explicitly
            // specifies the "raw" template
            // then we will stream the asset content directly.
            boolean attach = Boolean.parseBoolean(requestContext.getParameter("attach"));
            view = new StreamedAssetView(asset, attach);
        }
        else
        {
            Page page = lookupPage(template);
            if (page == null)
            {
                throw new EditorialException("Invalid template page \"" + template + "\" specified for "
                        + asset.getContainingSection().getPath() + asset.getName(), "template.none", asset
                        .getContainingSection().getPath()
                        + asset.getName() + asset.getName());
            }
            PageView pageView = new PageView(getServiceRegistry());
            pageView.setUrl(template);
            pageView.setPage(page);
            view = pageView;
        }
        return view;
    }

}
