<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/rules/rules-header.css" group="rules"/>
   <@link href="${url.context}/res/components/form/form.css" group="rules"/>
   <@link href="${url.context}/res/modules/documentlibrary/global-folder.css" group="rules"/>
   <@link href="${url.context}/res/modules/rules/rules-picker.css" group="rules"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/rules/rules-header.js" group="rules"/>
   <@script src="${url.context}/res/modules/documentlibrary/global-folder.js" group="rules"/>
   <@script src="${url.context}/res/modules/rules/rules-picker.js" group="rules"/>
   <@script src="${url.context}/res/components/common/common-component-style-filter-chain.js" group="rules"/>
</@>

<@markup id="widgets">
   <@createWidgets group="rules"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid>
      <#assign inheritRulesClass="off">
      <#if inheritRules>
         <#assign inheritRulesClass="on">
      </#if>
      <div id="${el}-body" class="rules-header">
         <div class="yui-g">
            <div class="yui-u first rules-title">
               <h1><span id="${el}-title"></span>: ${msg("header.rules")}</h1>
            </div>
            <div class="yui-u rules-actions">
               <span id="${el}-inheritButtonContainer" class="inherit inherit-${inheritRulesClass}">
                     <span id="${el}-inheritButton" class="yui-button yui-push-button">
                        <span class="first-child">
                           <button>${msg("button.inherit." + inheritRulesClass)}</button>
                        </span>
                     </span>
               </span>
               <span id="${el}-actions" class="hidden">
                  <span class="separator">&nbsp;</span>
                  <button class="new" id="${el}-newRule-button" tabindex="0">${msg("button.new-rule")}</button>
                  <span class="hidden">
                     <button class="copy" id="${el}-copyRuleFrom-button" tabindex="0">${msg("button.copy-rule-from")}</button>
                  </span>
                  <button class="run" id="${el}-runRules-menu" tabindex="0">${msg("menu.run")}</button>
                  <select class="run-menu" id="${el}-runRules-options">
                     <option value="run">${msg("menu.option.run")}</option>
                     <option value="run-recursive">${msg("menu.option.run-recursive")}</option>
                  </select>
               </span>
            </div>
         </div>
      </div>
   </@>
</@>