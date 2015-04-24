<#assign bgImageUrl = url.context + "/resources/images/logo/AlfrescoFadedBG.png">
<#assign logoImageUrl = url.context + "/resources/images/logo/AlfrescoLogo200.png">
<#assign frameworkTitle = context.frameworkTitle>
<#assign frameworkVersion = context.frameworkVersion>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Getting Started</title>
</head>
<body>
<table width="100%" height="100%" border="0" style="background-image:url('${bgImageUrl}'); background-repeat:no-repeat;">
	<tr>
		<td valign="center" align="middle">
			<img src="${logoImageUrl}"/>
			<br/>
			<b>${frameworkTitle} ${frameworkVersion}</b>
			<br/>
			<br/>
			<br/>
			${frameworkTitle} has been installed at this location.
			<br/>
			A root page has not been defined.
			<br/>
		</td>
	</tr>
</table>

</body>
</html>