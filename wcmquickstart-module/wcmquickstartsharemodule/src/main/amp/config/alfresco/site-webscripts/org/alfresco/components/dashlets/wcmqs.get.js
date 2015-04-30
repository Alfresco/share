function main()
{
   // Check to see if the web site data has already been loaded or not
   var conn = remote.connect("alfresco");
   var res = conn.get("/api/loadwebsitedata?site=" + page.url.templateArgs.site + "&preview=true");
   var jsonData = JSON.parse(res);   
   model.dataloaded = !jsonData.success;
   if (model.dataloaded == false)
   {
	   var keys = new Array();
	   var labels = new Array();
	   var count = 0;
	   for (var key in jsonData.importids)
	   {
		   var label = jsonData.importids[key];
		   keys[count] = key;
		   labels[count] = label;
		   count ++;
	   }   
	   
	   model.importids = keys;
	   model.importidlabels = labels;		   
   }
}

main();