<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/calendar/calendar.css" group="calendar"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/calendar/calendar.js" group="calendar"/>
</@>

<@markup id="widgets">
   <@createWidgets group="calendar"/>
   <@inlineScript group="calendar">
      <#-- JavaScript to be executed AFTER widget instantiation here -->
      calendar.setSiteId("${page.url.templateArgs.site!""}");
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
     <#assign el=args.htmlid?html>
     <div id="${el}-body">
         <div id="calendar"></div>
         <div>
            <div id="${el}-viewButtons" class="calendar-currentMonth"><a href="#" id="${el}-thisMonth-button">${msg("button.this-month")}</a></div>
         </div>
      </div>
   </@>
</@>