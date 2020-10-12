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

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class FieldVisibilityManagerTest extends TestCase
{
    private static FieldVisibilityManager emptyFVM = new FieldVisibilityManager();
    private static FieldVisibilityManager firstOverrideFVM = new FieldVisibilityManager();
    private static FieldVisibilityManager secondOverrideFVM = new FieldVisibilityManager();
    private static FieldVisibilityManager thirdOverrideFVM = new FieldVisibilityManager();
    private static FieldVisibilityManager fourthOverrideFVM = new FieldVisibilityManager();
    private static FieldVisibilityManager fifthOverrideFVM = new FieldVisibilityManager();

    public FieldVisibilityManagerTest(String name)
    {
        super(name);
    }
    
    @Override
    public void setUp()
    {
        firstOverrideFVM.addInstruction("hide", "A", null);
        firstOverrideFVM.addInstruction("hide", "B", "view");

        secondOverrideFVM.addInstruction("hide", "A", "edit");
        secondOverrideFVM.addInstruction("hide", "B", "create");

        thirdOverrideFVM.addInstruction("show", "C", "create");
        thirdOverrideFVM.addInstruction("show", "D", "");

        fourthOverrideFVM.addInstruction("hide", "D", null);

        fifthOverrideFVM.addInstruction("hide", "C", null);
    }
    
    public void testEmptyFVM()
    {
        assertFieldIsVisibleInModes(emptyFVM, "A", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsVisibleInModes(emptyFVM, "B", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsVisibleInModes(emptyFVM, "C", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsVisibleInModes(emptyFVM, "D", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsVisibleInModes(emptyFVM, "Z", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        
        assertEquals(null,
                emptyFVM.getFieldNamesVisibleInMode(Mode.CREATE));
        assertEquals(null,
                emptyFVM.getFieldNamesVisibleInMode(Mode.EDIT));
        assertEquals(null,
                emptyFVM.getFieldNamesVisibleInMode(Mode.VIEW));

        assertEquals(null,
                emptyFVM.getFieldNamesHiddenInMode(Mode.CREATE));
        assertEquals(null,
                emptyFVM.getFieldNamesHiddenInMode(Mode.EDIT));
        assertEquals(null,
                emptyFVM.getFieldNamesHiddenInMode(Mode.VIEW));
    }
    
    public void testFirstOverrideFVM()
    {
        FieldVisibilityManager testFVM = emptyFVM.combine(firstOverrideFVM);
        assertFieldIsNotVisibleInModes(testFVM, "A", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsVisibleInModes(testFVM, "B", Mode.CREATE, Mode.EDIT);
        assertFieldIsNotVisibleInModes(testFVM, "B", Mode.VIEW);
        assertFieldIsVisibleInModes(testFVM, "C", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsVisibleInModes(testFVM, "D", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsVisibleInModes(testFVM, "Z", Mode.CREATE, Mode.EDIT, Mode.VIEW);

        assertEquals(null,
                testFVM.getFieldNamesVisibleInMode(Mode.CREATE));
        assertEquals(null,
                testFVM.getFieldNamesVisibleInMode(Mode.EDIT));
        assertEquals(null,
                testFVM.getFieldNamesVisibleInMode(Mode.VIEW));

        assertEquals(Arrays.asList(new String[]{"A"}),
                testFVM.getFieldNamesHiddenInMode(Mode.CREATE));
        assertEquals(Arrays.asList(new String[]{"A"}),
                testFVM.getFieldNamesHiddenInMode(Mode.EDIT));
        assertEquals(Arrays.asList(new String[]{"A", "B"}),
                testFVM.getFieldNamesHiddenInMode(Mode.VIEW));
}
    
    public void testSecondOverrideFVM()
    {
        FieldVisibilityManager testFVM = emptyFVM.combine(firstOverrideFVM).combine(secondOverrideFVM);
        assertFieldIsNotVisibleInModes(testFVM, "A", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsVisibleInModes(testFVM, "B", Mode.EDIT);
        assertFieldIsNotVisibleInModes(testFVM, "B", Mode.CREATE, Mode.VIEW);
        assertFieldIsVisibleInModes(testFVM, "C", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsVisibleInModes(testFVM, "D", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsVisibleInModes(testFVM, "Z", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        
        assertEquals(null,
                testFVM.getFieldNamesVisibleInMode(Mode.CREATE));
        assertEquals(null,
                testFVM.getFieldNamesVisibleInMode(Mode.EDIT));
        assertEquals(null,
                testFVM.getFieldNamesVisibleInMode(Mode.VIEW));

        assertEquals(Arrays.asList(new String[]{"A", "B"}),
                testFVM.getFieldNamesHiddenInMode(Mode.CREATE));
        assertEquals(Arrays.asList(new String[]{"A"}),
                testFVM.getFieldNamesHiddenInMode(Mode.EDIT));
        assertEquals(Arrays.asList(new String[]{"A", "B"}),
                testFVM.getFieldNamesHiddenInMode(Mode.VIEW));
}
    
    public void testThirdOverrideFVM()
    {
        FieldVisibilityManager testFVM = emptyFVM.combine(firstOverrideFVM).combine(secondOverrideFVM)
            .combine(thirdOverrideFVM);
        assertFieldIsNotVisibleInModes(testFVM, "A", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsNotVisibleInModes(testFVM, "B", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsVisibleInModes(testFVM, "C", Mode.CREATE);
        assertFieldIsNotVisibleInModes(testFVM, "C", Mode.EDIT, Mode.VIEW);
        assertFieldIsVisibleInModes(testFVM, "D", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsNotVisibleInModes(testFVM, "Z", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        
        assertEquals(Arrays.asList(new String[]{"C", "D"}),
                testFVM.getFieldNamesVisibleInMode(Mode.CREATE));
        assertEquals(Arrays.asList(new String[]{"D"}),
                testFVM.getFieldNamesVisibleInMode(Mode.EDIT));
        assertEquals(Arrays.asList(new String[]{"D"}),
                testFVM.getFieldNamesVisibleInMode(Mode.VIEW));

        assertEquals(null, testFVM.getFieldNamesHiddenInMode(Mode.CREATE));
        assertEquals(null, testFVM.getFieldNamesHiddenInMode(Mode.EDIT));
        assertEquals(null, testFVM.getFieldNamesHiddenInMode(Mode.VIEW));
}
    
    public void testFourthOverrideFVM()
    {
        FieldVisibilityManager testFVM = emptyFVM.combine(firstOverrideFVM).combine(secondOverrideFVM)
            .combine(thirdOverrideFVM).combine(fourthOverrideFVM);
        assertFieldIsNotVisibleInModes(testFVM, "A", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsNotVisibleInModes(testFVM, "B", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsVisibleInModes(testFVM, "C", Mode.CREATE);
        assertFieldIsNotVisibleInModes(testFVM, "C", Mode.EDIT, Mode.VIEW);
        assertFieldIsNotVisibleInModes(testFVM, "D", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsNotVisibleInModes(testFVM, "Z", Mode.CREATE, Mode.EDIT, Mode.VIEW);

        assertEquals(Arrays.asList(new String[]{"C"}),
                testFVM.getFieldNamesVisibleInMode(Mode.CREATE));
        assertEquals(Arrays.asList(new String[]{}),
                testFVM.getFieldNamesVisibleInMode(Mode.EDIT));
        assertEquals(Arrays.asList(new String[]{}),
                testFVM.getFieldNamesVisibleInMode(Mode.VIEW));

        assertEquals(null, testFVM.getFieldNamesHiddenInMode(Mode.CREATE));
        assertEquals(null, testFVM.getFieldNamesHiddenInMode(Mode.EDIT));
        assertEquals(null, testFVM.getFieldNamesHiddenInMode(Mode.VIEW));
}
    
    public void testFifthOverrideFVM()
    {
        FieldVisibilityManager testFVM = emptyFVM.combine(firstOverrideFVM).combine(secondOverrideFVM)
            .combine(thirdOverrideFVM).combine(fourthOverrideFVM).combine(fifthOverrideFVM);
        assertFieldIsNotVisibleInModes(testFVM, "A", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsNotVisibleInModes(testFVM, "B", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsNotVisibleInModes(testFVM, "C", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsNotVisibleInModes(testFVM, "D", Mode.CREATE, Mode.EDIT, Mode.VIEW);
        assertFieldIsNotVisibleInModes(testFVM, "Z", Mode.CREATE, Mode.EDIT, Mode.VIEW);

        assertEquals(Arrays.asList(new String[]{}),
                testFVM.getFieldNamesVisibleInMode(Mode.CREATE));
        assertEquals(Arrays.asList(new String[]{}),
                testFVM.getFieldNamesVisibleInMode(Mode.EDIT));
        assertEquals(Arrays.asList(new String[]{}),
                testFVM.getFieldNamesVisibleInMode(Mode.VIEW));

        assertEquals(null, testFVM.getFieldNamesHiddenInMode(Mode.CREATE));
        assertEquals(null, testFVM.getFieldNamesHiddenInMode(Mode.EDIT));
        assertEquals(null, testFVM.getFieldNamesHiddenInMode(Mode.VIEW));
}
    
    public void testCheckVisibleFieldsforHideManagingFVM()
    {
        FieldVisibilityManager testFVM = emptyFVM.combine(firstOverrideFVM).combine(secondOverrideFVM);
        List<String> visibleCreateFields = testFVM.getFieldNamesVisibleInMode(Mode.CREATE);
        List<String> visibleEditFields = testFVM.getFieldNamesVisibleInMode(Mode.EDIT);
        List<String> visibleViewFields = testFVM.getFieldNamesVisibleInMode(Mode.VIEW);
        
        assertNull("We cannot know what fields are visible when there are no show tags.",
                visibleCreateFields);
        assertNull("We cannot know what fields are visible when there are no show tags.",
                visibleEditFields);
        assertNull("We cannot know what fields are visible when there are no show tags.",
                visibleViewFields);
    }
    
    public void testCheckVisibleFieldsforShowManagingFVM()
    {
        FieldVisibilityManager testFVM = emptyFVM.combine(firstOverrideFVM)
            .combine(secondOverrideFVM).combine(thirdOverrideFVM);
        List<String> visibleCreateFields = testFVM.getFieldNamesVisibleInMode(Mode.CREATE);
        List<String> visibleEditFields = testFVM.getFieldNamesVisibleInMode(Mode.EDIT);
        List<String> visibleViewFields = testFVM.getFieldNamesVisibleInMode(Mode.VIEW);
        
        assertEquals(Arrays.asList(new String[]{"C", "D"}), visibleCreateFields);
        assertEquals(Arrays.asList(new String[]{"D"}), visibleEditFields);
        assertEquals(Arrays.asList(new String[]{"D"}), visibleViewFields);
    }
    
    public void testCheckHiddenFieldsforHideManagingFVM()
    {
        FieldVisibilityManager testFVM = emptyFVM.combine(firstOverrideFVM).combine(secondOverrideFVM);
        List<String> hiddenCreateFields = testFVM.getFieldNamesHiddenInMode(Mode.CREATE);
        List<String> hiddenEditFields = testFVM.getFieldNamesHiddenInMode(Mode.EDIT);
        List<String> hiddenViewFields = testFVM.getFieldNamesHiddenInMode(Mode.VIEW);
        
        assertEquals(Arrays.asList(new String[]{"A", "B"}), hiddenCreateFields);
        assertEquals(Arrays.asList(new String[]{"A"}), hiddenEditFields);
        assertEquals(Arrays.asList(new String[]{"A", "B"}), hiddenViewFields);
    }
    
    public void testCheckHiddenFieldsforShowManagingFVM()
    {
        FieldVisibilityManager testFVM = emptyFVM.combine(firstOverrideFVM)
            .combine(secondOverrideFVM).combine(thirdOverrideFVM);
        List<String> hiddenCreateFields = testFVM.getFieldNamesHiddenInMode(Mode.CREATE);
        List<String> hiddenEditFields = testFVM.getFieldNamesHiddenInMode(Mode.EDIT);
        List<String> hiddenViewFields = testFVM.getFieldNamesHiddenInMode(Mode.VIEW);
        
        assertNull("We cannot know what fields are hidden when there are any show tags.",
                hiddenCreateFields);
        assertNull("We cannot know what fields are hidden when there are any show tags.",
                hiddenEditFields);
        assertNull("We cannot know what fields are hidden when there are any show tags.",
                hiddenViewFields);
    }
    
    private void assertFieldIsVisibleInModes(FieldVisibilityManager fvm, String fieldId, Mode... modes)
    {
        for (Mode m : modes)
        {
            assertTrue(fieldId + " should be visible in mode " + m,
                    fvm.isFieldVisible(fieldId, m));
        }
    }

    private void assertFieldIsNotVisibleInModes(FieldVisibilityManager fvm, String fieldId, Mode... modes)
    {
        for (Mode m : modes)
        {
            assertFalse(fieldId + " should NOT be visible in mode " + m,
                    fvm.isFieldVisible(fieldId, m));
        }
    }
}
