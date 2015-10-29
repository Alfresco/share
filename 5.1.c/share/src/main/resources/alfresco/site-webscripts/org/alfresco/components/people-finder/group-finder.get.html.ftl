<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link href="${url.context}/res/components/people-finder/group-finder.css" group="people-finder"/>
   </@>
   
   <@markup id="js">
      <#-- JavaScript Dependencies -->
      <@script src="${url.context}/res/components/people-finder/group-finder.js" group="people-finder"/>
   </@>
   
   <@markup id="widgets">
      <@createWidgets group="people-finder"/>
   </@>
   
   <@markup id="html">
      <@uniqueIdDiv>
         <#assign el=args.htmlid?html>
         <div id="${el}-body" class="group-finder list">
            <div class="title"><label for="${el}-search-text">${msg("title")}</label></div>
            <div class="finder-wrapper">
               <div class="search-bar theme-bg-color-3">
                  <div class="search-text"><input type="text" id="${el}-search-text" name="-" value="" maxlength="255"/></div>
                  <div class="group-search-button">
                     <span id="${el}-group-search-button" class="yui-button yui-push-button"><span class="first-child"><button>${msg("button.search")}</button></span></span>
                  </div>
               </div>
               <div id="${el}-results" class="results"></div>
            </div>
         </div>
      </@>
   </@>
</@>
