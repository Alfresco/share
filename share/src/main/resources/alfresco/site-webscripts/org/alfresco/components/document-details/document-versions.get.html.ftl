<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/document-details/document-versions.css" group="document-details"/>
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/revert-version.css" group="document-details"/>
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/historic-properties-viewer.css" group="document-details"/>
   </@>
   
   <@markup id="js">
      <#-- JavaScript Dependencies -->
      <@script type="text/javascript" src="${url.context}/res/components/document-details/document-versions.js" group="document-details"/>
      <@script type="text/javascript" src="${url.context}/res/modules/document-details/revert-version.js" group="document-details"/>
      <@script type="text/javascript" src="${url.context}/res/modules/document-details/historic-properties-viewer.js" group="document-details"/>
   </@>
   
   <@markup id="widgets">
      <#if exist>
         <@createWidgets group="document-details"/>
         <@inlineScript group="document-details">
            YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
               Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "DocumentVersions");
            });
         </@>
      </#if>
   </@>
   
   <@markup id="html">
      <@uniqueIdDiv>
         <#if exist>
            <#if isWorkingCopy>
               <!-- No version component is displayed since it is a working copy -->
            <#else>
               <#assign el=args.htmlid?html>
               <div id="${el}-body" class="document-versions document-details-panel">
                  <h2 id="${el}-heading" class="thin dark">
                     ${msg("header.versionHistory")}
                     <#if allowNewVersionUpload>
                        <span class="alfresco-twister-actions">
                           <a href="#" name=".onUploadNewVersionClick" class="${el} edit" title="${msg("label.newVersion")}">&nbsp;</a>
                        </span>
                     </#if>
                  </h2>
                  <div class="panel-body">
                     <h3 class="thin dark">${msg("section.latestVersion")}</h3>
                     <div id="${el}-latestVersion" class="current-version version-list"></div>
                     <hr />
                     <h3 class="thin dark">${msg("section.olderVersion")}</h3>
                     <div id="${el}-olderVersions" class="version-list"></div>
                  </div>
               </div>
            </#if>
         </#if>
      </@>
   </@>
</@>