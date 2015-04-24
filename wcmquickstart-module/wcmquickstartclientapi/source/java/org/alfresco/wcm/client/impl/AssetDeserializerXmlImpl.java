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
package org.alfresco.wcm.client.impl;

import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AssetDeserializerXmlImpl extends DefaultHandler implements WebscriptResponseHandler
{
    private static enum State
    {
        not_started, header, assets, asset, property, list, map, value, content
    }

    private static enum ValueType
    {
        id, text, integer, number, bool, time, content, missing
    };

    private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue()
        {
            return new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSSZ");
        }
    };

    private LinkedList<TreeMap<String, Serializable>> assets;
    private TreeMap<String, Serializable> assetProperties;
    private TreeMap<String, Serializable> headerProperties;
    private Stack<State> previousStates;
    private Stack<String> previousPropertyNames;
    private Stack<TreeMap<String, Serializable>> previousPropertyMaps;
    private State currentState;
    private StringBuilder stringBuilder;
    private String propertyName;
    private ArrayList<Serializable> valueList;
    private ValueType valueType;
    private Serializable value;

    public AssetDeserializerXmlImpl()
    {

    }

    public void reset()
    {
        assets = new LinkedList<TreeMap<String, Serializable>>();
        headerProperties = new TreeMap<String, Serializable>();
        assetProperties = null;
        previousStates = new Stack<State>();
        previousPropertyMaps = new Stack<TreeMap<String,Serializable>>();
        previousPropertyNames = new Stack<String>();
        currentState = State.not_started;
        stringBuilder = null;
        propertyName = null;
        valueList = null;
        value = null;
    }

    public LinkedList<TreeMap<String, Serializable>> deserialize(InputStream in)
    {
        try
        {
            reset();
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser parser = saxParserFactory.newSAXParser();
            parser.parse(in, this);
        }
        catch (Exception ex)
        {

        }
        return assets;
    }

    @Override
    public void handleResponse(InputStream in)
    {
        deserialize(in);
    }
    

    /**
     * Retrieve the list of assets that were deserialized. Never null, may be empty.
     * Note that concrete collection classes used in the return value to guarantee they're
     * serializable.
     * @return
     */
    public LinkedList<TreeMap<String, Serializable>> getAssets()
    {
        return assets;
    }

    /**
     * Retrieve any properties that were deserialized from the header of the response. Never null, may be empty.
     * Note that the concrete collection class used in the return value is to guarantee that it's
     * serializable.
     * @return
     */
    public TreeMap<String, Serializable> getHeader()
    {
        return headerProperties;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        if (stringBuilder != null)
        {
            stringBuilder.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if (!qName.equals(currentState.toString()))
        {
            invalidElement(qName);
        }
        else
        {
            switch(currentState)
            {
            case value:
                createValueObject();
                State previousState = previousStates.peek();
                if (previousState == State.list)
                {
                    valueList.add(value);
                }
                break;
                
            case list:
                value = valueList;
                break;
                
            case map:
                propertyName = previousPropertyNames.pop();
                value = assetProperties;
                assetProperties = previousPropertyMaps.pop();
                break;
            
            case property:
                assetProperties.put(propertyName, value);
                break;
                
            case asset:
                assets.add(assetProperties);
                break;
                
            case header:
                headerProperties = assetProperties;
                break;
                
            case content:
            case assets:
            case not_started:
                break;
                
            }
            currentState = previousStates.pop();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        switch (currentState)
        {
        case not_started:
            if ("assets".equals(qName))
            {
                previousStates.push(currentState);
                currentState = State.assets;
            }
            break;

        case assets:
            if ("asset".equals(qName))
            {
                assetProperties = new TreeMap<String, Serializable>();
                assetProperties.put("id", attributes.getValue("id"));
                assetProperties.put("type", attributes.getValue("type"));
                previousStates.push(currentState);
                currentState = State.asset;
            }
            else if ("header".equals(qName))
            {
                assetProperties = new TreeMap<String, Serializable>();
                previousStates.push(currentState);
                currentState = State.header;
            }
            break;

        case header:
        case asset:
            if ("property".equals(qName))
            {
                propertyName = attributes.getValue("name");
                previousStates.push(currentState);
                currentState = State.property;
            }
            break;
            
        case property:
            if ("value".equals(qName))
            {
                valueType = ValueType.valueOf(attributes.getValue("type"));
                value = null;
                stringBuilder = new StringBuilder();
                previousStates.push(currentState);
                currentState = State.value;
            }
            else if ("list".equals(qName))
            {
                valueList = new ArrayList<Serializable>();
                previousStates.push(currentState);
                currentState = State.list;
            }
            else if ("map".equals(qName))
            {
                previousPropertyMaps.push(assetProperties);
                assetProperties = new TreeMap<String, Serializable>();
                previousPropertyNames.push(propertyName);
                previousStates.push(currentState);
                currentState = State.map;
            }
            break;

        case value:
            if ("content".equals(qName))
            {
                ContentInfo info = new ContentInfo();
                info.setMimeType(attributes.getValue("mime"));
                info.setEncoding(attributes.getValue("enc"));
                info.setSize(Long.parseLong(attributes.getValue("size")));
                value = info;
                previousStates.push(currentState);
                currentState = State.content;
            }
            break;

        case list:
            if ("value".equals(qName))
            {
                valueType = ValueType.valueOf(attributes.getValue("type"));
                value = null;
                stringBuilder = new StringBuilder();
                previousStates.push(currentState);
                currentState = State.value;
            }
            break;

        case map:
            if ("property".equals(qName))
            {
                propertyName = attributes.getValue("name");
                previousStates.push(currentState);
                currentState = State.property;
            }
            break;

        default:
            invalidElement(qName);
            break;
        }
    }

    private void invalidElement(String qName) throws SAXException
    {
        String msg = "Unexpected element \"" + qName + "\" received. Current state is \"" + currentState + 
                "\". Previous states are: " + previousStates;
        throw new SAXException(msg);
    }

    private void createValueObject()
    {
        if (value == null)
        {
            //Work out what the value should be
            String valueText = stringBuilder.toString();
            switch (valueType)
            {
            case bool:
                value = Boolean.valueOf(valueText);
                break;
                
            case id:
            case text:
                value = valueText;
                break;
                
            case time:
                try
                {
                    value = dateFormat.get().parse(valueText);
                }
                catch (ParseException ex)
                {
                    //value will be null
                }
                break;
                
            case integer:
                value = Long.valueOf(valueText);
                break;
                
            case number:
                value = Double.valueOf(valueText);
                break;
                
            default:
                //content's already been handled and "missing" maps to null
                break;
            }
        }
    }
}
