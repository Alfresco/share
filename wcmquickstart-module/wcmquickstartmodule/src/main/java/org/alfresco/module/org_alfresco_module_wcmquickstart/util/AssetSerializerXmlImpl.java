/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.module.org_alfresco_module_wcmquickstart.util;

import java.io.Serializable;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ApplicationModel;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.FlyweightCDATA;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class AssetSerializerXmlImpl implements AssetSerializer
{
    private static enum ValueType
    {
        id, text, integer, number, bool, time, content, missing
    };

    private final static AttributesImpl EMPTY_ATTRIBUTES = new AttributesImpl();
    private static Set<QName> DEFAULT_PROPERTIES_TO_IGNORE;
    private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>()
    {
        @Override
        protected DateFormat initialValue()
        {
            return new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSSZ");
        }
    };

    static
    {
        Set<QName> ignoreSet = new HashSet<QName>();
        ignoreSet.add(ContentModel.PROP_STORE_IDENTIFIER);
        ignoreSet.add(ContentModel.PROP_STORE_NAME);
        ignoreSet.add(ContentModel.PROP_STORE_PROTOCOL);
        ignoreSet.add(ContentModel.PROP_NODE_DBID);
        ignoreSet.add(ContentModel.PROP_NODE_REF);
        ignoreSet.add(ContentModel.PROP_NODE_UUID);
        ignoreSet.add(ContentModel.PROP_TAGS);
        ignoreSet.add(ApplicationModel.PROP_EDITINLINE);
        ignoreSet.add(WebSiteModel.PROP_ANCESTOR_SECTIONS);
        DEFAULT_PROPERTIES_TO_IGNORE = Collections.unmodifiableSet(ignoreSet);
    }

    private XMLWriter writer;
    private NamespaceService namespaceService;
    private Set<QName> propertiesToIgnore = DEFAULT_PROPERTIES_TO_IGNORE;

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.alfresco.module.org_alfresco_module_wcmquickstart.webscript.
     * AssetSerializer#start(java.io.Writer)
     */
    public void start(Writer underlyingWriter) throws AssetSerializationException
    {
        try
        {
            OutputFormat format = OutputFormat.createCompactFormat();
            format.setEncoding("UTF-8");

            writer = new XMLWriter(underlyingWriter, format);
            writer.startDocument();
            startElement("assets", EMPTY_ATTRIBUTES);
        }
        catch (Exception ex)
        {
            throw new AssetSerializationException(ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.alfresco.module.org_alfresco_module_wcmquickstart.webscript.
     * AssetSerializer#end()
     */
    public void end() throws AssetSerializationException
    {
        try
        {
            endElement("assets");
        }
        catch (Exception ex)
        {
            throw new AssetSerializationException(ex);
        }
    }

    @Override
    public void writeHeader(Map<QName, Serializable> properties) throws AssetSerializationException
    {
        try
        {
            startElement("header", EMPTY_ATTRIBUTES);
            for (Map.Entry<QName, Serializable> property : properties.entrySet())
            {
                if (!propertiesToIgnore.contains(property.getKey()))
                {
                    writeProperty(property.getKey(), property.getValue());
                }
            }
            endElement("header");
        }
        catch (Exception ex)
        {
            throw new AssetSerializationException(ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.alfresco.module.org_alfresco_module_wcmquickstart.webscript.
     * AssetSerializer#writeNode(org.alfresco.service.cmr.repository.NodeRef,
     * org.alfresco.service.namespace.QName, java.util.Map)
     */
    public void writeNode(NodeRef nodeRef, QName type, Map<QName, Serializable> properties)
            throws AssetSerializationException
    {
        try
        {
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute(null, "id", "id", "String", nodeRef.toString());
            attributes.addAttribute(null, "type", "type", "String", type.toPrefixString(namespaceService));
            startElement("asset", attributes);
            for (Map.Entry<QName, Serializable> property : properties.entrySet())
            {
                if (!propertiesToIgnore.contains(property.getKey()))
                {
                    writeProperty(property.getKey(), property.getValue());
                }
            }
            endElement("asset");
        }
        catch (Exception ex)
        {
            throw new AssetSerializationException(ex);
        }
    }

    private void writeProperty(QName name, Object value) throws Exception
    {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute(null, "name", "name", "String", name.toPrefixString(namespaceService));
        startElement("property", attrs);
        writeValue(value);
        endElement("property");
    }

    @SuppressWarnings("unchecked")
    private void writeValue(Object value) throws Exception
    {
        Class<?> valueClass = (value == null) ? null : value.getClass();
        if (valueClass != null && List.class.isAssignableFrom(valueClass))
        {
            List<?> listValue = (List<?>) value;
            startElement("list", EMPTY_ATTRIBUTES);
            for (Object element : listValue)
            {
                writeValue(element);
            }
            endElement("list");
        }
        else if (valueClass != null && Map.class.isAssignableFrom(valueClass))
        {
            Map<QName,?> mapValue = (Map<QName,?>) value;
            startElement("map", EMPTY_ATTRIBUTES);
            for (Map.Entry<QName, ?> element : mapValue.entrySet())
            {
                writeProperty(element.getKey(), element.getValue());
            }
            endElement("map");
        }
        else
        {
            ValueType valueType = getValueType(value);
            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute(null, "type", "type", "String", valueType.toString());
            startElement("value", attrs);
            writeValue(valueType, value);
            endElement("value");
        }
    }

    private void writeValue(ValueType valueType, Object value) throws Exception
    {
        switch (valueType)
        {
        case id:
        case integer:
        case bool:
        case number:
            writer.write(value.toString());
            break;

        case text:
            writer.write(new FlyweightCDATA(value.toString()));
            break;

        case content:
            ContentData contentData = (ContentData) value;
            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute(null, "mime", "mime", "String", contentData.getMimetype());
            attrs.addAttribute(null, "size", "size", "String", Long.toString(contentData.getSize()));
            attrs.addAttribute(null, "enc", "enc", "String", contentData.getEncoding());
            startElement("content", attrs);
            endElement("content");
            break;

        case time:
            writer.write(getDateFormat().format((Date) value));
            break;

        case missing:
            // write nothing
            break;
        }
    }

    private DateFormat getDateFormat()
    {
        return dateFormat.get();
    }

    private void startElement(String elementName, Attributes attributes) throws SAXException
    {
        writer.startElement(null, elementName, elementName, attributes);
    }

    private void endElement(String elementName) throws SAXException
    {
        writer.endElement(null, elementName, elementName);
    }

    private ValueType getValueType(Object value)
    {
        ValueType valueType = ValueType.text;
        if (value == null)
        {
            valueType = ValueType.missing;
        }
        else
        {
            Class<?> valueClass = value.getClass();
            if (String.class.isAssignableFrom(valueClass))
            {
                valueType = ValueType.text;
            }
            if (MLText.class.isAssignableFrom(valueClass))
            {
                valueType = ValueType.text;
            }
            else if (Integer.class.isAssignableFrom(valueClass))
            {
                valueType = ValueType.integer;
            }
            else if (Long.class.isAssignableFrom(valueClass))
            {
                valueType = ValueType.integer;
            }
            else if (ContentData.class.isAssignableFrom(valueClass))
            {
                valueType = ValueType.content;
            }
            else if (Float.class.isAssignableFrom(valueClass))
            {
                valueType = ValueType.number;
            }
            else if (Double.class.isAssignableFrom(valueClass))
            {
                valueType = ValueType.number;
            }
            else if (Date.class.isAssignableFrom(valueClass))
            {
                valueType = ValueType.time;
            }
            else if (Boolean.class.isAssignableFrom(valueClass))
            {
                valueType = ValueType.bool;
            }
            else if (NodeRef.class.isAssignableFrom(valueClass))
            {
                valueType = ValueType.id;
            }
            else
            {
                valueType = ValueType.text;
            }
        }
        return valueType;
    }

    @Override
    public String getMimeType()
    {
        return "text/xml";
    }
}
