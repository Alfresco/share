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

import org.json.simple.JSONObject;

/**
 * Contract supported by all classes that provide dynamic evaluation for a UI element.
 * <p>
 * Evaluators are supplied with a Node instance context object.
 * <p>
 * The evaluator should decide if the precondition is valid based on the appropriate
 * logic and the properties etc. and return the result.
 *
 * @author mikeh
 */
public interface Evaluator
{
    /**
     * The evaluator should decide if the precondition is valid based on the appropriate
     * logic and the state etc. of the given object and return the result.
     *
     * @param jsonObject     The record the evaluation is for
     * @return result of whether the evaluation succeeded or failed.
     */
    public boolean evaluate(JSONObject jsonObject);
}
