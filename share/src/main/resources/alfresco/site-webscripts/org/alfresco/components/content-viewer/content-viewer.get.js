// Load the config to know what viewer to use for each mimetype
var contentviewer =  new XML(config.script);

// Check mandatory parameters
var nodeRef = page.url.args.nodeRef;
if (nodeRef == null || nodeRef.length == 0)
{
   status.code = 400;
   status.message = "Parameter 'nodeRef' is missing.";
   status.redirect = true;
}

// Call the repo for metadata
var json = remote.call("/api/metadata?nodeRef=" + nodeRef)
var node = JSON.parse(json);

/**
 * If no viewer is configured for the nodes mime-type display
 * a fallback template with an explanation text
 */
var viewer = "viewers/no-viewer-exist.ftl";
var preload = false;
var contentData = null;

var content = contentviewer.content.(@mimetype==node.mimetype);
if (content.length() == 1)
{
   // Found a configured viewer for the mimetype
   viewer = content.@viewer.toString();
   preload = (content.@preload.toString() == "true");
}
else if (content.length() > 1)
{
   // Multiple viewers found for the same mimetype, throw an error
   status.code = 500;
   status.message = "Multiple viewers (" + content.@viewer.toString() + ") defined for the mime-type ${node.mimetype}";
   status.redirect = true;
}

// property namespaces
var mcns = "{http://www.alfresco.org/model/content/1.0}";
var msns = "{http://www.alfresco.org/model/system/1.0}";

// extract metadata
var storeType = node.properties[msns + "store-protocol"];
var storeId = node.properties[msns + "store-identifier"];
var nodeId = node.properties[msns + "node-uuid"];
var name = node.properties[mcns + "name"];

var contentUrl = "/api/node/content/" +storeType+ "/" +storeId + "/" + nodeId + "?" + encodeURIComponent(name)
if (preload)
{
   /**
    * This mimetype can be displayed inline and has therefore been configured
    * to be loaded directly instead from the browser.
    */
   contentData = remote.call(contentUrl);
}

// Prepare model for template
model.viewer = viewer;
model.node = node;
model.contentData = contentData;
model.contentUrl = page.url.context + "/service" + contentUrl;

// Set the width
model.width = "100%"; // default value
if (node.properties[mcns + "width"])
{
   model.width = node.properties[mcns + "width"];
}
else if (args.width)
{
   model.width = args.width;
}
else if (content.@width.toString().length > 0)
{
   model.width = content.@width.toString();
}

// Set the height
model.height = "100%"; // default value
if (node.properties[mcns + "height"])
{
   model.height = node.properties[mcns + "height"];
}
else if (args.height)
{
   model.height = args.height;
}
else if (content.@height.toString().length > 0)
{
   model.height = content.@height.toString();
}
