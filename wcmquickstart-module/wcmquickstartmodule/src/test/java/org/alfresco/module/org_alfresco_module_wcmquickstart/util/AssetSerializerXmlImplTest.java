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
package org.alfresco.module.org_alfresco_module_wcmquickstart.util;

import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import junit.framework.TestCase;

public class AssetSerializerXmlImplTest extends TestCase
{
    
    
    private AssetSerializerXmlImpl testObject;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        testObject = new AssetSerializerXmlImpl();
        NamespaceService namespaceService = mock(NamespaceService.class);
        doAnswer(new Answer<Collection<String>>()
                {
                    @Override
                    public Collection<String> answer(InvocationOnMock invocation) throws Throwable
                    {
                        List<String> reply = new ArrayList<String>();
                        reply.add("myapp");
                        return reply;
                    }
                }).when(namespaceService).getPrefixes(any(String.class));
        
        testObject.setNamespaceService(namespaceService);
    }

    public void test1() throws Exception
    {
        StringWriter writer = new StringWriter();
        testObject.start(writer);
        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(QName.createQName("uri", "integerProperty"), new Integer(678));
        props.put(QName.createQName("uri", "longProperty"), new Long(6737436288L));
        props.put(QName.createQName("uri", "dateProperty"), new Date());
        props.put(QName.createQName("uri", "floatProperty"), new Float(132.435243));
        props.put(QName.createQName("uri", "doubleProperty"), new Double(132.4352e12));
        props.put(QName.createQName("uri", "noderefProperty"), 
                new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, GUID.generate()));
        props.put(QName.createQName("uri", "textProperty"), "The радиатор <sat> on the mat: first on one side; the on the other... – ");
        ArrayList<String> textList = new ArrayList<String>();
        textList.add("One");
        textList.add("Two");
        textList.add("Three");
        textList.add("Four");
        textList.add("Five");
        textList.add("Six");
        props.put(QName.createQName("uri", "textListProperty"), textList);

        ArrayList<Integer> intList = new ArrayList<Integer>();
        intList.add(1);
        intList.add(2);
        intList.add(3);
        intList.add(4);
        intList.add(5);
        intList.add(6);
        props.put(QName.createQName("uri", "intListProperty"), intList);
        
        props.put(QName.createQName("uri", "mapProperty"), new HashMap<QName,Serializable>(props));
        
        testObject.writeNode(new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, GUID.generate()), 
                QName.createQName("uri", "MyType"), props);
        testObject.end();
        System.out.println(writer.toString());
    }
}
