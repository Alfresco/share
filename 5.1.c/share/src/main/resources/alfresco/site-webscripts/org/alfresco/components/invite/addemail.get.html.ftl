<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/invite/addemail.css" group="invite"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/invite/addemail.js" group="invite"/>
</@>

<@markup id="widgets">
   <#if allowEmailInvite>
      <@createWidgets group="invite"/>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if allowEmailInvite>
         <div id="${args.htmlid}-body" class="inviteusersbyemail">
            <div class="title theme-color-2">${msg("addemail.title")}</div>
            <div class="byemailbody">  
               <table class="byemailuser">
                  <tr>
                     <td class="elabel"><label for="${args.htmlid}-firstname">${msg("addemail.firstname")}:</label></td>
                     <td class="einput"><input type="text" id="${args.htmlid}-firstname" tabindex="1" maxlength="256" /></td>
                     <td class="byemailadd" colspan="3">
                        <span id="${args.htmlid}-add-email-button" class="yui-button yui-push-button"><span class="first-child"><button tabindex="4">${msg("addemail.add")} &gt;&gt;</button></span></span>
                     </td>
                  </tr>
                  <tr>
                     <td class="elabel"><label for="${args.htmlid}-lastname">${msg("addemail.lastname")}:</label></td>
                     <td class="einput"><input type="text" id="${args.htmlid}-lastname" tabindex="2" maxlength="256" /></td>
                  </tr>
                  <tr>
                     <td class="elabel"><label for="${args.htmlid}-email">${msg("addemail.email")}:</label></td>
                     <td class="einput"><input type="text" id="${args.htmlid}-email" tabindex="3" maxlength="256" /></td>
                  </tr>
               </table>
            </div>
         </div>
      </#if>
   </@>
</@>

