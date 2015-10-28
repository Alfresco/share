<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/console/users.css" group="console"/>
   <@link href="${url.context}/res/components/people-finder/group-finder.css" group="console"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/console/consoletool.js" group="console"/>
   <@script src="${url.context}/res/components/console/users.js" group="console"/>
   <@script src="${url.context}/res/components/people-finder/group-finder.js" group="console"/>
</@>

<@markup id="widgets">
   <@createWidgets group="console"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <!--[if IE]>
      <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe> 
      <![endif]-->
      <input id="yui-history-field" type="hidden" />
      
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="users">
      
         <!-- Search panel -->
         <div id="${el}-search" class="hidden">
            <div class="yui-g">
               <div class="yui-u first">
                  <div class="title"><label for="${el}-search-text">${msg("label.title-search")}</label></div>
               </div>
               <div class="yui-u align-right">
                  <!-- New User button -->
                  <span class="newuser-button">
                     <span class="yui-button yui-push-button" id="${el}-newuser-button">
                        <span class="first-child"><button>${msg("button.newuser")}</button></span>
                     </span>
                  </span>
                  <span class="uploadusers-button">
                     <span class="yui-button yui-push-button" id="${el}-uploadusers-button">
                        <span class="first-child"><button>${msg("button.uploaduser")}</button></span>
                     </span>
                  </span>
               </div>
            </div>
            <div class="yui-g separator">
               <div class="yui-u first">
                  <div class="search-text"><input type="text" id="${el}-search-text" name="-" value="" maxlength="256"/>
                     <!-- Search button -->
                     <div class="search-button">
                        <span class="yui-button yui-push-button" id="${el}-search-button">
                           <span class="first-child"><button>${msg("button.search")}</button></span>
                        </span>
                     </div>
                  </div>
               </div>
               <div class="yui-u align-right">
                  <!-- TODO: enabled/disabled account list filter? -->
               </div>
            </div>
            <div class="search-main">
               <div id="${el}-search-bar" class="search-bar theme-bg-color-3">${msg("message.noresults")}</div>
               <div class="results" id="${el}-datatable"></div>
            </div>
            <div id="${el}-paginator" class="paginator" >&nbsp;</div>
         </div>
         
         <!-- Upload CSV results panel -->
         <div id="${el}-csvresults" class="hidden">
            <div class="yui-g separator">
               <div class="yui-u first">
                  <div class="title">${msg("label.title-csvresults")}: <span id="${el}-csvresults-title"></span></div>
               </div>
            </div>
            <div class="yui-g separator hidden" id="${el}-csvresults-success">
               <div class="results search-main " id="${el}-csvresults-datatable"></div>
            </div>
            <div class="yui-g separator hidden" id="${el}-csvresults-failure">
               <div>${msg("message.csvupload.error.details")}</div>
               <div id="${el}-csvresults-error"></div>
            </div>
            <div class="yui-g">
               <div class="goback-button">
                  <span id="${el}-csv-goback-button">
                     <span class="first-child"><button>${msg("button.goback")}</button></span>
                  </span>
               </div>
            </div>
         </div>
         
         <!-- View User panel -->
         <div id="${el}-view" class="hidden">
            <div class="yui-g separator">
               <div class="yui-u first">
                  <div class="title">${msg("label.title-view")}: <span id="${el}-view-title"></span></div>
               </div>
               <div class="yui-u">
                  <!-- Delete User button -->
                  <div class="deleteuser-button">
                     <span class="yui-button yui-push-button" id="${el}-deleteuser-button">
                        <span class="first-child"><button>${msg("button.deleteuser")}</button></span>
                     </span>
                  </div>
                  <!-- Edit User button -->
                  <div class="edituser-button">
                     <span class="yui-button yui-push-button" id="${el}-edituser-button">
                        <span class="first-child"><button>${msg("button.edituser")}</button></span>
                     </span>
                  </div>
               </div>
            </div>
            
            <div id="${el}-view-main" class="view-main separator">
               <!-- Each info section separated by a header-bar div -->
               <div class="header-bar">${msg("label.about")}</div>
               <div class="photo-row">
                  <div class="photo">
                     <img class="view-photoimg" src="${url.context}/res/components/images/no-user-photo-64.png" alt="" />
                  </div>
                  <div id="${el}-view-name" class="name-label"></div>
                  <div id="${el}-view-jobtitle" class="field-label"></div>
                  <div id="${el}-view-organization" class="field-label"></div>
                  <div id="${el}-view-location" class="field-label"></div>
               </div>
               <div class="bio-row">
                  <hr/>
                  <div id="${el}-view-bio"></div>
               </div>
               
               <div class="header-bar">${msg("label.contactinfo")}</div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.email")}:</span>
                  <span id="${el}-view-email" class="field-value"></span>
               </div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.telephone")}:</span>
                  <span id="${el}-view-telephone" class="field-value"></span>
               </div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.mobile")}:</span>
                  <span id="${el}-view-mobile" class="field-value"></span>
               </div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.skype")}:</span>
                  <span id="${el}-view-skype" class="field-value"></span>
               </div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.im")}:</span>
                  <span id="${el}-view-instantmsg" class="field-value"></span>
               </div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.googleusername")}:</span>
                  <span id="${el}-view-googleusername" class="field-value"></span>
               </div>
               
               <div class="header-bar">${msg("label.companyinfo")}</div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.companyname")}:</span>
                  <span id="${el}-view-companyname" class="field-value"></span>
               </div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.companyaddress")}:</span>
                  <span id="${el}-view-companyaddress" class="field-value"></span>
               </div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.companytelephone")}:</span>
                  <span id="${el}-view-companytelephone" class="field-value"></span>
               </div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.companyfax")}:</span>
                  <span id="${el}-view-companyfax" class="field-value"></span>
               </div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.companyemail")}:</span>
                  <span id="${el}-view-companyemail" class="field-value"></span>
               </div>
               
               <div class="header-bar">${msg("label.moreuserinfo")}</div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.username")}:</span>
                  <span id="${el}-view-username" class="field-value"></span>
               </div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.groups")}:</span>
                  <span id="${el}-view-groups" class="field-value"></span>
               </div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.accountstatus")}:</span>
                  <span id="${el}-view-enabled" class="field-value"></span>
               </div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.quota")}:</span>
                  <span id="${el}-view-quota" class="field-value"></span>
               </div>
               <div class="field-row">
                  <span class="field-label-right">${msg("label.usage")}:</span>
                  <span id="${el}-view-usage" class="field-value"></span>
               </div>
            </div>
      
            <div class="yui-g">
               <!-- Cancel view user button -->
               <div class="goback-button">
                  <span class="yui-button yui-push-button" id="${el}-goback-button">
                     <span class="first-child"><button>${msg("button.goback")}</button></span>
                  </span>
               </div>
            </div>
         </div>
         
         <!-- Create User panel -->
         <div id="${el}-create" class="hidden">
            <div class="yui-g separator">
               <div class="yui-u first">
                  <div class="title">${msg("label.title-create")}</div>
               </div>
               <div class="yui-u">
                  <div style="float:right">* ${msg("label.requiredfield")}</div>
               </div>
            </div>
            
            <form id="${el}-create-form">
            <div id="${el}-create-main" class="create-main">
               <!-- Each info section separated by a header-bar div -->
               <div class="header-bar">${msg("label.info")}</div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.firstname")}:&nbsp;*</span>
               </div>
               <div class="field-row">
                  <input class="crud-input" id="${el}-create-firstname" type="text" maxlength="256" />
               </div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.lastname")}:</span>
               </div>
               <div class="field-row">
                  <input class="crud-input" id="${el}-create-lastname" type="text" maxlength="256" />
               </div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.email")}:&nbsp;*</span>
               </div>
               <div class="field-row">
                  <input class="crud-input" id="${el}-create-email" type="text" maxlength="256" />
               </div>
               
               <div class="header-bar">${msg("label.aboutuser")}</div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.username")}:&nbsp;*</span>
               </div>
               <div class="field-row">
                  <input class="crud-input" id="${el}-create-username" type="text" maxlength="100" />
               </div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.password")}:&nbsp;*</span>
               </div>
               <div class="field-row">
                  <input class="crud-input" id="${el}-create-password" type="password" maxlength="100" />
               </div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.verifypassword")}:&nbsp;*</span>
               </div>
               <div class="field-row">
                  <input class="crud-input" id="${el}-create-verifypassword" type="password" maxlength="100" />
               </div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.groups")}:</span>
               </div>
               <!-- groups picker inserted here -->
               <div class="grouppicker-row" id="${el}-create-groupfinder"></div>
               <div class="groupselection-row" id="${el}-create-groups"></div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.quota")}:</span>
               </div>
               <div class="field-row">
                  <input class="crud-input-quota" id="${el}-create-quota" type="text" maxlength="8" />
                  <select id="${el}-create-quotatype">
                     <option value="gb">${msg("size.gigabytes")}</option>
                     <option value="mb">${msg("size.megabytes")}</option>
                     <option value="kb">${msg("size.kilobytes")}</option>
                  </select>
               </div>
               <div class="field-row">
                  <span class="crud-label"><input type="checkbox" id="${el}-create-disableaccount" />&nbsp;${msg("label.disableaccount")}</span>
               </div>
            </div>
            
            <div>
               <div class="createuser-ok-button left">
                  <span class="yui-button yui-push-button" id="${el}-createuser-ok-button">
                     <span class="first-child"><button>${msg("button.createuser")}</button></span>
                  </span>
               </div>
               <div class="createuser-another-button left">
                  <span class="yui-button yui-push-button" id="${el}-createuser-another-button">
                     <span class="first-child"><button>${msg("button.createanother")}</button></span>
                  </span>
               </div>
               <div class="createuser-cancel-button">
                  <span class="yui-button yui-push-button" id="${el}-createuser-cancel-button">
                     <span class="first-child"><button>${msg("button.cancel")}</button></span>
                  </span>
               </div>
            </div>
            </form>
         </div>
         
         <!-- Update User panel -->
         <div id="${el}-update" class="hidden">
            <div class="yui-g separator">
               <div class="yui-u first">
                  <div class="title">${msg("label.title-update")}: <span id="${el}-update-title"></span></div>
               </div>
               <div class="yui-u">
                  <div style="float:right">* ${msg("label.requiredfield")}</div>
               </div>
            </div>
            
            <form id="${el}-update-form">
            <div id="${el}-update-main" class="update-main">
               <!-- Each info section separated by a header-bar div -->
               <div class="header-bar">${msg("label.info")}</div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.firstname")}:&nbsp;*</span>
               </div>
               <div class="field-row">
                  <input class="crud-input" id="${el}-update-firstname" type="text" maxlength="256" />
               </div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.lastname")}:</span>
               </div>
               <div class="field-row">
                  <input class="crud-input" id="${el}-update-lastname" type="text" maxlength="256" />
               </div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.email")}:&nbsp;*</span>
               </div>
               <div class="field-row">
                  <input class="crud-input" id="${el}-update-email" type="text" maxlength="256" />
               </div>
               
               <div class="header-bar">${msg("label.aboutuser")}</div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.groups")}:</span>
               </div>
               <!-- groups picker inserted here -->
               <div class="grouppicker-row" id="${el}-update-groupfinder"></div>
               <div class="groupselection-row" id="${el}-update-groups"></div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.quota")}:</span>
               </div>
               <div class="field-row">
                  <input class="crud-input-quota" id="${el}-update-quota" type="text" maxlength="8" />
                  <select id="${el}-update-quotatype">
                     <option value="gb">${msg("size.gigabytes")}</option>
                     <option value="mb">${msg("size.megabytes")}</option>
                     <option value="kb">${msg("size.kilobytes")}</option>
                  </select>
               </div>
               <div id="${el}-oldpassword-wrapper">
                  <div class="field-row">
                     <span class="crud-label">${msg("label.oldpassword")}:</span>
                  </div>
                  <div class="field-row">
                     <input class="crud-input" id="${el}-update-old-password" type="password" maxlength="255" />
                  </div>
               </div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.newpassword")}:</span>
               </div>
               <div class="field-row">
                  <input class="crud-input" id="${el}-update-password" type="password" maxlength="100" />
                  <br/>
                  <span class="small">${msg("label.leaveblank")}</span>
               </div>
               <div class="field-row">
                  <span class="crud-label">${msg("label.verifypassword")}:</span>
               </div>
               <div class="field-row">
                  <input class="crud-input" id="${el}-update-verifypassword" type="password" maxlength="100" />
               </div>
      
               <div class="field-row">
                  <span class="crud-label"><input type="checkbox" id="${el}-update-disableaccount" />&nbsp;${msg("label.disableaccount")}</span>
               </div>
               
               <div class="header-bar">${msg("label.photo")}</div>
               <div class="update-photo-row">
                  <div class="photo">
                     <img id="${el}-update-photoimg" class="update-photoimg left" src="${url.context}/res/components/images/no-user-photo-64.png" alt="" />
                     <div class="updateuser-clearphoto-button">
                        <span class="yui-button yui-push-button" id="${el}-updateuser-clearphoto-button">
                           <span class="first-child"><button>${msg("button.usedefault")}</button></span>
                        </span>
                     </div>
                  </div>
               </div>
            </div>
            
            <div>
               <div class="updateuser-save-button left">
                  <span class="yui-button yui-push-button" id="${el}-updateuser-save-button">
                     <span class="first-child"><button>${msg("button.savechanges")}</button></span>
                  </span>
               </div>
               <div class="updateuser-cancel-button">
                  <span class="yui-button yui-push-button" id="${el}-updateuser-cancel-button">
                     <span class="first-child"><button>${msg("button.cancel")}</button></span>
                  </span>
               </div>
            </div>
            </form>
         </div>
         
      </div>
   </@>
</@>

