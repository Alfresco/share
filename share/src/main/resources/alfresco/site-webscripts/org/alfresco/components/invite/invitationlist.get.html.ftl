<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/invite/invitationlist.css" group="invite"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/invite/invitationlist.js" group="invite"/>
</@>

<@markup id="widgets">
   <@createWidgets group="invite"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-invitationlistwrapper" class="invitationlistwrapper">
         <div class="title theme-color-2">${msg("invitationlist.title")}</div>
         <div id="${args.htmlid}-invitationlist" class="invitationlist">
            <div id="${args.htmlid}-invitationBar" class="invitelist-bar">
               <button id="${args.htmlid}-selectallroles-button">${msg("invitationlist.selectallroles")}&nbsp;&#9662;</button>
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
            <span id="${args.htmlid}-invite-button" class="yui-button yui-push-button"><span class="first-child"><button>${msg("invitationlist.invite")}</button></span></span>
            <span id="${args.htmlid}-backTo" class="back-to">${msg("invitationlist.or")} <a href="site-members">${msg("invitationlist.back-to")}</a></span>
         </div>
      </div>
   </@>
</@>
