<#assign bgImageUrl = url.context + "/resources/images/logo/AlfrescoFadedBG.png">
<#assign homePageUrl = url.context>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Content Not Loaded</title>
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
			The content was not able to be retrieved.
			<br/>
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

