<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/quickshare/node-header.css" />
   </@>

   <@markup id="js"/>

   <@markup id="widgets"/>

   <@markup id="html">
      <@uniqueIdDiv>
         <#include "../../include/alfresco-macros.lib.ftl" />
         <#assign el=args.htmlid?html/>
         <div class="yui-gc quickshare-node-header">
            <#if showDownload == "true">
               <div class="yui-u first">
            </#if>
            <#-- Icon -->
            <img src="${url.context}/res/components/images/filetypes/${fileIcon(displayName,48)}"
                 onerror="this.src='${url.context}/res/components/images/filetypes/generic-file-48.png'"
                 title="${displayName?html}" class="quickshare-node-header-info-thumbnail" width="48" />

            <#-- Title -->
            <h1 class="quickshare-node-header-info-title thin dark">${displayName?html}</h1>

            <#-- Modified -->
            <div>
               ${msg("label.modified-by-user-on-date", (modifierFirstName!"")?html, (modifierLastName!"")?html, "<span id='${el}-modifyDate'>${modifyDate}</span>")}
               <script type="text/javascript">
                  var dateEl = YAHOO.util.Dom.get('${el}-modifyDate');
                  dateEl.innerHTML = Alfresco.util.formatDate(Alfresco.util.fromISO8601(dateEl.innerHTML), Alfresco.util.message("date-format.default"));
               </script>
            </div>

            <#if showDownload == "true">
               </div>
               <div class="yui-u quickshare-node-action"> 
                  <!-- Download Button -->
                  <span class="yui-button yui-link-button onDownloadDocumentClick">
                     <span class="first-child">
                        <a href="${url.context}/proxy/alfresco${(contentURL!"")?html}?a=true" tabindex="0">${msg("button.download")}</a>
                     </span>
                  </span>
               </div>
            </#if>

         </div>
      </@>
   </@>
</@>