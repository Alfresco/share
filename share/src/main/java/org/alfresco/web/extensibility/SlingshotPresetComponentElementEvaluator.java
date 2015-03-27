package org.alfresco.web.extensibility;

import java.util.Map;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;

/**
 * <p>
 * Evaluator used to decide if a {@code<sub-component>} shall be bound in to a {@code<component>} and {@code<@region>}.
 * </p>
 *
 * <p>
 * Returns true if we are inside a site AND that site's sitePreset matches the regexp from the {@code<sitePresets>}
 * parameter.
 * </p>
 *
 * <p>
 * Note! The regexp is expressed without using the surrounding // characters.
 * </p>
 * <p>
 * Note! The default value of the {@code<sitePresets>} parameter is ".*" which will make it match all site presets.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <p>
 * <pre>{@code
 * <evaluator type="preset.component.evaluator">
 *    <params>
 *       <sitePresets>rm-site-dashboard</referrer>
 *    </params>
 * </evaluator>
 * }</pre>
 *
 * <p>
 * Will return true if we are inside a Records Management site (which always has a sitePreset id set to
 * "rm-site-dashboard").
 * </p>
 *
 * @author ewinlof
 */
public class SlingshotPresetComponentElementEvaluator extends DefaultSubComponentEvaluator
{
    // Evaluator Parameters
    public static final String SITE_PRESET_FILTER = "sitePresets";

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
     * @return true if we are in a site and its id matches the {@code<sites>} param (defaults to ".*")
     */
    @Override
    public boolean evaluate(RequestContext context, Map<String, String> params)
    {
        String siteId = util.getSite(context);

        // If we are in a site use site filters
        if (siteId != null)
        {
            // Yes we are in a site now check the preset
            String sitePreset = util.getSitePreset(context, siteId);
            if (sitePreset != null && sitePreset.matches(util.getEvaluatorParam(params, SITE_PRESET_FILTER, ".*")))
            {
                // Yes we are in a site with a preset that matches our filter
                return true;
            }
        }

        // No we are not in a site with a preset
        return false;
    }

}
