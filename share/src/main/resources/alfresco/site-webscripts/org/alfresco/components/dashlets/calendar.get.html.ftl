<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/dashlets/mini-calendar.js" group="dashlets"/>
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="dashlet calendar">
         <div class="title">${msg("label.header")}</div>
         <div id="${args.htmlid}-eventsContainer" class="body scrollableList" <#if args.height??>style="height: ${args.height?html}px;"</#if>>
         </div>
      </div>
   </@>
</@>