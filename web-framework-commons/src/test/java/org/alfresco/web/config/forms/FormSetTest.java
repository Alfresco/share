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
package org.alfresco.web.config.forms;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.springframework.extensions.config.ConfigException;

public class FormSetTest extends TestCase
{
    private FormConfigElement testElement;
    public FormSetTest(String name)
    {
        super(name);
    }
    
    @Override
    public void setUp()
    {
        this.testElement = new FormConfigElement();
        this.testElement.addSet("root1", null, null, null, null);
        this.testElement.addSet("intermediate1", "root1", null, null, null);
        this.testElement.addSet("intermediate2", "root1", null, null, null);
        this.testElement.addSet("leaf1", "intermediate1", null, null, null);

        this.testElement.addSet("root2", null, null, null, null);

        this.testElement.addSet("root66", null, null, null, null);
        this.testElement.addSet("leaf2", "root66", null, null, null);
    }
    
    public void testGetSetIDsAreCorrect()
    {
        Map<String, FormSet> sets = this.testElement.getSets();
        List<String> setNames = new ArrayList<String>();
        setNames.addAll(sets.keySet());
        assertEquals("Error in set names.", testElement.getSetIDsAsList(), setNames);
        
        // Note ordering is important here.
        Set<String> expectedSetIDs = new LinkedHashSet<String>();
        expectedSetIDs.add(FormConfigElement.DEFAULT_SET_ID);
        expectedSetIDs.add("root1");
        expectedSetIDs.add("intermediate1");
        expectedSetIDs.add("intermediate2");
        expectedSetIDs.add("leaf1");
        expectedSetIDs.add("root2");
        expectedSetIDs.add("root66");
        expectedSetIDs.add("leaf2");
        assertEquals(expectedSetIDs, sets.keySet());
    }
    
    public void testRootSetsCorrectlyIdentified()
    {
        List<FormSet> rootSets = testElement.getRootSetsAsList();
        assertNotNull(rootSets);
        assertEquals("Expecting 4 root sets", 4, rootSets.size());
        assertEquals(FormConfigElement.DEFAULT_SET_ID, rootSets.get(0).getSetId());
        assertEquals("root1", rootSets.get(1).getSetId());
        assertEquals("root2", rootSets.get(2).getSetId());
        assertEquals("root66", rootSets.get(3).getSetId());
    }
    
    public void testRootSetsShouldHaveNoParent()
    {
        List<FormSet> rootSets = testElement.getRootSetsAsList();
        for (FormSet nextRoot : rootSets)
        {
            assertNull(nextRoot.getParent());
        }
    }
    
    public void testNavigationToChildSets()
    {
        Map<String, FormSet> allSets = testElement.getSets();
        
        // parent with children
        FormSet root1 = allSets.get("root1");
        List<FormSet> root1Children = root1.getChildrenAsList();
        assertEquals("Expecting 2 children for root1", 2, root1Children.size());
        assertEquals("intermediate1", root1Children.get(0).getSetId());
        assertEquals("intermediate2", root1Children.get(1).getSetId());

        // parent without children
        FormSet root2 = allSets.get("root2");
        List<FormSet> root2Children = root2.getChildrenAsList();
        assertEquals("Expecting 0 children for root2", 0, root2Children.size());
    }
    
    public void testNavigationToParentSets()
    {
        Map<String, FormSet> allSets = testElement.getSets();

        FormSet leaf2 = allSets.get("leaf2");
        assertEquals("root66", leaf2.getParent().getSetId());
    }
    
    public void testDetectCyclicAncestors()
    {
        // It should not be possible to create a set of sets whose ancestors are
        // cyclic. This is true if we disallow the creation of a set with a parentID
        // of a set that does not exist.
        try
        {
            FormConfigElement brokenFormElement = new FormConfigElement();
            brokenFormElement.addSet("root", "leaf", null, null, null);
            // This next line will not in fact be called but it illustrates what we're
            // trying to prevent.
            brokenFormElement.addSet("leaf", "root", null, null, null);
        }
        catch (ConfigException expected)
        {
            return;
        }
        fail("Expected exception not thrown.");
    }
    
    public void testCannotGiveTheDefaultSetAParent() throws Exception
    {
        // It should not be possible to create the default set except as a 'root set'.
        try
        {
            this.testElement.addSet(FormConfigElement.DEFAULT_SET_ID, "root1", null, null, null);
        }
        catch(ConfigException expected)
        {
            expected.toString();
            return;
        }
        fail("Expected exception not thrown.");
    }
}
