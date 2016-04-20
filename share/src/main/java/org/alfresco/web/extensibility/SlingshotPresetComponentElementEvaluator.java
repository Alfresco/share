/*
 * #%L
 * Alfresco Share WAR
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.web.extensibility;

import java.util.Map;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;

/**
 * <p>
 * Evaluator used to decide if a {@code <sub-component>} shall be bound in to a {@code <component>} and {@code <@region>}.
 * </p>
 *
 * <p>
 * Returns true if we are inside a site AND that site's sitePreset matches the regexp from the {@code <sitePresets>}
 * parameter.
 * </p>
 *
 * <p>
 * Note! The regexp is expressed without using the surrounding // characters.
 * </p>
 * <p>
 * Note! The default value of the {@code <sitePresets>} parameter is ".*" which will make it match all site presets.
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
     * @param context RequestContext
     * @param params Map
     * @return true if we are in a site and its id matches the {@code <sites>} param (defaults to ".*")
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
