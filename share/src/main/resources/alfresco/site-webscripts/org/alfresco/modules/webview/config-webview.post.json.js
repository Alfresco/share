var c = sitedata.getComponent(url.templateArgs.componentId);

var uri = String(json.get("url"));
var webviewTitle = String(json.get("webviewTitle"));
c.properties["webviewTitle"] = webviewTitle;
model.webviewTitle = (webviewTitle == "") ? null : webviewTitle;

if (uri !== "")
{
   var re = /^(http|https):\/\//;
   if (!re.test(uri))
   {
      uri = "http://" + uri;
   }
   c.properties["webviewURI"] = uri;
   model.uri = uri;
}

c.save();
