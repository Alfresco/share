<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link href="${url.context}/res/components/people-finder/people-finder.css" group="people-finder"/>
   </@>
   
   <@markup id="js">
      <#-- JavaScript Dependencies -->
      <@script src="${url.context}/res/components/people-finder/people-finder.js" group="people-finder"/>
   </@>
   
   <@markup id="widgets">
      <@createWidgets group="people-finder"/>
   </@>
   
   <@markup id="html">
      <@uniqueIdDiv>
         <#assign el=args.htmlid?html>
         <div id="${el}-body" class="people-finder list theme-color-1">
            <div class="title theme-color-2"><label for="${el}-search-text">${msg("title")}</label></div>
            <div class="finder-wrapper">
               <@markup id="searchBar">
               <div class="search-bar theme-bg-color-3">
                  <div class="search-text"><input type="text" id="${el}-search-text" name="-" value="" maxlength="256" tabindex="0" placeholder="${msg('help.title')}"/></div>
                  <div class="search-button alf-colored-button">
                     <span id="${el}-search-button" class="yui-button yui-push-button"><span class="first-child"><button>${msg("button.search")}</button></span></span>
                  </div>
               </div>
               </@markup>

               <@markup id="searchHelp">
               <div id="${el}-help" class="yui-g theme-bg-color-2 help hidden">
                  <span>${msg("help.content")}</span>
               </div>
               </@markup>

               <@markup id="searchResults">
               <div class="alf-results-wrapper">
                  <div id="${el}-results-info" class="yui-dt-liner theme-bg-color-2 result-info hidden"></div>
                  <div id="${el}-results" class="results hidden"></div>
               </div>
               </@markup>
            </div>
         </div>
      </@>
   </@>
</@>
