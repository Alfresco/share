<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/profile/profile.css" group="profile"/>
   <@link href="${url.context}/res/components/profile/usernotifications.css" group="profile"/>
   <@link href="${url.context}/res/components/profile/usercloudauth.css" group="profile"/>
   <@link href="${url.context}/res/modules/cloud/cloud-auth-form.css" />
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/modules/cloud-auth.js" group="profile"/>
   <@script src="${url.context}/res/components/profile/usercloudauth.js" group="profile"/>
</@>

<@markup id="widgets">
   <#if syncEnabled>
      <@createWidgets group="profile"/>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if syncEnabled>
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="profile cloudAuth notifications<#if cloudConnected> connected</#if>">
         <div class="header-bar">${msg("label.cloud-auth")}</div>
         <div class="not-connected">
            <p>${msg("message.not-connected")}</p>
            <div class="signin">
               <button id="${el}-button-signIn" name="signIn">${msg("label.signIn")}</button>
            </div>
            <p>${msg("message.not-connected-meta")}</p>
         </div>
         <div class="existing-connection">
            <p><strong>${msg("label.connected-heading")}</strong></p>
            <p>${msg("label.connected", email)}</p>
            <#if !lastLoginSuccessful><p>${msg("label.lastLoginFailed")}</#if></p>
            <div class="buttons">
               <button id="${el}-button-edit" name="edit">${msg("label.edit")}</button>
               <button id="${el}-button-delete" name="delete">${msg("label.delete")}</button>
            </div>
         </div>
      </div>
      </#if>
   </@>
</@>