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
package org.alfresco.web.evaluator;

import java.util.ArrayList;

import org.alfresco.web.extensibility.SlingshotEvaluatorUtil;
import org.alfresco.web.extensibility.SlingshotGroupComponentElementEvaluator;
import org.json.simple.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;

/**
 * Determines whether or not the current user is a member of a supplied list of groups. The groups should be
 * supplied as a list parameter named "groups" and the membership relationship should indicated
 * in a String parameter "relation" that should either be "AND" or "OR" indicating whether the user must be
 * a member of all supplied groups or only needs to be a member of one of them in order for the evaluator
 * to succeed.
 *
 * @author David Draper
 */
public class HasGroupMembershipsEvaluator extends BaseEvaluator
{
    protected SlingshotEvaluatorUtil util = null;

    public void setSlingshotEvaluatorUtil(SlingshotEvaluatorUtil slingshotExtensibilityUtil)
    {
        this.util = slingshotExtensibilityUtil;
    }

    private ArrayList<String> groups;

    public void setGroups(ArrayList<String> groups)
    {
        this.groups = groups;
    }

    private String relation = SlingshotGroupComponentElementEvaluator.AND;

    public void setRelation(String relation)
    {
        this.relation = relation;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean memberOfAllGroups = (this.relation == null || this.relation.trim().equalsIgnoreCase(SlingshotGroupComponentElementEvaluator.AND));
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        boolean hasMembership = this.util.isMemberOfGroups(rc, this.groups, memberOfAllGroups);
        return hasMembership;
    }
}
