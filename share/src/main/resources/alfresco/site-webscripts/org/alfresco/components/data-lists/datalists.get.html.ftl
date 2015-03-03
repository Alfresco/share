<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/data-lists/datalists.css"  group="datalists"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/modules/simple-dialog.js"  group="datalists"/>
   <@script src="${url.context}/res/components/data-lists/datalists.js" group="datalists"/>
</@>

<@markup id="widgets">
   <@createWidgets group="datalists"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign id = args.htmlid>
      <div id="${id}-body" class="datalists">
         <div id="${id}-headerBar" class="header-bar toolbar flat-button theme-bg-2">
            <div class="left">
               <span id="${id}-newListButton" class="yui-button yui-push-button new-list">
                   <span class="first-child">
                       <button type="button">${msg('button.new-list')}</button>
                   </span>
               </span>
            </div>
         </div>
         <h2>${msg("header.lists")}</h2>
         <div id="${id}-lists" class="filter"></div>
         <div class="horiz-rule">&nbsp;</div>
      </div>
   </@>
</@>