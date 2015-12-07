<#include "../component.head.inc">
<#--
   RESOURCES
-->
<@markup id="favicons">
   <!-- Icons -->
   <link rel="shortcut icon" href="${url.context}/res/favicon.ico" type="image/vnd.microsoft.icon" />
   <link rel="icon" href="${url.context}/res/favicon.ico" type="image/vnd.microsoft.icon" />
</@markup>

<@markup id="yui">
   <#-- YUI -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/yui-fonts-grids.css" group="template-common" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/columnbrowser/assets/columnbrowser.css" group="template-common" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/columnbrowser/assets/skins/default/columnbrowser-skin.css" group="template-common" />
   <#if theme = 'default'>
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/assets/skins/default/skin.css" group="template-common" />
   <#else>
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/yui/assets/skin.css" group="template-common" />
   </#if>

   <#--
      ACE-1147: Debug and Production files are combined into a yui-common.js and yui-common-min.js
                Surf automatically selects the correct one depending on config mode.
   -->
   <@script type="text/javascript" src="${url.context}/res/js/yui-common.js" group="template-common"/>
   
   <@script type="text/javascript" src="${url.context}/res/yui/history/history.js" group="template-common"/>

   <@script type="text/javascript" src="${url.context}/res/js/bubbling.v2.1.js" group="template-common"/>
   <@inlineScript group="template-common">
      YAHOO.Bubbling.unsubscribe = function(layer, handler, scope)
      {
         this.bubble[layer].unsubscribe(handler, scope);
      };
   </@>
</@>

