function main()
{
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
		if (!isURLValid(uri))
		{
			status.setCode(status.STATUS_BAD_REQUEST, "URL is not valid.");
			return;
		}
		c.properties["webviewURI"] = uri;
		model.uri = uri;
	}

	c.save();
}

function isURLValid(url)
{
    var expression = /(ftp|http|https):\/\/[\w\-_]+(\.[\w\-_]+)*([\w\-\.,@?^=%&:\/~\+#]*[\w\-\@?^=%&\/~\+#])?/;
    // Check an empty string replacement returns an empty string
    return url.replace(expression, "") === "";
}

main();