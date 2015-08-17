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

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.VersionNumber;
import org.alfresco.web.scripts.ShareManifest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tests the ModulePackageManager
 * @author Gethin James
 */
public class ModulePackageManagerTest
{
    private static Log logger = LogFactory.getLog(ModulePackageManagerTest.class);
    private static final DefaultResourceLoader loader = new DefaultResourceLoader();

    public static ModulePackageManager setup()
    {
        return new ModulePackageManager();
    }
    private static ShareManifest shareManifest;
    private static File manifestFile;

    @BeforeClass
    public static void setUp() throws Exception
    {
        // Write a sample manifest file that we can read with the class under test.
        manifestFile = File.createTempFile("Manifest-Test", "MF");
        manifestFile.deleteOnExit();
        try (PrintWriter pw = new PrintWriter(manifestFile))
        {
            pw.println(ModulePackageHelper.MANIFEST_SPECIFICATION_TITLE+": "+ModulePackageHelper.MANIFEST_SHARE);
            pw.println(ModulePackageHelper.MANIFEST_SPECIFICATION_VERSION+": "+"5.1");
            pw.println(ModulePackageHelper.MANIFEST_IMPLEMENTATION_TITLE+": "+ModulePackageHelper.MANIFEST_COMMUNITY);
        }

        // Create an instance of the class under test.
        shareManifest = new ShareManifest(new FileSystemResource(manifestFile));

        // Normally handled by register(), but we don't want to have to deal
        // with mocking out all the details of a processor - just test the manifest related stuff.
        shareManifest.readManifest();
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
        Resource resource = loader.getResource("classpath:alfresco/module/simple/simplemodule.properties");
        ModulePackage mp = ModulePackageManager.asModulePackage(resource);
        logger.debug("Simple module:" + mp.toString());
        assertEquals("Alfresco JAR Module Project", mp.getTitle());
        assertEquals("alfresco-simple-module", mp.getId());
        assertEquals("UNSUPPORTED experiment", mp.getDescription());
        assertTrue(new ComparableVersion("1.0-SNAPSHOT").equals(mp.getVersion()));

        ModulePackageUsingProperties mpup = (ModulePackageUsingProperties) mp;
        Map<String, String> asMap = ModulePackageHelper.toMap(mpup);
        assertEquals("Alfresco JAR Module Project", asMap.get("title"));
        assertEquals("alfresco-simple-module", asMap.get("id"));
        assertEquals("UNSUPPORTED experiment", asMap.get("description"));
        assertEquals("1.0-SNAPSHOT", asMap.get("version"));

        resource = loader.getResource("classpath:alfresco/module/bad/badmodule.properties");
        mp = ModulePackageManager.asModulePackage(resource);
        logger.debug("Bad module:" + mp.toString());
        assertEquals("Alfresco Bad Module", mp.getTitle());
        assertNull("A bad module with no id.", mp.getId());
        assertNull("A bad module with no desc.", mp.getDescription());
        assertTrue(new ComparableVersion(ModulePackageUsingProperties.UNSET_VERSION).equals(mp.getVersion()));

        mpup = (ModulePackageUsingProperties) mp;
        asMap = ModulePackageHelper.toMap(mpup);
        assertEquals("Alfresco Bad Module", asMap.get("title"));
        assertEquals("null", asMap.get("id"));
        assertEquals("null", asMap.get("description"));
        assertEquals(ModulePackageUsingProperties.UNSET_VERSION, asMap.get("version"));
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

    @Test
    public void tesModuleShareVersions()
    {
        Resource resource = loader.getResource("classpath:alfresco/module/simple/simplemodule.properties");
        ModulePackage mp = ModulePackageManager.asModulePackage(resource);
        assertEquals("alfresco-simple-module", mp.getId());
        //Uses module.repo.version rather than module.share.version
        assertEquals("2.0", mp.getVersionMin().toString());
        assertEquals("2.1", mp.getVersionMax().toString());

        resource = loader.getResource("classpath:alfresco/module/bad/badmodule.properties");
        mp = ModulePackageManager.asModulePackage(resource);
        assertEquals("Alfresco Bad Module", mp.getTitle());
        //No min/max
        assertEquals(VersionNumber.VERSION_ZERO, mp.getVersionMin());
        assertEquals(VersionNumber.VERSION_BIG, mp.getVersionMax());

        resource = loader.getResource("classpath:alfresco/module/pent/module.properties");
        mp = ModulePackageManager.asModulePackage(resource);
        assertEquals("pentaho-share", mp.getId());
        //Uses module.share.version
        assertEquals("5.0", mp.getVersionMin().toString());
        assertEquals("5.999", mp.getVersionMax().toString());
    }

    @Test
    public void testModuleVersionChecksValid()
    {
        Resource resource = loader.getResource("classpath:alfresco/module/pent/module.properties");
        ModulePackage mp = ModulePackageManager.asModulePackage(resource);
        ModulePackageHelper.checkValid(mp, shareManifest);

        //Nothing specified so valid
        resource = loader.getResource("classpath:alfresco/module/bad/badmodule.properties");
        mp = ModulePackageManager.asModulePackage(resource);
        ModulePackageHelper.checkValid(mp, shareManifest);
    }


    @Test
    public void testModuleVersionChecksInValid()
    {
        Resource resource = loader.getResource("classpath:alfresco/module/simple/simplemodule.properties");
        ModulePackage mp = ModulePackageManager.asModulePackage(resource);

        try
        {
            ModulePackageHelper.checkValid(mp, shareManifest);
            assertFalse(true); //should not get here
        } catch (AlfrescoRuntimeException are)
        {
           assertTrue(are.getMessage().contains("cannot be installed on a Share version greater than 2.1"));
        }

        resource = loader.getResource("classpath:alfresco/module/user.admin/module.properties");
        mp = ModulePackageManager.asModulePackage(resource);
        try
        {
            ModulePackageHelper.checkValid(mp, shareManifest);
            assertFalse(true); //should not get here
        } catch (AlfrescoRuntimeException are)
        {
            assertTrue(are.getMessage().contains("must be installed on a Share version greater than 5.2"));
        }
    }
}
