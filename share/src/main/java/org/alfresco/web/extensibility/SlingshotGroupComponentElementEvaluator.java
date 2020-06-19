/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
 */
package org.alfresco.web.extensibility;

import java.util.List;
import java.util.Map;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;

public class SlingshotGroupComponentElementEvaluator extends DefaultSubComponentEvaluator
{
    protected SlingshotEvaluatorUtil util = null;

    public void setSlingshotEvaluatorUtil(SlingshotEvaluatorUtil slingshotExtensibilityUtil)
    {
        this.util = slingshotExtensibilityUtil;
    }

    public static final String GROUPS = "groups";
    public static final String RELATION = "relation";
    public static final String AND = "AND";
    public static final String NEGATE = "negate";

    /**
     * Checks to see whether or not the current user satisfies the group membership requirements
     * specified.
     */
    @Override
    public boolean evaluate(RequestContext context, Map<String, String> params)
    {
        boolean memberOfAllGroups = getRelationship(context, params);
        List<String> groups = util.getGroups(params.get(GROUPS));
        boolean isMember = util.isMemberOfGroups(context, groups, memberOfAllGroups);
        boolean negate = getNegation(context, params);
        boolean apply = (isMember && !negate) || (!isMember && negate);
        return apply;
    }

    /**
     * Checks for a request for to negate the ruling. The default is false.
     * @param context RequestContext
     * @param evaluationProperties Map
     * @return boolean
     */
    protected boolean getNegation(RequestContext context, Map<String, String> evaluationProperties)
    {
        String negateParam = evaluationProperties.get(NEGATE);
        return (negateParam != null && negateParam.trim().equalsIgnoreCase(Boolean.TRUE.toString()));
    }
    
    /**
     * Gets the logical relationship between all the groups to test for membership of. By default
     * this boils down to a straight choice between "AND" (must be a member of ALL groups) and "OR"
     * (only needs to be a member of one group)
     *
     * @param context RequestContext
     * @param evaluationProperties Map
     * @return boolean
     */
    protected boolean getRelationship(RequestContext context, Map<String, String> evaluationProperties)
    {
        String relationParam = evaluationProperties.get(RELATION);
        return (relationParam != null && relationParam.trim().equalsIgnoreCase(AND));
    }
}
