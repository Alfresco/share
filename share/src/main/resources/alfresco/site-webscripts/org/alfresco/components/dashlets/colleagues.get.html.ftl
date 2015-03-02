<@markup id="css" >
<#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/dashlets/colleagues.css" group="dashlets"/>
</@>

<@markup id="js">
<#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
   <div class="dashlet colleagues">

      <#-- TITLE -->
      <@markup id="title">
         <div class="title">${msg("header")}</div>
      </@markup>

      <#if userMembership.isManager>

         <#-- MANAGER TOOLBAR -->
         <@markup id="managerToolbar">
            <div class="toolbar flat-button">
               <div>
                  <div class="align-left">

                     <#-- MANAGER TOOLBAR - EMPTY LEFT -->
                     <@markup id="managerToolbar-emptyLeft">
                     </@markup>

                  </div>
                  <span class="align-right yui-button-align">

                     <#-- INVITE -->
                     <@markup id="managerToolbar-inviteLink">
                        <span class="first-child">
                           <a href="invite" class="theme-color-1">
                              <img src="${url.context}/res/components/images/user-16.png" style="vertical-align: text-bottom" width="16" />
                           ${msg("link.invite")}</a>
                        </span>
                     </@markup>

                  </span>
                  <div class="clear"></div>
               </div>
            </div>
         </@markup>

      </#if>

      <#-- TOOLBAR -->
      <@markup id="toolbar">
         <div class="toolbar flat-button">
            <div>
               <div class="align-left paginator">

               <#-- PAGINATION -->
               <@markup id="toolbar-pagination">
               <#if totalResults <= maxResults>
                   ${msg("pagination.template", 1, memberships?size, totalResults?string)}
               <#else>
                   ${msg("pagination.template2", memberships?size)}
               </#if>
               </@markup>

               </div>
               <span class="align-right yui-button-align">

                  <#-- ALL MEMBERS LINK -->
                  <@markup id="toolbar-allMembersLink">
                     <span class="first-child">
                        <a href="site-members" class="theme-color-1">${msg("link.all-members")}</a>
                     </span>
                  </@markup>

               </span>
               <div class="clear"></div>
            </div>
         </div>
      </@markup>

   <#-- LIST -->
   <@markup id="list">
      <div class="body scrollableList" style="<#if args.height??>height: ${args.height?html}px;</#if>">
         <#if (memberships?size == 1 && memberships[0].authority.userName = user.id)>

            <#-- LIST - EMPTY -->
            <@markup id="list-empty">
               <div class="info">
                  <h3>${msg("empty.title")}</h3>
               </div>
            </@markup>

         </#if>

         <#list memberships as m>

            <#-- LIST - ITEM -->
            <@markup id="list-item">
               <div class="detail-list-item">

            <div class="avatar">
               
               <#-- LIST - ITEM - AVATAR -->
               <@markup id="list-item-avatar">
               <#assign avatarNodeRef>${m.authority.avatarNode!"avatar"}</#assign>
               <img src="${url.context}/proxy/alfresco/slingshot/profile/avatar/${avatarNodeRef?string?replace('://','/')}" alt="Avatar" />
               </@markup>

                  </div>
                  <div class="person">

                     <#-- LIST - ITEM - PERSON -->
                     <@markup id="list-item-person">
                        <h3><a href="${url.context}/page/user/${m.authority.userName?url}/profile" class="theme-color-1">${m.authority.firstName?html} <#if m.authority.lastName??>${m.authority.lastName?html}</#if></a></h3>
                     </@markup>

                     <#-- LIST - ITEM - ROLE -->
                     <@markup id="list-item-role">
                        <div>${msg("role." + m.role)}</div>
                     </@markup>

                     <#if m.authority.userStatus?? && (m.authority.userStatus?length > 0)>

                        <#-- LIST - ITEM - STATUS -->
                        <@markup id="list-item-status">
                           <#if m.authority.userStatus??>
                           <div class="user-status">${m.authority.userStatus?html} <span class="time">(<span class="relativeTime">${m.authority.userStatusTime.iso8601?html}</span>)</span></div>
                           </#if>
                        </@markup>

                     </#if>

                  </div>
                  <div class="clear"></div>
               </div>
            </@markup>

         </#list>
      </div>
   </@markup>
   </div>
   <script>Alfresco.util.renderRelativeTime("${args.htmlid?js_string}");</script>
   </@>
</@>