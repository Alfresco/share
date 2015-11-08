/**
 * Admin Console template controller script
 */

function main()
{
   // product edition information
   var edition = context.properties["editionInfo"].edition;
   
   // return an map of group->tool[] information
   var toolInfo = {};
   
   // the current tool may have been specified on the URL
   var currentToolId = page.url.templateArgs["toolid"];
   
   // family of tools to use for this console is linked to the current pageId from the URL
   var family = page.url.templateArgs["pageid"];
   if (family != null)
   {
      // find the existing current tool component binding
      var component = sitedata.getComponent("page", "ctool", family);
      
      // collect the tools required for this console
      var tools = sitedata.findWebScripts(family);
      
      // process each tool and generate the data so that a label+link can
      // be output by the component template for each tool required
      for (var i = 0; i < tools.length; i++)
      {
         var tool = tools[i],
            id = tool.id,
            scriptName = id.substring(id.lastIndexOf('/') + 1, id.lastIndexOf('.')),
            toolUrl = (new String(tool.getURIs()[0])).toString();
         
         // ensure that the tool is suitable for the product edition
         if (validForEdition(tool, edition))
         {
            // handle the case when no tool selection in the URL - select the first
            if (currentToolId.length == 0)
            {
               currentToolId = scriptName;
            }
            
            // use the webscript ID to generate message bundle IDs
            var labelId = "tool." + scriptName + ".label",
                descId = "tool." + scriptName + ".description";
            
            // identify console tool grouping if any
            // simple convention is used to resolve group - last element of the webscript package path after 'console'
            // for example: org.alfresco.components.console.repository = repository
            //              org.yourcompany.console.mygroup = mygroup
            // package paths not matching the convention will be placed in the default root group
            // the I18N label should be named: tool.group.<yourgroupid>
            var group = "",
                groupLabelId = null,
                paths = tool.scriptPath.split('/');
            if (paths.length > 4 && paths[3] == "console")
            {
               // found webscript package grouping
               group = paths[4];
               groupLabelId = "tool.group." + group;
            }
            
            var info =
            {
               id: scriptName,
               url: toolUrl,
               label: labelId,
               group: group,
               groupLabel: groupLabelId,
               description: descId,
               selected: (currentToolId == scriptName)
            };
            
            // generate the tool info structure for template usage
            if (!toolInfo[group])
            {
               // add initial group structure
               toolInfo[group] = [];
            }
            toolInfo[group].push(info);
            
            // dynamically update the component binding if this tool is the current selection
            if (info.selected)
            {
               if (component == null)
               {
                  // first ever visit to the page - there is no component binding yet
                  component = sitedata.newComponent("page", "ctool", family);
               }
               
               if (component.properties.url != toolUrl)
               {
                  component.properties.url = toolUrl;
                  component.save(false);
               }
            }
         }
      }
   }
   
   // Save the tool info structure into the request context, it is used
   // downstream by the console-tools component to dynamically render tool links.
   // Processing is performed here as the component binding must be set before rendering begins!
   var toolsArray = [];
   if (toolInfo[""])
   {
      // add system root group tools first to the list
      toolsArray.push(toolInfo[""]);
   }
   delete toolInfo[""];
   // add other tool groups after root tools
   for each (var g in toolInfo)
   {
      toolsArray.push(g);
   }
   context.setValue("console-tools", toolsArray);
}

/**
 * Helper to return if a tool is valid for a given product edition
 * 
 * @param tool {object} WebScript tool to test
 * @param edition {string} product edition to test against such as TEAM or ENTERPRISE
 * @return {boolean} true if valid and should be displayed, false otherwise
 */
function validForEdition(tool, edition)
{
   var valid = true,
       familys = tool.getFamilys();
   for each (var family in familys)
   {
      // we are only interested if an edition is specified, else it is a valid tool for all
      if (family.match("^edition:"))
      {
         // at least one edition specified - begin matching process
         valid = false;
         // test that the supplied edition matches an edition family
         for each (var f in familys)
         {
            // test if edition or sub-edition (edition and constraint)
            if (f.match("^edition:" + edition + "$") || (f.match("^edition:" + edition + "-CLOUDSYNCPREM$") && (syncMode.getValue() == "ON_PREMISE")))
            {
               valid = true;
               break;
            }
         }
         break;
      }
   }
   return valid;
}

main();