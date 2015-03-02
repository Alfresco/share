<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/invite/groupslist.css" group="invite"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/invite/groupslist.js" group="invite"/>
</@>

<@markup id="widgets">
   <@createWidgets/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-grouplistWrapper" class="grouplistWrapper">
         <div class="title theme-color-2">${msg("groupslist.title")}</div>
         <div id="${args.htmlid}-groupslist" class="groupslist">
            <div id="${args.htmlid}-invitationBar" class="invitelist-bar">
               <button id="${args.htmlid}-selectallroles-button">${msg("groupslist.selectallroles")}&nbsp;&#9662;</button>
               <select id="${args.htmlid}-selectallroles-menu">
               <#list siteRoles as siteRole>
                  <option value="${siteRole}">${msg('role.' + siteRole)}</option>
               </#list>
               </select>
            </div>
            <div id="${args.htmlid}-inviteelist" class="body inviteelist theme-bg-color-6"></div>
            <div id="${args.htmlid}-role-column-template" style="display:none">
               <button class="role-selector-button" value="">${msg("role")}</button>
            </div>
         </div>
         <div class="sinvite">
            <button id="${args.htmlid}-add-button">${msg("button.add-groups")}</button>
            <span id="${args.htmlid}-backTo" class="back-to">${msg("groupslist.or")} <a href="site-groups">${msg("groupslist.back-to")}</a></span>
         </div>
      </div>
   </@>
</@>
