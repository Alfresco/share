package org.alfresco.web.extensibility;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;

import java.util.Map;

/**
 * <p>
 * Evaluator used to decide if a {@code<sub-component>} shall be bound in to a {@code<component>} and {@code<@region>}.
 * </p>
 *
 * <p>
 * Returns true if we are inside a site's id matches the regexp from the {@code<pages>} parameter.
 * </p>
 *
 * <p>
 * Note! The regexp is expressed without using the surrounding // characters.
 * </p>
 * <p>
 * Note! the  value of the {@code<pages>} parameter is ".*" which will make it match all page's ids.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>{@code
 * <evaluator type="page.component.evaluator"/>
 * }</pre>
 *
 * <p>
 * Will return true as long as there is a page id defined.
 * </p>
 *
 * <p>
 * Example 2:
 * </p>
 *
 * <pre>{@code
 * <evaluator type="page.component.evaluator">
 *    <params>
 *       <sites>foo|bar</referrer>
 *    </params>
 * </evaluator>
 * }</pre>
 *
 * <p>
 * Will return true if we are inside a page with an id of "foo" or "bar".
 * </p>
 *
 * @author ewinlof
 */
public class SlingshotPageComponentElementEvaluator extends DefaultSubComponentEvaluator
{
    // Evaluator parameters
    public static final String PAGE_FILTER = "pages";

    protected SlingshotEvaluatorUtil util = null;

    public void setSlingshotEvaluatorUtil(SlingshotEvaluatorUtil slingshotExtensibilityUtil)
    {
        this.util = slingshotExtensibilityUtil;
    }

    /**
     * Decides if we are inside a site or not.
     *
     * @param context
     * @param params
     * @return true if we are on a page with an id that matches the {@code<pages>} param (defaults to ".*")
     */
    @Override
    public boolean evaluate(RequestContext context, Map<String, String> params)
    {
        String pageId = util.getPageId(context);

        // If we are in a site use site filters
        if (pageId != null && pageId.matches(util.getEvaluatorParam(params, PAGE_FILTER, ".*")))
        {
            return true;
        }

        // The page id didn't match the page filter
        return false;
    }

}
