/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

@Test(groups="unit")
public class DocumentAspectTest 
{
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void getAspectWithNull() throws Exception
    {
        DocumentAspect.getAspect(null);
    }
    
    @Test(dependsOnMethods="getAspectWithNull", expectedExceptions=UnsupportedOperationException.class)
    public void getAspectWithEmptyName() throws Exception
    {
        DocumentAspect.getAspect("");
    }
    @Test(dependsOnMethods="getAspectWithEmptyName", expectedExceptions=Exception.class)
    public void getAspectWithWrongName() throws Exception
    {
        DocumentAspect.getAspect("Alfresco");
    }
    @Test(dependsOnMethods="getAspectWithWrongName", expectedExceptions=Exception.class)
    public void getAspect() throws Exception
    {
        assertEquals(DocumentAspect.getAspect("Alfresco"), DocumentAspect.AUDIO);
    }

    @Test(dependsOnMethods="getAspect")
    public void getAspectByProperty() throws Exception
    {
        assertEquals(DocumentAspect.getAspectByProperty("P:cm:generalclassifiable"), DocumentAspect.CLASSIFIABLE);
        assertEquals(DocumentAspect.getAspectByProperty("P:cm:versionable"), DocumentAspect.VERSIONABLE);
        assertEquals(DocumentAspect.getAspectByProperty("P:audio:audio"), DocumentAspect.AUDIO);
        assertEquals(DocumentAspect.getAspectByProperty("P:cm:indexControl"), DocumentAspect.INDEX_CONTROL);
        assertEquals(DocumentAspect.getAspectByProperty("P:cm:complianceable"), DocumentAspect.COMPLIANCEABLE);
        assertEquals(DocumentAspect.getAspectByProperty("P:cm:dublincore"), DocumentAspect.DUBLIN_CORE);
        assertEquals(DocumentAspect.getAspectByProperty("P:cm:effectivity"), DocumentAspect.EFFECTIVITY);
        assertEquals(DocumentAspect.getAspectByProperty("P:cm:summarizable"), DocumentAspect.SUMMARIZABLE);
        assertEquals(DocumentAspect.getAspectByProperty("P:cm:templatable"), DocumentAspect.TEMPLATABLE);
        assertEquals(DocumentAspect.getAspectByProperty("P:cm:emailed"), DocumentAspect.EMAILED);
        assertEquals(DocumentAspect.getAspectByProperty("P:emailserver:aliasable"), DocumentAspect.ALIASABLE_EMAIL);
        assertEquals(DocumentAspect.getAspectByProperty("P:cm:taggable"), DocumentAspect.TAGGABLE);
        assertEquals(DocumentAspect.getAspectByProperty("P:app:inlineeditable"), DocumentAspect.INLINE_EDITABLE);
        assertEquals(DocumentAspect.getAspectByProperty("P:cm:geographic"), DocumentAspect.GEOGRAPHIC);
        assertEquals(DocumentAspect.getAspectByProperty("P:exif:exif"), DocumentAspect.EXIF);
        assertEquals(DocumentAspect.getAspectByProperty("P:dp:restrictable"), DocumentAspect.RESTRICTABLE);
    }

    @Test(dependsOnMethods="getAspectByProperty", expectedExceptions=UnsupportedOperationException.class)
    public void getAspectByPropertyWithNull() throws Exception
    {
        DocumentAspect.getAspectByProperty(null);
    }
    @Test(dependsOnMethods="getAspectByPropertyWithNull", expectedExceptions=UnsupportedOperationException.class)
    public void getAspectByPropertyWithEmptyName() throws Exception
    {
        DocumentAspect.getAspectByProperty("");
    }
    @Test(dependsOnMethods="getAspectByPropertyWithEmptyName", expectedExceptions=Exception.class)
    public void getAspectByPropertyWithWrongName() throws Exception
    {
        DocumentAspect.getAspectByProperty("Alfresco");
    }
    @Test(dependsOnMethods="getAspectByPropertyWithWrongName", expectedExceptions=Exception.class)
    public void getAspectByPropertyWithNoMatch() throws Exception
    {
        assertEquals(DocumentAspect.getAspectByProperty("Alfresco"), DocumentAspect.AUDIO);
    }
}
