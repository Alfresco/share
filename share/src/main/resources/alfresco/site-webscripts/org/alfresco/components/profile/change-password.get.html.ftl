<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/profile/profile.css" group="profile"/>
   <@link href="${url.context}/res/components/profile/changepassword.css" group="profile"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/profile/changepassword.js" group="profile"/>
</@>

<@markup id="widgets">
   <@createWidgets group="profile"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="profile password">
         <form id="${el}-form" action="${url.context}/service/components/profile/change-password" method="post">
            <div class="header-bar">${msg("label.changepassword")}</div>
            <!-- If AIMS is enabled, display a message to inform the user that some settings are restricted -->
            <#if aimsEnabled>
               <div class="yui-u first">
                  <div style="float:left;margin-left:16px">${msg("label.restricted-settings")}</div>
               </div>
            </#if>
            <div class="row">
               <span class="label"><label for="${el}-oldpassword" <#if aimsEnabled>hidden</#if> >${msg("label.oldpassword")}:</label></span>
               <span <#if aimsEnabled>hidden</#if> ><input type="password" maxlength="255" size="30" id="${el}-oldpassword" /></span>
            </div>
            <div class="row">
               <span class="label"><label for="${el}-newpassword1" <#if aimsEnabled>hidden</#if> >${msg("label.newpassword")}:</label></span>
               <span <#if aimsEnabled>hidden</#if> ><input type="password" maxlength="255" size="30" id="${el}-newpassword1" /></span>
            </div>
            <div class="row">
               <span class="label"><label for="${el}-newpassword2" <#if aimsEnabled>hidden</#if> >${msg("label.confirmpassword")}:</label></span>
               <span <#if aimsEnabled>hidden</#if> ><input type="password" maxlength="255" size="30" id="${el}-newpassword2" /></span>
            </div>
            <hr/>
            <#if !aimsEnabled>
            <div class="buttons">
               <button id="${el}-button-ok" name="save">${msg("button.ok")}</button>
               <button id="${el}-button-cancel" name="cancel">${msg("button.cancel")}</button>
            </div>
            </#if>
         </form>
      </div>
   </@>
</@>

