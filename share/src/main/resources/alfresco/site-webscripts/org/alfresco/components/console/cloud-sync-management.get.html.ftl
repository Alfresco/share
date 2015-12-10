<@markup id="css" >
   <#-- CSS Dependencies -->
   <#include "../form/form.css.ftl"/>
   <@link href="${url.context}/res/components/console/cloud-sync-management.css" group="console"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <#include "../form/form.js.ftl"/>
   <@script src="${url.context}/res/components/console/consoletool.js" group="console"/>
   <@script src="${url.context}/res/components/console/cloud-sync-management.js" group="console"/>
   <@script src="${url.context}/res/modules/simple-dialog.js" group="console"/>
   <@script src="${url.context}/res/modules/documentlibrary/doclib-actions.js" group="console"/>
</@>

<@markup id="widgets">
   <@createWidgets group="console"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      
      <div class="search-panel">
         <div class="search-text">
             <label for="${el}-show-all">${msg("message.ssd-id-label")}</label>
             <input type="text" id="${el}-ssd-id" name="-" value="" />
             
             <label for="${el}-ssd-sync-creator">${msg("message.ssd-sync-creator-label")}</label>
             <input type="text" id="${el}-ssd-sync-creator" name="-" value="" />
             
             <label for="${el}-ssd-failed">${msg("message.ssd-failed-label")}</label>
             <input type="checkbox" id="${el}-ssd-failed" name="-" title="${msg("message.ssd-failed-label")}" />
             
             <label for="${el}-ssd-failed-reason">${msg("message.ssd-failed-reason-label")}</label>
             <select id="${el}-ssd-failed-reason" name="-">
                 <option value="">${msg("message.ssd-failed-reason-dropdown-all-label")}</option>
                 <option value="sync.folder.name_clash">${msg("message.ssd-failed-reason-dropdown-name-clash-label")}</option>
                 <option value="sync.folder.not_found">${msg("message.ssd-failed-reason-dropdown-not-found-label")}</option>
                 <option value="sync.node.already_synced">${msg("message.ssd-failed-reason-dropdown-already-synced-label")}</option>
                 <option value="sync.node.other_sync_set">${msg("message.ssd-failed-reason-dropdown-other-sync-set-label")}</option>
                 <option value="sync.node.no_longer_exists">${msg("message.ssd-failed-reason-dropdown-no-longer-exists-label")}</option>
                 <option value="sync.node.access_denied">${msg("message.ssd-failed-reason-dropdown-access-denied-label")}</option>
                 <option value="sync.node.access_denied_source">${msg("message.ssd-failed-reason-dropdown-access-denied-source-label")}</option>
                 <option value="sync.node.content_limit_violation">${msg("message.ssd-failed-reason-dropdown-content-limit-violation-label")}</option>
                 <option value="sync.node.quota_limit_violation">${msg("message.ssd-failed-reason-dropdown-quota-limit-violation-label")}</option>
                 <option value="sync.node.authentication_error">${msg("message.ssd-failed-reason-dropdown-authentication-error-label")}</option>
                 <option value="sync.node.owner_not_found">${msg("message.ssd-failed-reason-dropdown-owner-not-found-label")}</option>
                 <option value="sync.node.deleted_on_cloud">${msg("message.ssd-failed-reason-dropdown-deleted-on-cloud-label")}</option>
                 <option value="sync.node.unknown">${msg("message.ssd-failed-reason-dropdown-unknown-label")}</option>
             </select>
         </div>
         
         <div class="search-button">
           <span class="yui-button yui-push-button" id="${el}-search-button">
              <span class="first-child"><button>${msg("button.ssd-reassign.applyFilter.label")}</button></span>
           </span>
        </div>
        
        <div class="search-button">
          <span class="yui-button yui-push-button" id="${el}-browse-button">
             <span class="first-child"><button>${msg("button.ssd-reassign.exportCSV.label")}</button></span>
          </span>
        </div>
      </div>
      
      <div class="dashlet ssds-List">
         <div id="${el}-ssds-list-info" class="ssds-list-info"></div>
         <div id="${el}-ssds-list-bar-bottom" class="toolbar theme-bg-color-3 hidden">
            <div id="${el}-paginator" class="paginator hidden">&nbsp;</div>
         </div>
         <div id="${el}-ssds" class="body scrollableList" style="height: 100%; overflow: hidden"></div>
      </div>
   </@>
</@>
