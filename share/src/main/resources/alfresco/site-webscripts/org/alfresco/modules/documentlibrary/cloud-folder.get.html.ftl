<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="global-folder cloud-folder">
   <div id="${el}-title" class="hd"></div>
   <div class="bd">
      <div id="${el}-wrapper" class="wrapper">
         <div class="mode flat-button hidden">
            <h3>${msg("header.mode-picker")}</h3>
            <div id="${el}-modeGroup" class="yui-buttongroup">
               <input type="radio" id="${el}-cloud" name="0" value="${msg("button.cloud-mode")}" checked="checked" />
            </div>
         </div>
         <div class="network mode flat-button">
            <h3>${msg("header.network-picker")}</h3>
            <div id="${el}-networkGroup" class="yui-buttongroup">
               <#list networks as network>
                  <input type="radio" id="${el}-network-${network.name}" name="${network.name}" value="${network.name}" <#if network.isSyncEnabled == false>disabled="disabled"</#if> <#if network_index=0>checked="checked"</#if> />
               </#list>
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
            <div class="cloud-path-add-folder" title="${msg("tooltip.create-folder")}">&nbsp;</div>
            <h3>${msg("header.path-picker")}</h3>
            <div id="${el}-treeview" class="treeview"></div>
         </div>
         <div class="cloud-options">
            <h3>${msg("header.options")}</h3>
            <label for="includeSubFolders" id="includeSubFolders-label"><input type="checkbox" value="includeSubFolders" name="includeSubFolders" id="includeSubFolders" class="cloudSyncOption" checked="checked"><span>${msg("label.includeSubFolders")}</span></label>
            <label for="lockSourceCopy" id="lockSourceCopy-label"><input type="checkbox" value="lockSourceCopy" name="lockSourceCopy" id="lockSourceCopy" class="cloudSyncOption"><span>${msg("label.lock-source-copy")}</span></label>
            
            <label for="isDeleteOnCloud" id="isDeleteOnCloud-label"><input type="checkbox" value="isDeleteOnCloud" name="isDeleteOnCloud" id="isDeleteOnCloud" class="cloudSyncOption" checked="checked"><span>${msg("label.is-delete-on-cloud")}</span></label>
            <label for="isDeleteOnPrem" id="isDeleteOnPrem-label" class="noWrapElements"><input type="checkbox" value="isDeleteOnPrem" name="isDeleteOnPrem" id="isDeleteOnPrem" class="cloudSyncOption"><span>${msg("label.is-delete-on-prem")}</span></label>
            
            
            <#--
            <label for="syncComments" id="syncComments-label"><input type="checkbox" value="syncComments" name="syncComments" id="syncComments" class="cloudSyncOption"><span>${msg("label.syncComments")}</span></label>
            <label for="deleteOnUnsync" id="deleteOnUnsync-label"><input type="checkbox" value="deleteOnUnsync" name="deleteOnUnsync" id="deleteOnUnsync" class="cloudSyncOption"><span>${msg("label.deleteOnUnsync")}</span></label>
            -->
         </div>
         <div class="bdft">
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
</#if>
<#assign commonComponentConfig = config.scoped["CommonComponentStyle"]["component-style"]!>
<#if commonComponentConfig.value??>
        <#assign tmp = commonComponentConfig.value>
        <#assign customFolderStyleConfig = tmp!"">
</#if>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.addMessages(${messages}, "Alfresco.module.DoclibCloudFolder");
   Alfresco.util.ComponentManager.get("${el}").setOptions(
   {
      evaluateChildFoldersSite: ${evaluateChildFoldersSite!"true"},
      maximumFolderCountSite: ${(maximumFolderCountSite!"-1")},
      evaluateChildFoldersRepo: ${evaluateChildFoldersRepo!"true"},
      maximumFolderCountRepo: ${(maximumFolderCountRepo!"-1")},
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
