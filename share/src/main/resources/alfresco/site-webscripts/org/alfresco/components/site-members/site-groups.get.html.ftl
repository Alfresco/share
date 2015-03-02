<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/site-members/site-groups.css" group="site-members"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/site-members/site-groups.js" group="site-members"/>
</@>

<@markup id="widgets">
   <@createWidgets group="site-members"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-body" class="site-groups">
         <div class="title"><label for="${args.htmlid}-term">${msg("site-groups.heading")}</label></div>
         <div class="add-groups">
         <#if currentUserRole = "SiteManager">
            <span id="${args.htmlid}-addGroups" class="yui-button yui-link-button">
               <span class="first-child">
                  <a href="add-groups">${msg("site-groups.add-groups")}</a>
               </span>
            </span>
         </#if>
         </div>
         <div class="finder-wrapper">
            <div class="search-controls theme-bg-color-3">
               <div class="search-text"><input id="${args.htmlid}-term" type="text" class="search-term" /></div>
               <div class="search-button"><button id="${args.htmlid}-button">${msg("site-groups.search-button")}</button></div>
            </div>
            <div id="${args.htmlid}-groups" class="results"></div>
         </div>
      </div>
   </@>
</@>