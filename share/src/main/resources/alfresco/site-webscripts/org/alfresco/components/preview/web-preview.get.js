<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function getPluginConditions(xmlConfig)
{
   // Create a json representation of the conditions that will be used to decide which previewer that shall be used
   var pluginConditions = [], conditionNode, pluginNode, condition, plugin, attribute;
   if (xmlConfig && xmlConfig["plugin-conditions"])
   {
      for each (conditionNode in xmlConfig["plugin-conditions"].elements("condition"))
      {
         condition =
         {
            attributes: {},
            plugins: []
         };

         for each (attribute in conditionNode.attributes())
         {
            condition.attributes[attribute.name()] = attribute.text();
         }

         for each (pluginNode in conditionNode.elements("plugin"))
         {
            plugin =
            {
               name: pluginNode.text(),
               attributes: {}
            };
            for each (attribute in pluginNode.attributes())
            {
               plugin.attributes[attribute.name()] = attribute.text();
            }
            condition.plugins.push(plugin);
         }
         pluginConditions.push(condition);
      }
      return pluginConditions;
   }
}

function getNodeMetadata(proxy, api, nodeRef)
{

   var result = remote.connect(proxy).get("/" + api + "/node/" + nodeRef.replace(/:\//g, "") + "/metadata"),
      node;
   if (result.status == 200)
   {
      var nodeMetadata = JSON.parse(result);
      node = {};
      node.name = nodeMetadata.name || nodeMetadata.title;
      node.mimeType = nodeMetadata.mimetype;
      node.size = nodeMetadata.size || "0";
      node.thumbnailModifications = nodeMetadata.lastThumbnailModificationData;
      node.thumbnails = nodeMetadata.thumbnailDefinitions;
   }
   return node;
}

function main()
{
   // Populate model with parameters
   AlfrescoUtil.param("nodeRef");
   AlfrescoUtil.param("api", "api");
   AlfrescoUtil.param("proxy", "alfresco");

   // Populate model with data from repo
   var nodeMetadata = getNodeMetadata(model.proxy, model.api, model.nodeRef);
   if (nodeMetadata)
   {
      // Populate model with data from node and config
      model.node = true;
      var pluginConditions = getPluginConditions(new XML(config.script));
      var pluginConditionsJSON = jsonUtils.toJSONString(pluginConditions);

      // Widget instantiation metadata...
      var webPreview = {
         id : "WebPreview",
         name : "Alfresco.WebPreview",
         options : {
            siteId: this.page ? page.url.templateArgs.site : (args.site != null ? args.site : ""),
            thumbnailModification : nodeMetadata.thumbnailModifications,
            nodeRef : model.nodeRef,
            name : nodeMetadata.name,
            mimeType : nodeMetadata.mimeType,
            size: nodeMetadata.size,
            thumbnails : nodeMetadata.thumbnails,
            pluginConditions : pluginConditionsJSON,
            api: model.api,
            proxy: model.proxy
         }
      };
      model.widgets = [webPreview];
   }
}

// Start the webscript
main();

// Set the group from the component property...
model.dependencyGroup =  (args.dependencyGroup != null) ? args.dependencyGroup : "web-preview";
