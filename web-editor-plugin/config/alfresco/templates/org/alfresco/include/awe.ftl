<#function globalConfig key default>
   <#if config.global.flags??>
      <#assign values = config.global.flags.childrenMap[key]>
      <#if values?? && values?is_sequence>
         <#return values[0].value>
      </#if>
   </#if>
   <#return default>
</#function>

<#-- Global flags retrieved from web-framework-config-application -->
<#assign DEBUG=(globalConfig("client-debug", "false") = "true")>
<#assign AUTOLOGGING=(globalConfig("client-debug-autologging", "false") = "true")>
<#-- allow theme to be specified in url args - helps debugging themes -->
<#assign theme = (page.url.args.theme)!theme />

<#-- Look up page title from message bundles where possible -->
<#assign pageTitle = page.title />
<#if page.titleId??>
   <#assign pageTitle = (msg(page.titleId))!page.title>
</#if>
<#if context.properties["page-titleId"]??>
   <#assign pageTitle = msg(context.properties["page-titleId"])>
</#if>

<#--
   JavaScript minimisation via YUI Compressor.
-->
<#assign minJS=DEBUG?string(".js", "-min.js")>
<#macro script type src>
   <script type="${type}" src="${src?replace(".js", minJS)}"></script>
</#macro>

<#--
   Stylesheets gathered and rendered using @import to workaround IEBug KB262161
-->
<#assign templateStylesheets = []>
<#macro link rel type href>
   <#assign templateStylesheets = templateStylesheets + [href]>
</#macro>
<#macro renderStylesheets>
   <style type="text/css" media="screen">
   <#list templateStylesheets as href>
      @import "${href}";
   </#list>
   </style>
</#macro>

<#--
   Template "templateHeader" macro.
   Includes preloaded YUI assets and essential site-wide libraries.
-->                                                                           
<#macro templateHeader doctype="strict">
   <#if doctype = "strict">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
   <#else>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
   </#if>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <title>Alfresco Web Editor &raquo; ${pageTitle}</title>
   <meta http-equiv="X-UA-Compatible" content="Edge" />

<!-- Shortcut Icons -->
   <link rel="shortcut icon" href="${url.context}/favicon.ico" type="image/vnd.microsoft.icon" /> 
   <link rel="icon" href="${url.context}/favicon.ico" type="image/vnd.microsoft.icon" />

<!-- Site-wide YUI Assets -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/reset-fonts-grids/reset-fonts-grids.css" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/assets/skins/sam/skin.css" />
<#-- Selected components preloaded here for better UI experience. -->
<#if DEBUG>
<!-- log4javascript -->
   <script type="text/javascript" src="${url.context}/res/js/log4javascript.v1.4.1.js"></script>
<!-- Common YUI components: DEBUG -->
   <script type="text/javascript" src="${url.context}/res/yui/yahoo/yahoo-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/event/event-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/dom/dom-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/dragdrop/dragdrop-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/animation/animation-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/logger/logger-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/connection/connection-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/element/element-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/get/get-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/yuiloader/yuiloader-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/button/button-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/container/container-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/menu/menu-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/json/json-debug.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/selector/selector-debug.js"></script>
<#else>
<!-- Common YUI components: RELEASE -->
   <script type="text/javascript" src="${url.context}/res/yui/utilities/utilities.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/button/button-min.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/container/container-min.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/menu/menu-min.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/json/json-min.js"></script>
   <script type="text/javascript" src="${url.context}/res/yui/selector/selector-min.js"></script>
</#if>

<!-- Site-wide Common Assets -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/awe/awe.css" />  
   <@script type="text/javascript" src="${url.context}/res/js/bubbling.v2.1.js"></@script>
   <#-- NOTE: Do not attempt to load -min.js version of messages.js -->
   <script type="text/javascript" src="${url.context}/service/messages.js?locale=${locale}"></script>
   <script type="text/javascript">//<![CDATA[
      Alfresco.constants = Alfresco.constants || {};
      Alfresco.constants.DEBUG = ${DEBUG?string};
      Alfresco.constants.AUTOLOGGING = ${AUTOLOGGING?string};
      Alfresco.constants.PROXY_URI = window.location.protocol + "//" + window.location.host + "${url.context}/proxy/alfresco/";
      Alfresco.constants.PROXY_URI_RELATIVE = "${url.context}/proxy/alfresco/";
      Alfresco.constants.THEME = "${theme}";
      Alfresco.constants.URL_CONTEXT = "${url.context}/";
      Alfresco.constants.URL_RESCONTEXT = "${url.context}/res/";
      Alfresco.constants.URL_PAGECONTEXT = "${url.context}/page/";
      Alfresco.constants.URL_SERVICECONTEXT = "${url.context}/service/";
      Alfresco.constants.USERNAME = "${user.name!""}";
      Alfresco.constants.HTML_EDITOR = "tinyMCE";
   //]]></script>
   <@script type="text/javascript" src="${url.context}/res/js/alfresco.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/js/forms-runtime.js"></@script>

<!-- Template Assets -->
<#nested>
<@renderStylesheets />

<!-- Component Assets -->
${head}

</head>
</#macro>


<#--
   Template "templateHtmlEditorAssets" macro.
   Loads wrappers for Rich Text editors.
-->
<#macro templateHtmlEditorAssets>
<!-- HTML Editor Assets -->
   <#-- NOTE: Use preminified version of tinymce/tinymce.js -->
   <script type="text/javascript" src="${url.context}/res/modules/editors/tinymce/tinymce.min.js"></script>
   <@script type="text/javascript" src="${url.context}/res/modules/editors/tiny_mce.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/modules/editors/yui_editor.js"></@script>
</#macro>


<#--
   Template "templateBody" macro.
   Pulls in main template body.
-->
<#macro templateBody>
<body id="WebEditor" class="yui-skin-${theme}">
   <div class="sticky-wrapper">
      <div id="doc3">
<#-- Template-specific body markup -->
<#nested>
      </div>
      <div class="sticky-push"></div>
   </div>

<#-- This function call MUST come after all other component includes. -->
   <div id="alfresco-yuiloader"></div>
   <script type="text/javascript">//<![CDATA[
      Alfresco.util.YUILoaderHelper.loadComponents(true);
   //]]></script>
</body>
</html>
</#macro>
