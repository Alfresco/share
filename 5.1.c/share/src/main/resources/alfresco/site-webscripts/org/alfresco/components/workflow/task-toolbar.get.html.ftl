<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="workflow"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#include "../../include/alfresco-macros.lib.ftl" />
      <div class="share-toolbar theme-bg-2">
         <div class="navigation-bar">
            <div>
               <span class="<#if (page.url.args.myTasksLinkBack! == "true")>backLink<#else>forwardLink</#if>">
                  <a href="${siteURL("my-tasks#filter=workflows|active")}">${msg("link.myTasks")}</a>
               </span>
            </div>
         </div>
      </div>
   </@>
</@>


