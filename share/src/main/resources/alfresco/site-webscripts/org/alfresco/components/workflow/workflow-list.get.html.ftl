<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/workflow/workflow-list.css" group="workflow"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/workflow/workflow-actions.js" group="workflow"/>
   <@script src="${url.context}/res/components/workflow/workflow-list.js" group="workflow"/>
</@>

<@markup id="widgets">
   <@createWidgets group="workflow"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#import "workflow.lib.ftl" as workflow/>
      <#import "filter/filter.lib.ftl" as filter/>
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="workflow-list">
         <div class="yui-g workflow-list-bar flat-button">
            <div class="yui-u first">
               <h2 id="${el}-filterTitle" class="thin">
                  &nbsp;
               </h2>
            </div>
            <div class="yui-u">
               <div id="${el}-paginator" class="paginator">&nbsp;</div>
            </div>
         </div>
         <div id="${el}-workflows" class="workflows"></div>
      </div>
   </@>
</@>