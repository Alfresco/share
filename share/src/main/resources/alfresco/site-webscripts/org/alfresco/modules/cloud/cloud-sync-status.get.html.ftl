<#include "../../include/alfresco-macros.lib.ftl" />
<#assign el=args.htmlid?html>
<div class="cloud-sync-status" data-sync-owner-fullname="${(syncOwnerFullName?html)!""}">
   {title}
   <div class="cloud-sync-details">
      <div class="cloud-sync-error-details {syncFailed}">
         <div class="cloud-sync-details-failed-detailed">
               <span><a class="cloud-sync-details-failed-show-link">${msg("sync.status.show-details")}</a></span>
               <span><a class="cloud-sync-details-failed-hide-link hidden">${msg("sync.status.hide-details")}</a></span>
               ${msg("sync.status.last-failed")}
            <div class="cloud-sync-error-detailed hidden">
               <span class="cloud-sync-error-code">{errorCode}</span>
               <div class="cloud-sync-error-details-report">
                  <label for="${el}-cloud-sync-error-details" class="cloud-sync-error-details-label">${msg("sync.status.report-label")}</label>
                  <textarea id="${el}-cloud-sync-error-details" class="cloud-sync-error-details" readonly="readonly">{errorDetails}</textarea>
               </div>
            </div>
         </div>
      </div>
      <div class="cloud-sync-error-details-transient {transientError}">
         <div class="cloud-sync-details-failed-detailed-transient">
               <span><a class="cloud-sync-details-failed-show-link-transient">${msg("sync.status.show-details")}</a></span>
               <span><a class="cloud-sync-details-failed-hide-link-transient hidden">${msg("sync.status.hide-details")}</a></span>
               ${msg("sync.status.transient-error")}
            <div class="cloud-sync-error-detailed-transient hidden">
               <span class="cloud-sync-error-code">{transientErrorCode}</span>
               <div class="cloud-sync-error-details-report">
                  <label for="${el}-cloud-sync-error-details-transient" class="cloud-sync-error-details-transient-label">${msg("sync.status.report-label")}</label>
                  <textarea id="${el}-cloud-sync-error-details-transient" class="cloud-sync-error-details-transient" readonly="readonly">{transientErrorDetails}</textarea>
               </div>
            </div>
         </div>
      </div>
      <div class="cloud-sync-details-info">
         <div class="cloud-sync-status-heading">
            <h4>${msg("sync.status.heading.status")}</h4>
            {showMoreInfoLink}
         </div>
         <#if synced>
            <p>
               <#--Rendered client side, since we've got the data in hand there & it involves a date. -->
               {status}
            </p>
            <div class="cloud-sync-location-heading"><h4>${msg("sync.status.heading.location")}</h4></div>

            <#-- If we've found the node, show details about it's location.-->
            <#if nodeFound??>
               <#-- Generate a file type specific icon -->
               <#assign urlPrefix=shareURL?html + "/" + remoteNetworkId?html + "/page/" />
               <#assign urlPrefixSite=urlPrefix + "site/" + site.name + "/"/>

               <#-- If we're showing the parent folder (e.g. because the node doesn't exist yet), then let the user know-->
               <#if isParentPath??>
                  <p class="parent-msg">${msg("sync.status.message.parent")}</p>
               </#if>

               <p class="location">
                  <#-- Breadcrumb / path -->
                  <span class="folder-link network-link"><a target="_blank" href="${urlPrefix}">${remoteNetworkId}</a></span>
                  &gt;
                  <span class="folder-link site-link"><a target="_blank" href="${urlPrefixSite}dashboard">${site.title?html}</a></span>
                  &gt;
                  <@renderPrefixedPaths paths urlPrefixSite />
                  <#-- Show the file/folder name with a link, if we're not showing the parent. -->
                  <#if !isParentPath??>
                     <#assign cloudViewUrl=url.context + "/service/cloud/cloudUrl?nodeRef=" + nodeRef>
                     <span class="document-link">
                        <#if isContainer>
                           <img src="${url.context}/components/images/filetypes/generic-folder-32.png" width="32" /><a href="${cloudViewUrl}" target="_blank" class="view-in-cloud">${nodeTitle}</a>
                        <#else>
                           <#assign fileExtIndex = item.fileName?last_index_of(".")>
                           <#assign fileExt = (fileExtIndex > -1)?string(item.fileName?substring(fileExtIndex + 1)?lower_case, "generic")>
                           <img src="${url.context}/components/images/filetypes/${fileExt}-file-32.png" onerror="this.src='${url.context}/res/components/images/filetypes/generic-file-32.png'" width="32" /><a href="${cloudViewUrl}" target="_blank" class="view-in-cloud">${nodeTitle}</a>
                        </#if>
                     </span>
                  </#if>
               </p>
            <#else>
               <#-- If we can't find the node, show the user a message to help them -->
               <p>${msg("sync.status.unknown-location")}</p>
               <#if error??>
                  <h4>${msg("sync.status.unknown-location.cause")}</h4>
                  <p>${error.message}</p>
               </#if>
            </#if>
         <#else>
            <p>${msg("sync.status.message.not-synced")}</p>
         </#if>

         <#-- Root folder for indirectly synced nodes -->
         <#if isDirectSync?? && isDirectSync == "false">
            <div class="cloud-sync-indirect-root location">
               <h4>${msg("sync.status.heading.synced-folder")}</h4>
               <span class="document-root-link document-link">
                  <img src="${url.context}/res/components/images/filetypes/generic-folder-32.png" width="32" /><a href="folder-details?nodeRef=${rootNodeRef}" class="view-in-cloud">${rootNodeName}</a>
                </span>
             </div>
          </#if>

      </div>
   </div>
</div>
<div class="cloud-sync-status-buttons">
   <button id="${el}-button-requestsyn" class={requestSyncButtonClass}>${msg("button.requestsync")}</button>
   <button id="${el}-button-unsync" class={unsyncButtonClass}>${msg("button.unsync")}</button>
</div>
