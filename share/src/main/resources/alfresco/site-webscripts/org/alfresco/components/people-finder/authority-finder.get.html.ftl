<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link href="${url.context}/res/components/people-finder/authority-finder.css" group="people-finder"/>
   </@>
   
   <@markup id="js">
      <#-- JavaScript Dependencies -->
      <@script src="${url.context}/res/components/people-finder/authority-finder.js" group="people-finder"/>
   </@>
   
   <@markup id="widgets">
      <@inlineScript group="people-finder">
          var dataWebScript = Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/authority-query";
      </@>
      <@createWidgets group="people-finder"/>
   </@>
   
   <@markup id="html">
      <@uniqueIdDiv>
         <#assign el=args.htmlid?html>
         <div id="${el}-body" class="authority-finder list">
            <div id="${el}-title" class="title"><label for="${el}-search-text">&nbsp;</label></div>
            <div class="finder-wrapper">
               <div class="search-bar theme-bg-color-3">
                  <div class="search-text"><input type="text" id="${el}-search-text" name="-" value="" /></div>
                  <div class="authority-search-button">
                     <span id="${el}-authority-search-button" class="yui-button yui-push-button"><span class="first-child"><button>${msg("button.search")}</button></span></span>
                  </div>
               </div>
               <div id="${el}-results" class="results"></div>
            </div>
         </div>
      </@>
   </@>
</@>