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
package org.alfresco.web.scripts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;

/**
 * Tests for the {@link ShareManifest} class.
 * 
 * @author Matt Ward
 */
public class ShareManifestTest
{
    private ShareManifest shareManifest;
    private File manifestFile;
    
    @Before
    public void setUp() throws Exception
    {
        // Write a sample manifest file that we can read with the class under test.
        manifestFile = File.createTempFile("Manifest-Test", "MF");
        manifestFile.deleteOnExit();
        try (PrintWriter pw = new PrintWriter(manifestFile))
        {
            pw.println("First-Attr: Red");
            pw.println("Second-Attr: Green");
            pw.println("");
            pw.println("Name: Meals");
            pw.println("Dinner: Fish and chips");
            pw.println("Lunch: Pizza");
            pw.println("Breakfast: Toast");
        }
                
        // Create an instance of the class under test.
        shareManifest = new ShareManifest(new FileSystemResource(manifestFile));
        
        // Normally handled by register(), but we don't want to have to deal
        // with mocking out all the details of a processor - just test the manifest related stuff.
        shareManifest.readManifest();
    }

    @Test
    public void canGetMainAttributeNames()
    {
        List<String> attrNames = shareManifest.mainAttributeNames();
        assertEquals(2, attrNames.size());
        // Do not expect these to be ordered, they came from a Set<Object>
        assertTrue(attrNames.contains("First-Attr"));
        assertTrue(attrNames.contains("Second-Attr"));
    }
    
    @Test
    public void canGetMainAttributes()
    {
        assertEquals("Red", shareManifest.mainAttributeValue("First-Attr"));
        assertEquals("Green", shareManifest.mainAttributeValue("Second-Attr"));
        // Dinner belongs to the Meals section, not the 'main' section.
        assertNull(shareManifest.mainAttributeValue("Dinner"));
    }
    
    @Test
    public void canGetMainAttributesMap()
    {
        Map<String, String> map = shareManifest.mainAttributesMap();
        assertEquals(2, map.size());
        assertEquals("Red", map.get("First-Attr"));
        assertEquals("Green", map.get("Second-Attr"));
    }
    
    @Test
    public void canGetAllSectionNames()
    {
        Set<String> sections = shareManifest.sectionNames();
        assertEquals(1, sections.size());
        assertTrue(sections.contains("Meals"));
    }
    
    @Test
    public void canGetNamedSectionAttributeNames()
    {
        List<String> attrNames = shareManifest.attributeNames("Meals");
        assertEquals(3, attrNames.size());
        assertTrue(attrNames.contains("Breakfast"));
        assertTrue(attrNames.contains("Lunch"));
        assertTrue(attrNames.contains("Dinner"));
    }
    
    @Test
    public void canGetNamedSectionAttributeValue()
    {
        assertEquals("Fish and chips", shareManifest.attributeValue("Meals", "dinner"));
        assertEquals("Pizza", shareManifest.attributeValue("Meals", "lunch"));
        assertEquals("Toast", shareManifest.attributeValue("Meals", "breakfast"));
    }
    
    @Test
    public void canGetNamedSectionAttributesMap()
    {
        Map<String, String> map = shareManifest.attributesMap("Meals");
        assertEquals(3, map.size());
        assertEquals("Fish and chips", map.get("Dinner"));
        assertEquals("Pizza", map.get("Lunch"));
        assertEquals("Toast", map.get("Breakfast"));
    }

    @Test
    public void doesntGetVersion()
    {
        try
        {
            String version = shareManifest.getSpecificationVersion();
            fail("should throw the error");
        } catch (AlfrescoRuntimeException expected)
        {
            assertTrue(expected.getMessage().contains("Share Specification-Version is missing"));
            assertTrue(expected.getMessage().contains("Invalid MANIFEST.MF"));
        }

        try
        {
            String version = shareManifest.getImplementationVersion();
            fail("should throw the error");
        } catch (AlfrescoRuntimeException expected)
        {
            assertTrue(expected.getMessage().contains("Share Implementation-Version is missing"));
            assertTrue(expected.getMessage().contains("Invalid MANIFEST.MF"));
        }
    }
    
    @Test
    public void canHandleMissingSection()
    {
        List<String> noNames = shareManifest.attributeNames("Missing");
        assertTrue(noNames.isEmpty());
    }
}
