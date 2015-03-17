<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/rules/rules-none.css" group="rules"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/rules/rules-none.js" group="rules"/>
</@>

<@markup id="widgets">
   <@createWidgets group="rules"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#include "../../include/alfresco-macros.lib.ftl" />
      <#assign el=args.htmlid>
      <div id="${el}-body" class="rules-none">
         <div id="${el}-inheritedRules" class="rules-info theme-bg-color-2 theme-border-3 hidden">
            <span>${msg("label.folderInheritsRules")}</span>
         </div>
         <div class="dialog-options theme-bg-color-6 theme-border-3">
            <h2>${msg("header")}</h2>
            <div class="dialog-option">
               <#assign href>rule-edit?nodeRef=${(page.url.args.nodeRef!"")?url}</#assign>
               <a href="${siteURL(href)}">${msg("header.create-rule")}</a>
               <div>${msg("text.create-rule")}</div>
            </div>
            <div class="dialog-option">
               <a id="${el}-linkToRuleSet" href="#">${msg("header.link-to-rule-set")}</a>
               <div>${msg("text.link-to-rule-set")}</div>
            </div>
         </div>
      </div>
   </@>
</@>