<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/profile/profile.css" group="profile"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/profile/profile.js" group="profile"/>
   <@script src="${url.context}/res/modules/simple-dialog.js" group="profile"/>
</@>

<#assign el=args.htmlid>

<@markup id="widgets">
   <@createWidgets group="profile"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign displayname=profile.firstName>
      <#if profile.lastName??><#assign displayname=displayname + " " + profile.lastName></#if>
      <#macro immutablefield field>
         <#if profile.nativeUser.isImmutableProperty(field)>disabled="true"</#if>
      </#macro>
      <div id="${el}-body" class="profile">
         <div id="${el}-readview" class="hidden">
            <@markup id="viewControls">
            <#if isEditable>
            <div class="editcolumn">
               <div class="btn-edit">
                  <span class="yui-button yui-push-button" id="${el}-button-edit">
                     <span class="first-child"><button name="edit">${msg("button.editprofile")}</button></span>
                  </span>
               </div>
            </div>
            <#else>
            <#if follows??>
            <div class="editcolumn">
               <button id="${el}-button-following"><#if follows>${msg("button.unfollow")}<#else>${msg("button.follow")}</#if></button>
            </div>
            </#if>
            </#if>
            </@markup>
            <div class="viewcolumn">
            <@markup id="viewAbout">
               <div class="header-bar">${msg("label.about")}</div>
               <div class="photorow">
                  <div class="photo">
                     <img class="photoimg" src="${url.context}<#if profile.properties.avatar??>/proxy/alfresco/api/node/${profile.properties.avatar?replace('://','/')}/content/thumbnails/avatar?c=force<#else>/res/components/images/no-user-photo-64.png</#if>" alt="" />
                  </div>
                  <div class="namelabel">${displayname?html}</div>
                  <#if profile.jobTitle?? && profile.jobTitle?length!=0><div class="fieldlabel">${profile.jobTitle?html}</div></#if>
                  <#if profile.organization?? && profile.organization?length!=0><div class="fieldlabel">${profile.organization?html}</div></#if>
                  <#if profile.location?? && profile.location?length!=0><div class="fieldlabel">${profile.location?html}</div></#if>
               </div>
               <#if bio?? && bio?length!=0>
               <div class="biorow">
                  <hr/>
                  <div>${bio?html}</div>
               </div>
               </#if>
            </@markup>

            <@markup id="viewContactInfo">
               <div class="header-bar">${msg("label.contactinfo")}</div>
               <#if profile.email?? && profile.email?length!=0>
               <div class="row">
                  <span class="fieldlabelright">${msg("label.email")}:</span>
                  <span class="fieldvalue">${profile.email?html}</span>
               </div>
               </#if>
               <#if profile.telephone?? && profile.telephone?length!=0>
               <div class="row">
                  <span class="fieldlabelright">${msg("label.telephone")}:</span>
                  <span class="fieldvalue">${profile.telephone?html}</span>
               </div>
               </#if>
               <#if profile.mobilePhone?? && profile.mobilePhone?length!=0>
               <div class="row">
                  <span class="fieldlabelright">${msg("label.mobile")}:</span>
                  <span class="fieldvalue">${profile.mobilePhone?html}</span>
               </div>
               </#if>
               <#if profile.skype?? && profile.skype?length!=0>
               <div class="row">
                  <span class="fieldlabelright">${msg("label.skype")}:</span>
                  <span class="fieldvalue">${profile.skype?html}</span>
               </div>
               </#if>
               <#if profile.instantMsg?? && profile.instantMsg?length!=0>
               <div class="row">
                  <span class="fieldlabelright">${msg("label.im")}:</span>
                  <span class="fieldvalue">${profile.instantMsg?html}</span>
               </div>
               </#if>
               <#if profile.googleUsername?? && profile.googleUsername?length!=0>
               <div class="row">
                  <span class="fieldlabelright">${msg("label.googleusername")}:</span>
                  <span class="fieldvalue">${profile.googleUsername?html}</span>
               </div>
               </#if>
            </@markup>

            <@markup id="viewCompanyInfo">
               <div class="header-bar">${msg("label.companyinfo")}</div>
               <#if profile.organization?? && profile.organization?length!=0>
               <div class="row">
                  <span class="fieldlabelright">${msg("label.companyname")}:</span>
                  <span class="fieldvalue">${profile.organization?html}</span>
               </div>
               </#if>
               <#if (profile.companyAddress1?? && profile.companyAddress1?length!=0) ||
                    (profile.companyAddress2?? && profile.companyAddress2?length!=0) ||
                    (profile.companyAddress3?? && profile.companyAddress3?length!=0) ||
                    (profile.companyPostcode?? && profile.companyPostcode?length!=0)>
               <div class="row">
                  <span class="fieldlabelright">${msg("label.companyaddress")}:</span>
                  <span class="fieldvalue"><#if profile.companyAddress1?? && profile.companyAddress1?length!=0>${profile.companyAddress1?html}<br /></#if>
                     <#if profile.companyAddress2?? && profile.companyAddress2?length!=0>${profile.companyAddress2?html}<br /></#if>
                     <#if profile.companyAddress3?? && profile.companyAddress3?length!=0>${profile.companyAddress3?html}<br /></#if>
                     <#if profile.companyPostcode?? && profile.companyPostcode?length!=0>${profile.companyPostcode?html}</#if>
                  </span>
               </div>
               </#if>
               <#if profile.companyTelephone?? && profile.companyTelephone?length!=0>
               <div class="row">
                  <span class="fieldlabelright">${msg("label.companytelephone")}:</span>
                  <span class="fieldvalue">${profile.companyTelephone?html}</span>
               </div>
               </#if>
               <#if profile.companyFax?? && profile.companyFax?length!=0>
               <div class="row">
                  <span class="fieldlabelright">${msg("label.companyfax")}:</span>
                  <span class="fieldvalue">${profile.companyFax?html}</span>
               </div>
               </#if>
               <#if profile.companyEmail?? && profile.companyEmail?length!=0>
               <div class="row">
                  <span class="fieldlabelright">${msg("label.companyemail")}:</span>
                  <span class="fieldvalue">${profile.companyEmail?html}</span>
               </div>
               </#if>
            </@markup>
            </div>
         </div>

         <#if isEditable>
         <div id="${el}-editview" class="hidden">
            <form id="${htmlid}-form" action="${url.context}/service/components/profile/userprofile" method="post">

            <#-- EDIT ABOUT -->
            <@markup id="editAbout">
            <div class="header-bar">${msg("label.about")}</div>
            <div class="drow">
               <div class="reqcolumn">&nbsp;</div>
               <div class="leftcolumn">
                  <span class="label"><label for="${el}-input-firstName">${msg("label.firstname")}:</label></span>
                  <span><input type="text" maxlength="256" size="30" id="${el}-input-firstName" value="" <@immutablefield field="firstName" /> />&nbsp;*</span>
               </div>
               <div class="rightcolumn">
                  <span class="label"><label for="${el}-input-lastName">${msg("label.lastname")}:</label></span>
                  <span><input type="text" maxlength="256" size="30" id="${el}-input-lastName" value="" <@immutablefield field="lastName" /> /></span>
               </div>
            </div>
            <div class="drow">
               <div class="reqcolumn">&nbsp;</div>
               <div class="leftcolumn">
                  <span class="label"><label for="${el}-input-jobtitle">${msg("label.jobtitle")}:</label></span>
                  <span><input type="text" maxlength="256" size="30" id="${el}-input-jobtitle" value="" <@immutablefield field="jobtitle" /> /></span>
               </div>
               <div class="rightcolumn">
                  <span class="label"><label for="${el}-input-location">${msg("label.location")}:</label></span>
                  <span><input type="text" maxlength="256" size="30" id="${el}-input-location" value="" <@immutablefield field="location" /> /></span>
               </div>
            </div>
            <div class="row">
               <span class="label"><label for="${el}-input-bio">${msg("label.bio")}:</label></span>
               <span><textarea id="${el}-input-bio" name="${el}-text-biography" rows="5" cols="60"></textarea></span>
            </div>
            </@markup>

            <#-- EDIT PHOTO -->
            <@markup id="editPhoto">
            <div class="header-bar">${msg("label.photo")}</div>
            <div class="photorow">
               <div class="photo">
                  <img class="photoimg" src="${url.context}<#if profile.properties.avatar??>/proxy/alfresco/api/node/${profile.properties.avatar?replace('://','/')}/content/thumbnails/avatar?c=force<#else>/res/components/images/no-user-photo-64.png</#if>" alt="" />
               </div>
               <div class="photobtn">
            <#if uploadable>
                  <span class="yui-button yui-push-button" id="${el}-button-upload">
                     <span class="first-child"><button>${msg("button.upload")}</button></span>
                  </span>
            </#if>
                  <span class="yui-button yui-push-button" id="${el}-button-clearphoto">
                     <span class="first-child"><button>${msg("button.usedefault")}</button></span>
                  </span>
                  <div class="phototxt">${msg("label.photoimagesize")}</div>
                  <div class="phototxt">${msg("label.photonote")}</div>
               </div>
            </div>
            </@markup>

            <#-- EDIT CONTACT INFO -->
            <@markup id="editContactInfo">
            <div class="header-bar">${msg("label.contactinfo")}</div>
            <div class="row">
               <span class="label"><label for="${el}-input-telephone">${msg("label.telephone")}:</label></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-telephone" value="" <@immutablefield field="telephone" /> /></span>
            </div>
            <div class="row">
               <span class="label"><label for="${el}-input-mobile">${msg("label.mobile")}:</label></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-mobile" value="" <@immutablefield field="mobile" /> /></span>
            </div>
            <div class="row">
               <span class="label"><label for="${el}-input-email">${msg("label.email")}:</label></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-email" value="" <@immutablefield field="email" /> />&nbsp;*</span>
            </div>
            <div class="row">
               <span class="label"><label for="${el}-input-skype">${msg("label.skype")}:</label></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-skype" value="" <@immutablefield field="skype" /> /></span>
            </div>
            <div class="row">
               <span class="label"><label for="${el}-input-instantmsg">${msg("label.im")}:</label></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-instantmsg" value="" <@immutablefield field="instantmsg" /> /></span>
            </div>
            <div class="row">
               <span class="label"><label for="${el}-input-googleusername">${msg("label.googleusername")}:</label></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-googleusername" value="" <@immutablefield field="googleusername" /> /></span>
            </div>
            </@markup>

            <#-- EDIT COMPANY INFO -->
            <@markup id="editCompanyInfo">
            <div class="header-bar">${msg("label.companyinfo")}</div>
            <div class="row">
               <span class="label"><label for="${el}-input-organization">${msg("label.companyname")}:</label></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-organization" value="" <@immutablefield field="organization" /> /></span>
            </div>
            <div class="row">
               <span class="label"><label for="${el}-input-companyaddress1">${msg("label.companyaddress")}:</label></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-companyaddress1" value="" <@immutablefield field="companyaddress1" /> /></span>
            </div>
            <div class="row">
               <span class="label"></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-companyaddress2" value="" <@immutablefield field="companyaddress2" /> /></span>
            </div>
            <div class="row">
               <span class="label"></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-companyaddress3" value="" <@immutablefield field="companyaddress3" /> /></span>
            </div>
            <div class="row">
               <span class="label"><label for="${el}-input-companypostcode">${msg("label.companypostcode")}:</label></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-companypostcode" value="" <@immutablefield field="companypostcode" /> /></span>
            </div>
            <div class="row">
               <span class="label"><label for="${el}-input-companytelephone">${msg("label.companytelephone")}:</label></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-companytelephone" value="" <@immutablefield field="companytelephone" /> /></span>
            </div>
            <div class="row">
               <span class="label"><label for="${el}-input-companyfax">${msg("label.companyfax")}:</label></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-companyfax" value="" <@immutablefield field="companyfax" /> /></span>
            </div>
            <div class="row">
               <span class="label"><label for="${el}-input-companyemail">${msg("label.companyemail")}:</label></span>
               <span><input type="text" maxlength="256" size="30" id="${el}-input-companyemail" value="" <@immutablefield field="companyemail" /> /></span>
            </div>
            </@markup>

            <hr/>

            <div class="buttons">
               <button id="${el}-button-save" name="save">${msg("button.savechanges")}</button>
               <button id="${el}-button-cancel" name="cancel">${msg("button.cancel")}</button>
            </div>

            </form>
         </div>
         </#if>

      </div>
      <script>Alfresco.util.renderRelativeTime("${args.htmlid?js_string}-body");</script>
   </@>
</@>