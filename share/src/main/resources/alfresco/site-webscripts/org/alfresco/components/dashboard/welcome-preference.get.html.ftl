<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/dashboard/welcome-preference.css" group="dashboard"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/dashboard/welcome-preference.js" group="dashboard"/>
</@>

<@markup id="widgets">
   <@createWidgets group="dashboard"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="alf-welcome-preference">
         <div id="${args.htmlid}-welcome-preference" class="instructions">
            <h2>${msg("header.welcomePreference")}</h2>
            <hr />
            <div class="buttons alf-values" id="${args.htmlid}-welcomePreferenceButtonWrapper-div">
              <input id="${args.htmlid}-welcomePanelEnabled" type="radio" name="welcomePanelEnabled" value="true" <#if welcomePanelEnabled>checked</#if>>${msg("welcomePanel.enabled")}<br />
              <input id="${args.htmlid}-welcomePanelDisabled" type="radio" name="welcomePanelEnabled" value="false" <#if !welcomePanelEnabled>checked</#if>>${msg("welcomePanel.disabled")}
           </div>
         </div>
         <div class="actions">
             <hr />
                <div>
                   <div class="buttons">
                      <input id="${args.htmlid}-save-button" type="button" value="${msg("button.save")}" />
                      <input id="${args.htmlid}-cancel-button" type="button" value="${msg("button.cancel")}" />
                   </div>
                </div>
             </div>
          </div>
      </div>
   </@>
</@>