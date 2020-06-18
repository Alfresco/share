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
package org.alfresco.web.scripts;

import org.alfresco.web.config.packaging.ModulePackage;
import org.alfresco.web.config.packaging.ModulePackageHelper;
import org.alfresco.web.config.packaging.ModulePackageManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gets a list of ModulePackages as JSON.
 * @author Gethin James
 */
public class ModulePackageWebScript extends DeclarativeWebScript
{
    private static Log logger = LogFactory.getLog(ModulePackageWebScript.class);
    private ModulePackageManager moduleManager;

    protected Map<String, Object> executeImpl(
            WebScriptRequest req, Status status, Cache cache) {

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("modulepackages", asMap(moduleManager.getModulePackages()));
        return model;

    }

    private List<Map> asMap(List<ModulePackage> mp)
    {
        List<Map> modulesPacks = new ArrayList<>();

        if (mp!= null && !mp.isEmpty())
        {
            for (ModulePackage modulePackage : mp)
            {
                modulesPacks.add(ModulePackageHelper.toMap(modulePackage));
            }
        }
        return modulesPacks;
    }

    public void setModuleManager(ModulePackageManager moduleManager)
    {
        this.moduleManager = moduleManager;
    }

}
