<#if url.args?contains("debug=true")><#assign debug="true"><#else><#assign debug="false"></#if>

if (typeof Alfresco == "undefined" || !Alfresco)
{
   var Alfresco = {};
}
Alfresco.constants = Alfresco.constants || {};
Alfresco.constants.DEBUG = ${debug};
Alfresco.constants.AUTOLOGGING = ${debug};
Alfresco.constants.PROXY_URI = window.location.protocol + "//" + window.location.host + "${url.context}/proxy/alfresco/";
Alfresco.constants.PROXY_URI_RELATIVE = "${url.context}/proxy/alfresco/";
Alfresco.constants.THEME = "sam";
Alfresco.constants.URL_CONTEXT = "${url.context}/";
Alfresco.constants.URL_RESCONTEXT = "${url.context}/res/";
Alfresco.constants.URL_PAGECONTEXT = "${url.context}/page/";
Alfresco.constants.URL_SERVICECONTEXT = "${url.context}/service/";
Alfresco.constants.USERNAME = "${user.name!""}";
Alfresco.constants.HTML_EDITOR = "tinyMCE";
Alfresco.constants.TINY_MCE_SUPPORTED_LOCALES = "${config.global["I18N"].getChildValue("tiny-mce-supported-locales")}";
