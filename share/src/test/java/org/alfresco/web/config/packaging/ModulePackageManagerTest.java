/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
package org.alfresco.web.config.packaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the ModulePackageManager
 * @author Gethin James
 */
public class ModulePackageManagerTest
{
    private static Log logger = LogFactory.getLog(ModulePackageManagerTest.class);

    public static ModulePackageManager setup()
    {
        return new ModulePackageManager();
    }

    @Test
    public void testResolveModules()
    {
        ModulePackageManager mpm = setup();

        List<ModulePackage> mods = mpm.resolveModules("classpath*:NOFresco/module/*/module.properties");
        assertTrue("No modules should be found", mods.isEmpty());
        mods = mpm.resolveModules(ModulePackageManager.MODULE_RESOURCES);
        assertFalse("Modules should be found", mods.isEmpty());
        assertEquals(3, mods.size());
        List<String> ids = toIds(mods);
        assertTrue(ids.contains("module-list"));
        assertTrue(ids.contains("user-admin"));
        assertTrue(ids.contains("pentaho-share"));
    }

    private List<String> toIds(List<ModulePackage> mods)
    {
        //In Java 8 this is: mods.stream().map(module -> module.getId()).collect(toList());
        //In Groovy mods.collect { it.id }
        //In Java 7
        List<String> ids = new ArrayList<>();
        for (ModulePackage mod : mods)
        {
            ids.add(mod.getId());
        }
        return ids;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResolveModulesErrors()
    {
        ModulePackageManager mpm = setup();
        mpm.resolveModules(null);
    }


    @Test
    public void testAsModulePackage()
    {
        ModulePackageManager mpm = setup();
        Resource resource = new DefaultResourceLoader().getResource("classpath:alfresco/module/simple/simplemodule.properties");
        ModulePackage mp = mpm.asModulePackage(resource);
        logger.debug("Simple module:" + mp.toString());
        assertEquals("Alfresco JAR Module Project", mp.getTitle());
        assertEquals("alfresco-simple-module", mp.getId());
        assertEquals("UNSUPPORTED experiment", mp.getDescription());
        assertTrue(new ComparableVersion("1.0-SNAPSHOT").equals(mp.getVersion()));

        resource = new DefaultResourceLoader().getResource("classpath:alfresco/module/bad/badmodule.properties");
        mp = mpm.asModulePackage(resource);
        logger.debug("Bad module:" + mp.toString());
        assertEquals("Alfresco Bad Module", mp.getTitle());
        assertNull("A bad module with no id.", mp.getId());
        assertNull("A bad module with no desc.", mp.getDescription());
        assertTrue(new ComparableVersion(ModulePackageUsingProperties.UNSET_VERSION).equals(mp.getVersion()));
    }

    @Test
    public void testWriteModuleList()
    {
        ModulePackageManager mpm = setup();
        List<ModulePackage> mods = new ArrayList<>();
        String moduleList = mpm.writeModuleList(mods);
        assertTrue("No Modules found so an empty string.", moduleList.length() == 0);

        mods = mpm.resolveModules(ModulePackageManager.MODULE_RESOURCES);
        moduleList = mpm.writeModuleList(mods);
        logger.debug(moduleList);
        assertTrue(moduleList.contains("User admin project"));
    }

}
