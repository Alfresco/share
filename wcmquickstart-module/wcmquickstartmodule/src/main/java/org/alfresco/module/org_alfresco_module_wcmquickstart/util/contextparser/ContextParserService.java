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
	 * @param context NodeRef
	 * @param value String
	 * @return String
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
