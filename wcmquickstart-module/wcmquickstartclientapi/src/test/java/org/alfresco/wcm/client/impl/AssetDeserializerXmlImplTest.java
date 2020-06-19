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
package org.alfresco.wcm.client.impl;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.TreeMap;

import junit.framework.TestCase;

public class AssetDeserializerXmlImplTest extends TestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
        System.out.println(System.getProperty("java.io.tmpdir"));
    }

    public void test1() throws Exception
    {
        AssetDeserializerXmlImpl deserializer = new AssetDeserializerXmlImpl();
        LinkedList<TreeMap<String,Serializable>> results = 
            deserializer.deserialize(new ByteArrayInputStream(sampleXml1.getBytes("UTF-8")));
        System.out.println(results);
        assertEquals(1, results.size());
    }

    private String sampleXml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><assets><asset id=\"workspace://SpacesStore/5e6541cc-d1f0-4c32-9d16-cf2c15c9e8a6\" type=\"myapp:MyType\"><property name=\"myapp:textListProperty\"><list><value type=\"text\"><![CDATA[One]]></value><value type=\"text\"><![CDATA[Two]]></value><value type=\"text\"><![CDATA[Three]]></value><value type=\"text\"><![CDATA[Four]]></value><value type=\"text\"><![CDATA[Five]]></value><value type=\"text\"><![CDATA[Six]]></value></list></property><property name=\"myapp:noderefProperty\"><value type=\"id\">workspace://SpacesStore/b6b10862-da5d-49f3-a6e8-90f8bff0349e</value></property><property name=\"myapp:textProperty\"><value type=\"text\"><![CDATA[The радиатор &lt;sat&gt; on the mat: first on one side; the on the other... –]]></value></property><property name=\"myapp:mapProperty\"><map><property name=\"myapp:textListProperty\"><list><value type=\"text\"><![CDATA[One]]></value><value type=\"text\"><![CDATA[Two]]></value><value type=\"text\"><![CDATA[Three]]></value><value type=\"text\"><![CDATA[Four]]></value><value type=\"text\"><![CDATA[Five]]></value><value type=\"text\"><![CDATA[Six]]></value></list></property><property name=\"myapp:noderefProperty\"><value type=\"id\">workspace://SpacesStore/b6b10862-da5d-49f3-a6e8-90f8bff0349e</value></property><property name=\"myapp:textProperty\"><value type=\"text\"><![CDATA[The радиатор &lt;sat&gt; on the mat: first on one side; the on the other... –]]></value></property><property name=\"myapp:integerProperty\"><value type=\"integer\">678</value></property><property name=\"myapp:dateProperty\"><value type=\"time\">20110405-12:14:02.054+0100</value></property><property name=\"myapp:longProperty\"><value type=\"integer\">6737436288</value></property><property name=\"myapp:doubleProperty\"><value type=\"number\">1.324352E14</value></property><property name=\"myapp:intListProperty\"><list><value type=\"integer\">1</value><value type=\"integer\">2</value><value type=\"integer\">3</value><value type=\"integer\">4</value><value type=\"integer\">5</value><value type=\"integer\">6</value></list></property><property name=\"myapp:floatProperty\"><value type=\"number\">132.43524</value></property></map></property><property name=\"myapp:dateProperty\"><value type=\"time\">20110405-12:14:02.054+0100</value></property><property name=\"myapp:integerProperty\"><value type=\"integer\">678</value></property><property name=\"myapp:intListProperty\"><list><value type=\"integer\">1</value><value type=\"integer\">2</value><value type=\"integer\">3</value><value type=\"integer\">4</value><value type=\"integer\">5</value><value type=\"integer\">6</value></list></property><property name=\"myapp:doubleProperty\"><value type=\"number\">1.324352E14</value></property><property name=\"myapp:longProperty\"><value type=\"integer\">6737436288</value></property><property name=\"myapp:floatProperty\"><value type=\"number\">132.43524</value></property></asset></assets>";
}
