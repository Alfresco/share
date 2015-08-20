<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/links/linksview.css" group="links"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/links/linksview.js" group="links"/>
</@>

<@markup id="widgets">
   <@createWidgets group="links"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div class="linksview-header theme-bg-color-1">
         <div class="navigation-bar theme-bg-color-2">
            <div>
               <span class="<#if (page.url.args.listViewLinkBack! == "true")>back-link<#else>forward-link</#if>">
                  <a href="${url.context}/page/site/${page.url.templateArgs.site?url}/links">${msg("header.back")}</a>
               </span>
            </div>
         </div>
         <div class="action-bar"></div>
      </div>
      <div id="${el}-link">
         <div id="${el}-link-view-div"></div>
      </div>
   </@>
</@>