<#assign bgImageUrl = url.context + "/resources/images/logo/AlfrescoFadedBG.png">
<#assign homePageUrl = url.context>
<#assign resource = context.properties["resource"]>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Content Association Missing</title>
    <style>
		a:link              { color:red; text-decoration:underline; }
    	a:visited           { color:red; text-decoration:underline; }
    	a:hover             { color:blue; text-decoration:underline; }
    	a:active            { color:red; text-decoration:underline; }
    </style>
</head>
<body>
<table width="100%" height="100%" border="0" style="background-image:url('${bgImageUrl}'); background-repeat:no-repeat;">
	<tr>
		<td align="center" valign="center" height="100%">
		
			The content being viewed is not associated to a display page.
			<br/>
			<br/>
			Resource ID:<br/>${resource.id}<br/>
			<br/>
			Protocol ID:<br/>${resource.protocolId}<br/>
			Endpoint ID:<br/>${resource.endpointId}<br/>
			Object ID:<br/>${resource.objectId}<br/>
			<br/>
			Object Type ID:<br/>${resource.objectTypeId}</br>
			<br/>
			<a href="javascript:window.history.back()">Go back to previous page</a>
			<br/>
			<br/>
			<a href="${homePageUrl}">Go to the home page</a>
		</td>
	</tr>
</table>

</body>
</html>

