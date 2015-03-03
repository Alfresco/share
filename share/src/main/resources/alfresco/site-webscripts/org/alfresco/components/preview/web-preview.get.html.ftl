<@standalone>
   
   <@markup id="css" >
      <#include "include/web-preview-css-dependencies.lib.ftl" />
   </@>
   
   <@markup id="js" >
      <#include "include/web-preview-js-dependencies.lib.ftl" />
   </@>

   <@markup id="widgets">
      <#if node??>
         <@createWidgets group="${dependencyGroup}"/>
      </#if>
   </@>

   <@markup id="html">
      <@uniqueIdDiv>
         <#if node??>
            <#assign el=args.htmlid?html>
         <div id="${el}-body" class="web-preview">
            <div id="${el}-previewer-div" class="previewer">
               <div class="message"></div>
            </div>
         </div>
         </#if>
      </@>
   </@>

</@standalone>
