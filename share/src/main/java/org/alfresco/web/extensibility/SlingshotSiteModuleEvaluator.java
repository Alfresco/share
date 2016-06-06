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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensionModuleEvaluator;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * Evaluator used to decide if an extension module (and its {@code <components>} & {@code <customizations>}) shall be
 * used for this request.
 * </p>
 *
 * <p>
 * Makes it possible to decide if we are viewed specific sites based on their ids and sitePreset ids by matching them
 * against the regexps inside the {@code <sites>} and {@code <sitePresets>} parameters and the comma separated groups list
 * in the {@code <groups>} parameter. The {@code <groupsRelation>} parameter decides if the groups list shall be
 * matched using "and" or "or", allowed values are: AND and OR.
 * </p>
 *
 * <p>
 * Note! If we are outside a side (i.e. a "global/non-site-page"  page: i.e. the "Repository browser", A users dashboard or the
 * "My Workflows" page the evaluator will return <code>true</code> by default. To change this behaviour you can set
 * {@code <applyForNonSites>} to false, which means the evaluator will return true ONLY when inside a site.
 * Note that the {@code <groups>} parameter still applies even
 * </p>
 * <p>
 * Note! The regexp is expressed without using the surrounding // characters.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>{@code
 * <evaluator type="site.module.evaluator">
 *    <params>
 *       <sitePresets>rm-site-dashboard</sitePresets>
 *       <applyForNonSites>false</applyForNonSites>
 *     </params>
 * </evaluator>
 * }</pre>
 *
 * <p>
 * Will return true if we are inside a site with a sitePreset id of "rm-site-dashboard".
 * </p>
 *
 * <p>
 * Example 2:
 * </p>
 *
 * <pre>{@code
 * <evaluator type="site.module.evaluator">
 *    <params>
 *       <sites>rm|photos</sitePresets>
 *    </params>
 * </evaluator>
 * }</pre>
 *
 * <p>
 * Will return true if we are inside a site with a site id of "rm" or "photos" OR if we are on a global page.
 * </p>
 *
 *
 * <p>
 * Example 3:
 * </p>
 *
 * <pre>{@code
 * <evaluator type="site.module.evaluator">
 *    <params>
 *       <sites>.*</sites>
 *       <applyForNonSites>false</applyForNonSites>
 *       <groups>SiteManager,SiteCollaborator</groups>
 *       <groupsRelation>OR</groupsRelation>
 *    </params>
 * </evaluator>
 * }</pre>
 *
 * <p>
 * Will return true as long as we are in a site and the user is a SiteManager or SiteCollaborator.
 * </p>
 *
 * @author ewinlof
 */
public class SlingshotSiteModuleEvaluator implements ExtensionModuleEvaluator
{
    private static Log logger = LogFactory.getLog(SlingshotSiteModuleEvaluator.class);

    /* Evaluator parameters */
    public static final String SITE_PRESET_FILTER = "sitePresets";
    public static final String SITE_FILTER = "sites";
    public static final String APPLY_FOR_NON_SITES = "applyForNonSites";
    public static final String GROUPS = "groups";
    public static final String GROUPS_RELATION = "groupsRelation";
    public static final String GROUPS_RELATION_AND = "AND";

    protected SlingshotEvaluatorUtil util = null;

    public void setSlingshotEvaluatorUtil(SlingshotEvaluatorUtil slingshotExtensibilityUtil)
    {
        this.util = slingshotExtensibilityUtil;
    }

    public String[] getRequiredProperties()
    {
        String[] properties = new String[2];
        properties[0] = SITE_PRESET_FILTER;
        properties[1] = SITE_FILTER;
        return properties;
    }

    /**
     * Will return true if we are outside a site OR inside a site with a sitePreset id of "rm-site-dashboard".
     *
     * @param context RequestContext
     * @param params Map
     * @return true if we are outside a site OR inside a site with a sitePreset id of "rm-site-dashboard".
     */
    public boolean applyModule(RequestContext context, Map<String, String> params)
    {
        String siteId = util.getSite(context);

        // If we are in a site use site filters
        if (siteId != null)
        {
            // Test site filter
            if (!siteId.matches(util.getEvaluatorParam(params, SITE_FILTER, ".*")))
            {
                return false;
            }

            // Test silePresets filter
            String sitePreset = util.getSitePreset(context, siteId);
            if (sitePreset == null || !sitePreset.matches(util.getEvaluatorParam(params, SITE_PRESET_FILTER, ".*")))
            {
                return false;
            }

            // Test groups filter
            if (!isUserInGroups(context, params))
            {
                return false;
            }

            // SITE PASSED BOTH SITE ID & SITE PRESET & GROUP FILTERS
            return true;
        }

        // We are not in a site, test if we shall apply the module anyway
        if (!util.getEvaluatorParam(params, APPLY_FOR_NON_SITES, "true").equals("true"))
        {
            return false;
        }

        // Test groups filter
        if (!isUserInGroups(context, params))
        {
            return false;
        }

        return true;
    }

    /**
     * Checks to see whether or not the current user satisfies the group membership requirements
     * specified.
     *
     * @param context RequestContext
     * @param params Map
     * @return true if groups param is empty or user is a member of the specified groups (honouring the groupsRelation parameter)

     */
    protected boolean isUserInGroups(RequestContext context, Map<String, String> params)
    {
        String groupsParam = util.getEvaluatorParam(params, GROUPS, ".*");
        if (groupsParam.equals(".*"))
        {
            // Any group is fine, no need to test
            return true;
        }

        String relationParam = params.get(GROUPS_RELATION);
        boolean memberOfAllGroups = (relationParam != null && relationParam.trim().equalsIgnoreCase(GROUPS_RELATION_AND));
        List<String> groups = util.getGroups(groupsParam);
        return util.isMemberOfGroups(context, groups, memberOfAllGroups);
    }

}
