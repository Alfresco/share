<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

// Grab the wiki pages for the (current) site
var theUrl = "/slingshot/wiki/pages/" + url.templateArgs.siteId; 

model.pageList = doGetCall(theUrl);