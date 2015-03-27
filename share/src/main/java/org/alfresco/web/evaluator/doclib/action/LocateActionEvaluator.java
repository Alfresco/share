/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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

package org.alfresco.web.evaluator.doclib.action;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

/**
 * Evaluator for the Locate document library action.
 * The action is only valid when the current filter is not "path"
 *
 * @author: mikeh
 */
public class LocateActionEvaluator extends BaseEvaluator
{
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        String filter = getArg("filter");
        if (filter instanceof String)
        {
            return !(filter.equalsIgnoreCase("path"));
        }

        return false;
    }
}
