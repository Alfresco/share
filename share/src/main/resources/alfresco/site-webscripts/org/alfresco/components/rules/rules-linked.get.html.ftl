<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/rules/rules-linked.css" group="rules"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/rules/rules-linked.js" group="rules"/>
</@>

<@markup id="widgets">
   <@createWidgets group="rules"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid>
      <div id="${el}-body" class="rules-linked">
         <div id="${el}-inheritedRules" class="rules-info theme-bg-color-2 theme-border-3 hidden">
            <span>${msg("label.folderInheritsRules")}</span>
         </div>
         <div class="link-info theme-bg-color-6 theme-border-3">
            <h2>${msg("label.linked-to-rules-set")}:</h2>
            <div class="yui-g">
               <div class="yui-u first">
                  <div class="info">
                     <h2 id="${el}-title">&nbsp;</h2>
                     <div>
                        <em>${msg("label.path")}: </em><span id="${el}-path">&nbsp;</span>
                     </div>
                  </div>
               </div>
               <div class="yui-u">
                  <input type="button" id="${el}-view-button" value="${msg("button.view")}" tabindex="0"/>
                  <input type="button" id="${el}-change-button" value="${msg("button.change")}" tabindex="0"/>
                  <input type="button" id="${el}-unlink-button" value="${msg("button.unlink")}" tabindex="0"/>
               </div>
            </div>
         </div>
         <div class="main-buttons">
            <input type="button" id="${el}-done-button" value="${msg("button.done")}" tabindex="0"/>
         </div>
      </div>
   </@>
</@>


