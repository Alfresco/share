package org.alfresco.wcm.client.controller;

import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.Query;
import org.alfresco.wcm.client.SearchResults;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.SectionFactory;
import org.alfresco.wcm.client.WebSiteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * SearchFormController processes the search field request.
 * 
 * @author Chris Lack
 */
@Controller
@RequestMapping("/**/search.html")
public class SearchFormController
{
    private AssetFactory assetFactory;
    private SectionFactory sectionFactory;

    @RequestMapping(method = RequestMethod.GET)
    protected String search(Query query, Model model)
    {
        if (query.getSectionId() == null)
        {
            query.setSectionId(WebSiteService.getThreadWebSite().getRootSection().getId());
        }
        // Perform the search
        SearchResults results = assetFactory.findByQuery(query);

        // Get the section name to display on the results page.
        Section section = sectionFactory.getSection(query.getSectionId());
        if (section.getContainingSection() != null)
        { // Leave null if it's the root and the page will display something
          // suitable.
            model.addAttribute("sectionTitle", section.getTitle() != null ? section.getTitle() : section.getName());
        }

        // Store the results in the Spring model.
        model.addAttribute("results", results);
        
        //return the name of the view to render...
        return "search";
    }

    public void setAssetFactory(AssetFactory assetFactory)
    {
        this.assetFactory = assetFactory;
    }

    public void setSectionFactory(SectionFactory sectionFactory)
    {
        this.sectionFactory = sectionFactory;
    }
}
