<@markup id="css" >
   <#-- CSS Dependencies -->
   <#include "../form/form.css.ftl"/>
   <@link href="${url.context}/res/components/console/tag-management.css" group="console"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <#include "../form/form.js.ftl"/>
   <@script src="${url.context}/res/components/console/consoletool.js" group="console"/>
   <@script src="${url.context}/res/components/console/tag-management.js" group="console"/>
   <@script src="${url.context}/res/modules/simple-dialog.js" group="console"/>
   <@script src="${url.context}/res/modules/documentlibrary/doclib-actions.js" group="console"/>
</@>

<@markup id="widgets">
   <@createWidgets group="console"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div class="search-panel">
         <div class="search-text"><input type="text" id="${el}-search-text" name="-" value="" />
            <!-- Search button -->
            <div class="search-button">
               <span class="yui-button yui-push-button" id="${el}-search-button">
                  <span class="first-child"><button>${msg("button.search")}</button></span>
               </span>
            </div>
         </div>
      </div>
      
      <div class="dashlet tags-List">
         <div class="title">${msg("item.tagList")}</div>
         <div id="${el}-tags-list-info" class="tags-list-info"></div>
         <div id="${el}-tags-list-bar-bottom" class="toolbar theme-bg-color-3 hidden">
            <div id="${el}-paginator" class="paginator hidden">&nbsp;</div>
         </div>
         <div id="${el}-tags" class="body scrollableList" style="height: 100%; overflow: hidden"></div>
      </div>
   </@>
</@>
