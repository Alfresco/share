<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
   <@script type="text/javascript" src="${url.context}/res/templates/manage-permissions/template.manage-permissions.js"></@script>   
</@>

<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
   </div>
   </@>
   <@markup id="bd">
   <div id="bd" class="manage-permissions">
      <@region id="path" scope="template" />
      <@region id="manage-permissions" scope="template" />
   </div>
   </@>
   <@markup id="manage-permissions">
   <script type="text/javascript">//<![CDATA[
   new Alfresco.template.ManagePermissions().setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${url.args.nodeRef?js_string}"),
      siteId: "${page.url.templateArgs.site!""}",
      rootNode: "${(config.scoped["RepositoryLibrary"]["root-node"].getValue())!"alfresco://company/home"}"
   });
   //]]></script>
   </@>
</@>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
   </@>
</@>
