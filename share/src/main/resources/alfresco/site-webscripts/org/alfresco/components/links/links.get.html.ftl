<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/links/links.css" group="links"/>
   <@link href="${url.context}/res/modules/taglibrary/taglibrary.css" group="links"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/links/links.js" group="links"/>
   <@script src="${url.context}/res/modules/taglibrary/taglibrary.js" group="links"/>
</@>

<@markup id="widgets">
   <@createWidgets group="links"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div id="${el}-links-header" class="links-header" style="visibility:hidden">
         
         <div id="${el}-linksBar" class="toolbar links-toolbar flat-button theme-bg-color-1">
            <div>
               <div id="${el}-create-link-container" class="createLink">
                  <div style="float:left"><button id="${el}-create-link-button" name="linklist-create-link-button">${msg("header.createLink")}</button></div>
                  <div class="separator hideable"> </div>
               </div>
               <div style="float:left" class="btn-selected-items">
                  <button id="${el}-selected-i-dd" name="linklist-create-link-button">${msg("header.selectedItems")}</button>
                  <div id="${el}-selectedItems-menu" class="yuimenu">
                     <div class="bd">
                        <ul>
                           <li><a class="deselect-item" rel="" href="#"><span class="links-action-deselect-all">${msg("links.deselectAll")}</span></a></li>
                        </ul>
                     </div>
                  </div>
               </div>
            </div>
            <@markup id="rssAction">
            <div class="rss-feed"><button id="${el}-rss-feed" name="rss-feed">${msg("header.rssFeed")}</button></div>
            </@>
         </div>
         
         <div id="${el}-links-titleBar" class="links-titlebar theme-bg-color-2">
            <div id="${el}-listTitle" class="list-title">${msg("title.generic")}</div>
         </div>
         
         <div id="${el}-links-infoBar" class="links-infobar flat-button" >
            <div class="vm-button-container">
               <button id="${el}-viewMode-button"
                       name="topiclist-simpleView-button">${msg("header.simpleList")}</button>
            </div>
            <div class="separator hideable">&nbsp;</div>
            <div id="${el}-paginator" class="paginator"></div>
            <div class="select-button-container">
               <button id="${el}-select-button">${msg("header.select")}</button>
               <div id="${el}-selecItems-menu" class="yuimenu">
                  <div class="bd">
                     <ul>
                        <li><a rel="" href="#"><span class="links-action-select-all">${msg("links.selectAll")}</span></a></li>
                        <li><a rel="" href="#"><span class="links-action-invert-selection">${msg("links.invertSelection")}</span></a></li>
                        <li><a rel="" href="#"><span class="links-action-deselect-all">${msg("links.none")}</span></a></li>
                     </ul>
                  </div>
               </div>
            </div>
         </div>
      </div>
      
      <div id="${el}-body" class="links-body" style="visibility:hidden">
         <div  id="${el}-links"> </div>
      </div>
   </@>
</@>