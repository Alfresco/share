if (typeof Surf == "undefined")
{
	var Surf = {};
}

/*
String.prototype.startsWith = function(str)
{
	return (this.match("^"+str) == str);
};
*/

Surf.ModelObject = {};

Surf.ModelObject.readFromJson = function(object, json)
{
	// set id
	if (json.id)
	{
		object.setId(json.id);
	}
	
	// properties
	if (json.properties)
	{
		for (var name in json.properties)
		{
			if (name != "resources")
			{
				var value = json.properties[name];
				if (value != null)
				{
					object.properties[name] = value;
				}
			}
		}
	}
	
	// resources
	if (json.resources)
	{
		for (var name in json.resources)
		{
			var resourceConfig = json.resources[name];
			if (resourceConfig != null)
			{
				var resource = object.getResources().get(name);
				if (resource == null)
				{
					resource = object.getResources().add(name);
				}
				
				for (var attributeName in resourceConfig)
				{
					if (attributeName == "value")
					{
						resource.setValue(resourceConfig[attributeName]);
					}
					else
					{
						resource.setAttribute(attributeName, resourceConfig[attributeName]);
					}
				}
				
			}
		}
	}
};
