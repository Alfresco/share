<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/profile/profile.css" group="profile"/>
   <@link href="${url.context}/res/components/profile/usertrashcan.css" group="profile"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/profile/usertrashcan.js" group="profile"/>
</@>

<@markup id="widgets">
   <@createWidgets group="profile"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="trashcan profile">
         <div class="header-bar">${msg("label.trashcan")}</div>
         <div class="search-bar theme-bg-color-3">
            <div class="search-text toolbar-widget"><input type="text" id="${el}-search-text" name="-" value="" maxlength="256" tabindex="0"/></div>
            <div class="search-button toolbar-widget">
               <span id="${el}-search-button" class="yui-button yui-push-button">
                  <span class="first-child"><button>${msg("button.search")}</button></span>
               </span>
            </div>
            <div class="clear-button toolbar-widget">
               <span id="${el}-clear-button" class="yui-button yui-push-button">
                  <span class="first-child"><button>${msg("button.clear")}</button></span>
               </span>
            </div>
            <div class="selected-items-button toolbar-widget">
               <button id="${el}-selected">${msg("label.selectedItems")}</button>
               <div id="${el}-selectedItems-menu" class="yuimenu">
                  <div class="bd">
                     <ul>
                        <li><a class="recover-item" href="#">${msg("button.recover")}</a></li>
                        <li><a class="delete-item" href="#">${msg("button.delete")}</a></li>
                     </ul>
                  </div>
               </div>
            </div>
            <div class="select-button toolbar-widget">
               <button id="${el}-select-button">${msg("label.select")}</button>
               <div id="${el}-selectItems-menu" class="yuimenu">
                  <div class="bd">
                     <ul>
                        <li><a class="select-all" href="#">${msg("label.selectAll")}</a></li>
                        <li><a class="select-invert" href="#">${msg("label.selectInvert")}</a></li>
                        <li><a class="select-none" href="#">${msg("label.selectNone")}</a></li>
                     </ul>
                  </div>
               </div>
            </div>
            <div class="align-right">
               <div class="empty-button">
                  <span class="yui-button yui-push-button" id="${el}-empty-button">
                     <span class="first-child"><button>${msg("button.empty")}</button></span>
                  </span>
               </div>
            </div>
         </div>
         <div class="content">
            <div id="${el}-datalist" class="datalist"></div>
            <div>
               <div id="${el}-paginator" class="paginator">
                  <span class="yui-button yui-push-button" id="${el}-paginator-less-button">
                     <span class="first-child"><button>${msg("pagination.previousPageLinkLabel")}</button></span>
                  </span>
                  &nbsp;
                  <span class="yui-button yui-push-button" id="${el}-paginator-more-button">
                     <span class="first-child"><button>${msg("pagination.nextPageLinkLabel")}</button></span>
                  </span>
               </div>
            </div>
         </div>
      </div>
   </@>
</@>