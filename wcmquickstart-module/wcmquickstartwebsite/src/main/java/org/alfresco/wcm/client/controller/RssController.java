package org.alfresco.wcm.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * RssController selects the rssfeed Surf page for rss.xml URLs 
 * @author Chris Lack
 */
@Controller
@RequestMapping("**/rss.xml")
public class RssController
{
    @RequestMapping(method=RequestMethod.GET)
	protected String handleGet()
    {
    	return "rssfeed"; // Surf page name
    }    
}
