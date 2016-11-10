<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/invite/sent-invites.css" group="invite"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/invite/sent-invites.js" group="invite"/>
</@>

<@markup id="widgets">
   <@createWidgets group="invite"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-pendingrequests" class="sent-invites">
         <div class="title">
            <label for="${args.htmlid}-search-text">${msg("pendingrequests.title")}</label>
         </div>
         <div id="${args.htmlid}-wrapper" class="sent-invites-wrapper">
            <div class="search-controls theme-bg-color-3">
               <div class="search-text"><input id="${args.htmlid}-search-text" type="text" /></div>
               <div class="search-button"><button id="${args.htmlid}-search-button">${msg("button.search")}</button></div>
            </div>
            <div id="${args.htmlid}-results" class="results"></div>
         </div>
      </div>
   </@>
</@>