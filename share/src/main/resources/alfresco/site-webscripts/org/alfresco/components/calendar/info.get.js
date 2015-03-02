<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

var uri = unescape(args['uri']);
model.result = doGetCall(uri);