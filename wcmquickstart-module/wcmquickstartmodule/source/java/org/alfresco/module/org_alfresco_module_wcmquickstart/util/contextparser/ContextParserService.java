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
package org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Context parser service.
 * 
 * @author Roy Wetherall
 */
public class ContextParserService
{
	/** Pattern matcher */
	private static final Pattern MATCH_PATTERN = Pattern.compile("\\$\\{(.+)\\}"); 
	
	/** Context parser map */
	private Map<String, ContextParser> contextParsers = new TreeMap<String, ContextParser>();
	
	/**
	 * Registers a new context parser
	 * @param queryParser query parser
	 */
	public void register(ContextParser queryParser)
	{
		contextParsers.put(queryParser.getName(), queryParser);
	}
	
	/**
	 * 
	 * @param context
	 * @param value
	 * @return
	 */
	public String parse(NodeRef context, String value)
	{		
		String result = value;	
		Collection<ContextParser> parsers = contextParsers.values();
		
		// Get a Matcher based on the target string. 
		Matcher matcher = MATCH_PATTERN.matcher(value); 

		// Find all the matches. 
		while (matcher.find() == true) 
		{ 
			String invocation = matcher.group(1).trim();
			for (ContextParser parser : parsers)
			{
			    if (parser.canHandle(invocation))
			    {
	                String temp = parser.execute(context, invocation);
	                if (temp != null)
	                {
	                    result = result.replace(matcher.group(), temp);
	                }
	                break;
			    }
			}
		}	
		
		return result;
	}
}
