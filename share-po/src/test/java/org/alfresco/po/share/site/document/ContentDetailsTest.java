/*
 * #%L
 * share-po
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
/**
 * 
 */
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Ranjith Manyam
 *
 */
@Listeners(FailedTestListener.class)
public class ContentDetailsTest extends AbstractSiteDashletTest
{

    @Test
    public void testDefaultConstructor()
    {
        ContentDetails details = new ContentDetails();
        details.setName("Name");
        details.setTitle("Title");
        details.setDescription("Description");
        details.setContent("Content");

        Assert.assertEquals(details.getName(), "Name");
        Assert.assertEquals(details.getTitle(), "Title");
        Assert.assertEquals(details.getDescription(), "Description");
        Assert.assertEquals(details.getContent(), "Content");
    }

    @Test
    public void testNameFieldConstructor()
    {
        ContentDetails details = new ContentDetails("Name");

        Assert.assertEquals(details.getName(), "Name");
        Assert.assertNull(details.getTitle());
        Assert.assertNull(details.getDescription());
        Assert.assertNull(details.getContent());
    }

    @Test
    public void testConstructorWithAllFields()
    {
        ContentDetails details = new ContentDetails("Name", "Title", "Description", "Content");

        Assert.assertEquals(details.getName(), "Name");
        Assert.assertEquals(details.getTitle(), "Title");
        Assert.assertEquals(details.getDescription(), "Description");
        Assert.assertEquals(details.getContent(), "Content");
    }
    
}
