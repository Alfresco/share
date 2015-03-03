var component = sitedata.getComponent(url.templateArgs.componentId);
if (component != null)
{
   var names = json.names(), name, value;
   for (var i = 0, j = names.length(); i < j; i++)
   {
      name = names.get(i);
      component.properties[name] = String(json.get(name));
   }

   component.save();
}