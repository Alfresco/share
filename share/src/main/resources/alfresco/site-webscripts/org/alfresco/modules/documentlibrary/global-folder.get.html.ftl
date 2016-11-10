<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="global-folder">
   <div id="${el}-title" class="hd"></div>
   <div class="bd">
      <div id="${el}-wrapper" class="wrapper">
         <div class="mode flat-button">
            <h3>${msg("header.destination-type")}</h3>
            <div id="${el}-modeGroup" class="yui-buttongroup">
               <input type="radio" id="${el}-recentsites" name="3" value="${msg("button.recentsites")}" />
               <input type="radio" id="${el}-favouritesites" name="4" value="${msg("button.favouritesites")}" />
               <input type="radio" id="${el}-site" name="0" value="${msg("button.site")}" checked="checked" />
               <#if showRepositoryLink>
               <input type="radio" id="${el}-repository" name="1" value="${msg("button.repository")}" />
               </#if>
               <input type="radio" id="${el}-shared" name="5" value="${msg("button.shared")}" />
               <input type="radio" id="${el}-myfiles" name="2" value="${msg("button.myfiles")}" />
            </div>
         </div>
         <div class="site">
            <h3>${msg("header.site-picker")}</h3>
            <div id="${el}-sitePicker" class="site-picker"></div>
         </div>
         <div class="container">
            <h3>${msg("header.container-picker")}</h3>
            <div id="${el}-containerPicker" class="container-picker"></div>
         </div>
         <div class="path">
            <h3>${msg("header.path-picker")}</h3>
            <div id="${el}-treeview" class="treeview"></div>
         </div>
         <div class="bdft">
            <input type="button" id="${el}-link" value="${msg("button.link")}" />
            <input type="button" id="${el}-ok" value="${msg("button.ok")}" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" />
         </div>
      </div>
   </div>
</div>
<#assign treeConfig = config.scoped["DocumentLibrary"]["tree"]!>
<#if treeConfig.getChildValue??>
   <#assign evaluateChildFoldersSite = treeConfig.getChildValue("evaluate-child-folders")!"true">
   <#assign maximumFolderCountSite = treeConfig.getChildValue("maximum-folder-count")!"-1">
   <#assign webscriptTimeout = treeConfig.getChildValue("timeout")!"7000">
</#if>
<#assign treeConfig = config.scoped["RepositoryLibrary"]["tree"]!>
<#if treeConfig.getChildValue??>
   <#assign evaluateChildFoldersRepo = treeConfig.getChildValue("evaluate-child-folders")!"true">
   <#assign maximumFolderCountRepo = treeConfig.getChildValue("maximum-folder-count")!"-1">
</#if>
<#assign commonComponentConfig = config.scoped["CommonComponentStyle"]["component-style"]!>
<#if commonComponentConfig.value??>
        <#assign tmp = commonComponentConfig.value>
        <#assign customFolderStyleConfig = tmp!"">
</#if>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.addMessages(${messages}, "Alfresco.module.DoclibGlobalFolder");
   Alfresco.util.ComponentManager.get("${el}").setOptions(
   {
      evaluateChildFoldersSite: ${evaluateChildFoldersSite!"true"},
      maximumFolderCountSite: ${(maximumFolderCountSite!"-1")},
      evaluateChildFoldersRepo: ${evaluateChildFoldersRepo!"true"},
      maximumFolderCountRepo: ${(maximumFolderCountRepo!"-1")},
      webscriptTimeout: ${(webscriptTimeout!"7000")},
      customFolderStyleConfig: <#if customFolderStyleConfig??>${(customFolderStyleConfig!"")}<#else>null</#if>,
      siteTreeContainerTypes: {
         <#assign siteTreeConfig = config.scoped["GlobalFolder"]["siteTree"]!>
         <#if siteTreeConfig.getChildren?? && siteTreeConfig.getChildren("container")??>
            <#list siteTreeConfig.childrenMap["container"] as container>
            "${container.attributes["type"]}":
            {
               uri: <#if container.getChildValue("uri")??>"${container.getChildValue("uri")?js_string}"<#else>null</#if>,
               rootLabel: <#if container.getChildValue("rootLabel")??>"${container.getChildValue("rootLabel")?js_string}"<#else>null</#if>
            }<#if container_has_next>,</#if>
            </#list>
         </#if>
      }
   });
//]]></script>
