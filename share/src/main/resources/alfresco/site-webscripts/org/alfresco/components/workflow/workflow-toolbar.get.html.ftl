<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
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
               <span class="<#if (page.url.args.myWorkflowsLinkBack! == "true")>backLink<#else>forwardLink</#if>">
                  <a href="${siteURL("my-workflows#filter=workflows|active")}">${msg("link.myWorkflows")}</a>
               </span>
            </div>
         </div>
      </div>
   </@>
</@>


