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

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Evaluator used to decide if a {@code <sub-component>} shall be bound in to a {@code <component>} and {@code <@region>}.
 * </p>
 *
 * <p>
 * Returns true if the current User Agent matches the regexp from the {@code <useragent>} parameter.
 * </p>
 *
 * <p>
 * Note! The regexp is expressed without using the surrounding // characters.
 * </p>
 * <p>
 * Note! the default value of the {@code <useragent>} parameter is ".*" which will make it match all User Agents.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>{@code
 * <evaluator type="useragent.component.evaluator"/>
 * }</pre>
 *
 * <p>
 * Will return true, no matter what User Agent is being used.
 * </p>
 *
 * <p>
 * Example 2:
 * </p>
 *
 * <pre>{@code
 * <evaluator type="useragent.component.evaluator">
 *    <params>
 *       <useragent>MSIE\s([^;]*)</useragent>
 *    </params>
 * </evaluator>
 * }</pre>
 *
 * <p>
 * Will return true if the current User Agent matches Internet Explorer
 * </p>
 *
 * @author mikeh
 */
public class SlingshotUserAgentComponentElementEvaluator extends DefaultSubComponentEvaluator
{
    // Evaluator parameters
    public static final String USERAGENT_FILTER = "useragent";

    protected SlingshotEvaluatorUtil util = null;

    public void setSlingshotEvaluatorUtil(SlingshotEvaluatorUtil slingshotExtensibilityUtil)
    {
        this.util = slingshotExtensibilityUtil;
    }

    /**
     * Decides if the requesting User Agent matches a given expression.
     *
     * @param context RequestContext
     * @param params Map
     * @return true if the requesting User Agent matches the {@code <useragent>} param (defaults to ".*")
     */
    @Override
    public boolean evaluate(RequestContext context, Map<String, String> params)
    {
        String userAgent = util.getHeader("user-agent");
        if (userAgent != null)
        {
            Pattern p = Pattern.compile(util.getEvaluatorParam(params, USERAGENT_FILTER, ".*"));
            Matcher m = p.matcher(userAgent);
            return m.find();
        }

        // No match
        return false;
    }
}
