var dashboardURL = json.get("dashboardURL");

var componentId = json.get("componentId");
var component = sitedata.getComponent(componentId);

if (component)
{
   var regionId = component.properties["region-id"]; 
   sitedata.unbindComponent("page", regionId, dashboardURL);
   
   var pos = getComponentPosition(component);
   
   var components = sitedata.findComponents("page", null, dashboardURL, null);
   var re = /^component-(\d+)-(\d+)$/;
   var c, coord;
   for (var idx=0; idx < components.length; idx++)
   {
      c = components[idx];
      if (!re.test(c.properties["region-id"]))
      {
         continue;
      }
      
      coord = getComponentPosition(c);
     
      if (coord.column > pos.column)
      {
         continue; // the order of the components is not guaranteed so we need to continue
      }
      
      if (coord.column === pos.column && coord.row > pos.row)
      {
         // reposition component - move it up one row
         c.properties["region-id"] = "component-" + pos.column + "-" + (coord.row-1);
         c.save();
      }
   }
   model.msg = "Success";  
}
else
{
   model.msg = "Failed";
}

/**
 * Takes a dashboard component and returns the column / row properties.
 *
 * @return {Object} "row" and "column" properties 
 */
function getComponentPosition(component)
{
   var re = /^component-(\d+)-(\d+)$/;
   var regionId = component.properties["region-id"];
  
   var result = regionId.match(re);
  
   return {
      row: result[2],
      column: result[1]
   };   
}

