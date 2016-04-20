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
