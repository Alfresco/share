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
package org.alfresco.web.config.packaging;

import static org.junit.Assert.*;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.scripts.ShareManifest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Tests the ModulePackageHelper
 * @author Gethin James
 */
public class ModulePackageHelperTest
{
    public static final String SIMPLE_SIMPLEMODULE_PROPERTIES = "classpath:alfresco/module/simple/simplemodule.properties";
    public static final String BAD_BADMODULE_PROPERTIES = "classpath:alfresco/module/bad/badmodule.properties";
    public static final String USER_BADMODULE_PROPERTIES = "classpath:alfresco/module/bad/usermodule.properties";
    public static final String MODULE_PENT_MODULE_PROPERTIES = "classpath:alfresco/module/pent/module.properties";

    public static ModulePackageManager setup()
    {
        return new ModulePackageManager();
    }
    protected static final DefaultResourceLoader loader = new DefaultResourceLoader();
    protected static ShareManifest shareManifest;
    protected static File manifestFile;


    @BeforeClass
    public static void setUp() throws Exception
    {
        // Write a sample manifest file that we can read with the class under test.
        manifestFile = File.createTempFile("Manifest-Test", "MF");
        manifestFile.deleteOnExit();
        try (PrintWriter pw = new PrintWriter(manifestFile))
        {
            pw.println(ShareManifest.MANIFEST_SPECIFICATION_TITLE+": "+ModulePackageHelper.MANIFEST_SHARE);
            pw.println(ShareManifest.MANIFEST_SPECIFICATION_VERSION+": "+"5.1");
            pw.println(ShareManifest.MANIFEST_IMPLEMENTATION_VERSION+": "+"5.1-SNAPSHOT");
        }

        // Create an instance of the class under test.
        shareManifest = new ShareManifest(new FileSystemResource(manifestFile));

        // Normally handled by register(), but we don't want to have to deal
        // with mocking out all the details of a processor - just test the manifest related stuff.
        shareManifest.readManifest();
    }

    @Test
    public void testValidVersion() throws IOException
    {
        assertEquals("5.1",shareManifest.getSpecificationVersion());
        assertEquals("5.1-SNAPSHOT",shareManifest.getImplementationVersion());
    }

    @Test
    public void testInValidVersion() throws IOException
    {
        File badFile = File.createTempFile("Bad Manifest", "MF");
        badFile.deleteOnExit();
        ShareManifest badManifest = new ShareManifest(new FileSystemResource(badFile));
        badManifest.readManifest();
        try
        {
            ModulePackageHelper.checkValid(null, badManifest);
            fail("should throw the error");
        } catch (AlfrescoRuntimeException expected)
        {
            assertTrue(expected.getMessage().contains("Share Specification-Version is missing"));
            assertTrue(expected.getMessage().contains("Invalid MANIFEST.MF"));
        }
    }

    @Test
    public void testCheckValid()
    {
        Resource resource = loader.getResource(MODULE_PENT_MODULE_PROPERTIES);
        ModulePackage mp = ModulePackageManager.asModulePackage(resource);
        ModulePackageHelper.checkValid(mp, shareManifest);

        //Nothing specified so valid
        resource = loader.getResource(BAD_BADMODULE_PROPERTIES);
        mp = ModulePackageManager.asModulePackage(resource);
        ModulePackageHelper.checkValid(mp, shareManifest);
    }


    @Test
    public void testCheckVersions()
    {
        Resource resource = loader.getResource(SIMPLE_SIMPLEMODULE_PROPERTIES);
        ModulePackage mp = ModulePackageManager.asModulePackage(resource);

        try
        {
            ModulePackageHelper.checkValid(mp, shareManifest);
            assertFalse(true); //should not get here
        } catch (AlfrescoRuntimeException are)
        {
            assertTrue(are.getMessage().contains("cannot be installed on a Share version greater than 2.1"));
        }

        resource = loader.getResource(USER_BADMODULE_PROPERTIES);
        mp = ModulePackageManager.asModulePackage(resource);
        try
        {
            ModulePackageHelper.checkValid(mp, shareManifest);
            assertFalse(true); //should not get here
        } catch (AlfrescoRuntimeException are)
        {
            assertTrue(are.getMessage().contains("must be installed on a Share version equal to or greater than 5.2"));
        }
    }

    @Test
    public void testCheckDependencies() throws Exception
    {
        /**
         * Currently we have only implemented ID checking in ModuleDependencies and ignore the VERSION information.
         * This will be addressed in future releases.
         */
        Resource resource = loader.getResource(BAD_BADMODULE_PROPERTIES);
        ModulePackage mp = ModulePackageManager.asModulePackage(resource);
        List<ModulePackageDependency> deps = mp.getDependencies();
        List<ModulePackage> mods = new ModulePackageManager().resolveModules(ModulePackageManager.MODULE_RESOURCES);

        assertNotNull(mods);
        assertNotNull(deps);
        assertTrue("Bad module has no dependencies", deps.isEmpty());
        ModulePackageHelper.checkDependencies(mp, mods);

        resource = loader.getResource(MODULE_PENT_MODULE_PROPERTIES);
        mp = ModulePackageManager.asModulePackage(resource);
        ModulePackageHelper.checkDependencies(mp, mods);

        try
        {
            resource = loader.getResource(SIMPLE_SIMPLEMODULE_PROPERTIES);
            mp = ModulePackageManager.asModulePackage(resource);
            ModulePackageHelper.checkDependencies(mp, mods);
            assertFalse(true); //should not get here
        } catch (AlfrescoRuntimeException are)
        {
            assertTrue(are.getMessage().contains("cannot be installed. The following modules must first be installed:"));
            assertTrue(are.getMessage().contains("net.sf.myproject.module.SupportModuleA"));
            assertTrue(are.getMessage().contains("net.sf.myproject.module.SupportModuleB"));
            assertTrue(are.getMessage().contains("net.sf.myproject.module.SupportModuleC"));
        }

    }

    @Test
    public void testAsModuleDependencies()
    {
        /**
         * Currently we have only implemented ID checking in ModuleDependencies and ignore the VERSION information.
         * This will be addressed in future releases.
         */
        Resource resource = loader.getResource(SIMPLE_SIMPLEMODULE_PROPERTIES);
        ModulePackage mp = ModulePackageManager.asModulePackage(resource);
        List<ModulePackageDependency> deps = mp.getDependencies();
        assertNotNull(deps);
        assertEquals(3, deps.size());
        for (ModulePackageDependency dependency : deps)
        {
            String id = dependency.getId();
            assertTrue(id.startsWith("net.sf.myproject.module.SupportModule"));
            assertTrue(id.endsWith("A")||id.endsWith("B")||id.endsWith("C"));

            //Always null for now until we implement it.
            assertNull(dependency.getVersionRange());
        }
    }

}