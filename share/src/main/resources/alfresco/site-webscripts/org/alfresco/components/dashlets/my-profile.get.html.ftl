<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/dashlets/my-profile.css" group="dashlets"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="post">
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="dashlet">
         <div class="title">${msg("header.myLimitedProfile")}</div>
         <div class="toolbar flat-button">
            <div>
               <span class="align-right yui-button-align">
                  <span class="first-child">
                     <a href="${url.context}/page/user/${user.name?url}/profile" class="theme-color-1">
                        <img src="${url.context}/res/components/images/user-16.png" style="vertical-align: text-bottom" width="16" />
                        ${msg("link.viewFullProfile")}</a>
                  </span>
               </span>
            </div>
         </div>
         <div class="body profile">
            <div class="photorow">
               <div class="photo">
                  <#assign avatarNodeRef>${user.properties["avatar"]!"avatar"}</#assign>
                  <img  class="photoimg" src="${url.context}/proxy/alfresco/slingshot/profile/avatar/${avatarNodeRef?string?replace('://','/')}" alt="avatar" />
               </div>
      <#escape x as x?html>
               <div class="namelabel"><a href="${url.context}/page/user/${user.name?url}/profile" class="theme-color-1">${user.properties["firstName"]!""} ${user.properties["lastName"]!""}</a></div>
               <div class="titlelabel">${user.properties["jobtitle"]!""}</div>
               <#if user.properties.userStatus?? && user.properties.userStatus?length!=0><div class="user-status">${user.properties.userStatus?html} <span class="time">(<span class="relativeTime">${user.properties.userStatusTime}</span>)</span></div></#if>
               <div class="clear"></div>
            </div>
            <div class="clear"></div>
            <div class="row">
               <div class="fieldlabel">${msg("label.email")}:</div>
               <div class="fieldvalue"><#if user.properties["email"]??><a href="mailto:${user.properties["email"]!""}" class="theme-color-1">${user.properties["email"]}</a></#if></div>
            </div>
            <div class="row">
               <div class="fieldlabel">${msg("label.telephone")}:</div>
               <div class="fieldvalue">${user.properties["telephone"]!""}</div>
            </div>
            <div class="row">
               <div class="fieldlabel">${msg("label.skype")}:</div>
               <div class="fieldvalue">${user.properties["skype"]!""}</div>
            </div>
            <div class="row">
               <div class="fieldlabel">${msg("label.im")}:</div>
               <div class="fieldvalue">${user.properties["instantmsg"]!""}</div>
            </div>
            <div class="clear"></div>
         </div>
      </div>
      </#escape>
      <script>Alfresco.util.renderRelativeTime("${args.htmlid?js_string}");</script>
   </@>
</@>
