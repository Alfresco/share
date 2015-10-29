<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/rules/rules-list.css" group="rules"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/js/alfresco-dnd.js" group="rules"/>
   <@script src="${url.context}/res/components/rules/rules-list.js" group="rules"/>
</@>

<@markup id="widgets">
   <@createWidgets group="rules"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid>
      <div id="${el}-body" class="rules-list">
         <div id="${el}-rulesListText"></div>   
         <div class="rules-list-bar">
            <span class="rules-list-bar-icon">&nbsp;</span>
            <span id="${el}-rulesListBarText" class="rules-list-bar-text"></span>
         </div>
         <ul id="${el}-rulesListContainer" class="rules-list-container">
            <li class="message">${msg("message.loadingRules")}</li>
         </ul>
         <div id="${el}-buttonsContainer" class="rules-button-container hidden">
            <button id="${el}-save-button" tabindex="0">${msg("button.save")}</button>
            <button id="${el}-reset-button" tabindex="0">${msg("button.reset")}</button>
         </div>
      
         <!-- Rule Templates -->
         <div style="display:none">
            <ul id="${el}-ruleTemplate" >         
               <li class="rules-list-item">
                  <input type="hidden" class="id" name="id" value=""/>
                  <div class="rule-icons">
                     <span class="no">&nbsp;</span>
                     <span class="active-icon">&nbsp;</span>
                     <span class="rule-icon">&nbsp;</span>
                  </div>
                  <div class="info">
                     <a class="title" href="#">Name</a><span class="inherited">&nbsp;</span><br/>
                     <span class="inherited-from">&nbsp;</span><a class="inherited-folder">&nbsp;</a>
                     <span class="description">Description of the rules will go here</span>
                  </div>
                  <div class="clear"></div>
               </li>
            </ul>
         </div>
      </div>
   </@>
</@>