<@markup id="alfrescoConstants">
   <@inlineScript group="template-common">
      <!-- Alfresco web framework constants -->
      Alfresco.constants = Alfresco.constants || {};
      Alfresco.constants.DEBUG = ${DEBUG?string};
      Alfresco.constants.AUTOLOGGING = ${AUTOLOGGING?string};
      Alfresco.constants.PROXY_URI = window.location.protocol + "//" + window.location.host + "${url.context?js_string}/proxy/alfresco/";
      Alfresco.constants.PROXY_URI_RELATIVE = "${url.context?js_string}/proxy/alfresco/";
      Alfresco.constants.PROXY_FEED_URI = window.location.protocol + "//" + window.location.host + "${url.context?js_string}/proxy/alfresco-feed/";
      Alfresco.constants.THEME = "${theme}";
      Alfresco.constants.URL_CONTEXT = "${url.context?js_string}/";
      Alfresco.constants.URL_RESCONTEXT = "${url.context?js_string}/res/";
      Alfresco.constants.URL_PAGECONTEXT = "${url.context?js_string}/page/";
      Alfresco.constants.URL_SERVICECONTEXT = "${url.context?js_string}/service/";
      Alfresco.constants.URL_FEEDSERVICECONTEXT = "${url.context?js_string}/feedservice/";
      Alfresco.constants.USERNAME = "${(user.name!"")?js_string}";
      Alfresco.constants.SITE = "<#if page??>${(page.url.templateArgs.site!"")?url?js_string}</#if>";
      Alfresco.constants.PAGECONTEXT = "<#if page??>${(page.url.templateArgs.pagecontext!"")?url?js_string}</#if>";
      Alfresco.constants.PAGEID = "<#if page??>${(page.url.templateArgs.pageid!"")?url?js_string}</#if>";
      Alfresco.constants.JS_LOCALE = "${locale}";
      Alfresco.constants.USERPREFERENCES = "${preferences?js_string}";
      Alfresco.constants.CSRF_POLICY = {
         enabled: ${((config.scoped["CSRFPolicy"]["filter"].getChildren("rule")?size > 0)?string)!false},
         cookie: "${config.scoped["CSRFPolicy"]["client"].getChildValue("cookie")!""}",
         header: "${config.scoped["CSRFPolicy"]["client"].getChildValue("header")!""}",
         parameter: "${config.scoped["CSRFPolicy"]["client"].getChildValue("parameter")!""}",
         properties: {}
      };
      <#if config.scoped["CSRFPolicy"]["properties"]??>
         <#assign csrfProperties = (config.scoped["CSRFPolicy"]["properties"].children)![]>
         <#list csrfProperties as csrfProperty>
      Alfresco.constants.CSRF_POLICY.properties["${csrfProperty.name?js_string}"] = "${(csrfProperty.value!"")?js_string}";
         </#list>
      </#if>

      Alfresco.constants.IFRAME_POLICY =
      {
         sameDomain: "${config.scoped["IFramePolicy"]["same-domain"].value!"allow"}",
         crossDomainUrls: [<#list (config.scoped["IFramePolicy"]["cross-domain"].childrenMap["url"]![]) as c>
            "${c.value?js_string}"<#if c_has_next>,</#if>
         </#list>]
      };
      
      Alfresco.constants.HIDDEN_PICKER_VIEW_MODES = [
         <#list config.scoped["DocumentLibrary"]["hidden-picker-view-modes"].children as viewMode>
            <#if viewMode.name?js_string == "mode">"${viewMode.value?js_string}"<#if viewMode_has_next>,</#if></#if>
         </#list>
      ];
      
      Alfresco.constants.MENU_ARROW_SYMBOL = "&#9662;";

      Alfresco.constants.TINY_MCE_SUPPORTED_LOCALES = "${config.global["I18N"].getChildValue("tiny-mce-supported-locales")}";
   </@>
</@>

<@markup id="alfrescoResources">
   <!-- Alfresco web framework common resources -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/base.css" group="template-common" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/yui-layout.css" group="template-common" />
   <@script type="text/javascript" src="${url.context}/res/js/flash/AC_OETags.js" group="template-common"/>
   <@script type="text/javascript" src="${url.context}/res/js/alfresco.js" group="template-common"/>
   <script type="text/javascript" src="<@checksumResource src="${url.context}/res/modules/editors/tinymce/tinymce.min.js" parameter="checksum"/>"></script>
   <@script type="text/javascript" src="${url.context}/res/modules/editors/tiny_mce.js" group="template-common"/>
   <@script type="text/javascript" src="${url.context}/res/modules/editors/yui_editor.js" group="template-common"/>
   <@script type="text/javascript" src="${url.context}/res/js/forms-runtime.js" group="template-common"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/form/form.css" />
</@>

<@markup id="shareConstants">
   <@inlineScript group="template-common">
      <!-- Share Constants -->
      Alfresco.service.Preferences.FAVOURITE_DOCUMENTS = "org.alfresco.share.documents.favourites";
      Alfresco.service.Preferences.FAVOURITE_FOLDERS = "org.alfresco.share.folders.favourites";
      Alfresco.service.Preferences.FAVOURITE_FOLDER_EXT = "org.alfresco.ext.folders.favourites.";
      Alfresco.service.Preferences.FAVOURITE_DOCUMENT_EXT = "org.alfresco.ext.documents.favourites.";
      Alfresco.service.Preferences.FAVOURITE_SITES = "org.alfresco.share.sites.favourites";
      Alfresco.service.Preferences.IMAP_FAVOURITE_SITES = "org.alfresco.share.sites.imapFavourites";
      Alfresco.service.Preferences.COLLAPSED_TWISTERS = "org.alfresco.share.twisters.collapsed";
      Alfresco.service.Preferences.RULE_PROPERTY_SETTINGS = "org.alfresco.share.rule.properties";
      Alfresco.constants.URI_TEMPLATES =
      {
         <#list config.scoped["UriTemplate"]["uri-templates"].childrenMap["uri-template"] as c>
         "${c.attributes["id"]}": "${c.value}"<#if c_has_next>,</#if>
         </#list>
      };
      Alfresco.constants.HELP_PAGES =
      {
         "share-help": "${msg("share-help.docs-url", context.properties["docsEdition"].value)}",
         "share-tutorial": "${msg("share-tutorial.docs-url", context.properties["docsEdition"].value)}"
      };
      Alfresco.constants.HTML_EDITOR = 'tinyMCE';
      <#if config.scoped["Social"]["quickshare"].getChildValue("url")??>
      Alfresco.constants.QUICKSHARE_URL = "${config.scoped["Social"]["quickshare"].getChildValue("url")?replace("{context}", url.context)?js_string}";
      </#if>
      <#if config.scoped["Social"]["linkshare"].childrenMap["action"]??>
      Alfresco.constants.LINKSHARE_ACTIONS = [
         <#list config.scoped["Social"]["linkshare"].childrenMap["action"] as a>
         {
         id: "${a.attributes["id"]}", type: "${a.attributes["type"]}", index: ${a.attributes["index"]},
         params: { <#list a.childrenMap["param"] as p>"${p.attributes["name"]}": "${p.value?js_string}"<#if p_has_next>,</#if></#list> }
         }<#if a_has_next>,</#if>
         </#list>
      ];
      </#if>
   </@>
</@>

<@markup id="shareResources">
   <#-- Share resources -->
   <@script type="text/javascript" src="${url.context}/res/js/share.js" group="template-common"/>
   <@script type="text/javascript" src="${url.context}/res/js/lightbox.js" group="template-common"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/presentation.css" group="template-common" />
   <@script src="${url.context}/res/modules/create-site.js" group="template-common"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/create-site.css" group="template-common" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/${sitedata.getDojoPackageLocation('dijit')}/themes/claro/claro.css" group="share" forceAggregation="true"/>
</@>

<@markup id="resources">
   <#-- Use this "markup id" to add in a extension's resources -->
</@>
