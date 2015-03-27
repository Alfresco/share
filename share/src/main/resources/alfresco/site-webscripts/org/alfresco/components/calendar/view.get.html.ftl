<#assign view=context.properties.filteredView />

<@markup id="css" >
   <#-- CSS Dependencies -->
   <#if (view !='agenda')>
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/jquery/fullcalendar/fullcalendar.css" group="calendar"/>
   </#if>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/taglibrary/taglibrary.css" group="calendar"/>   
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="calendar"/>
   <@script type="text/javascript" src="${url.context}/res/components/calendar/calendar-view.js" group="calendar"/>
   <#if (view=='agenda')>
      <@script type="text/javascript" src="${url.context}/res/components/calendar/calendar-view-${context.properties.filteredView?js_string}.js" group="calendar"/>
   <#else>
      <@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js" group="calendar"/>
      <@script type="text/javascript" src="${url.context}/res/jquery/jquery-ui-1.8.11.custom.min.js" group="calendar"/>
      <@script type="text/javascript" src="${url.context}/res/jquery/fullcalendar/fullcalendar.js" group="calendar"/>
      <@script type="text/javascript" src="${url.context}/res/components/calendar/calendar-view-fullCalendar.js" group="calendar"/>
   </#if>
   <@script type="text/javascript" src="${url.context}/res/components/calendar/eventinfo.js" group="calendar"/>
   <@script type="text/javascript" src="${url.context}/res/modules/taglibrary/taglibrary.js" group="calendar"/>
</@>

<@markup id="widgets">
   <@createWidgets group="calendar"/>
   <@inlineScript group="calendar">
      Alfresco.util.addMessages(${messages}, "Alfresco.EventInfo");
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <#if (viewArgs.viewType=='agenda')>
         <!-- agenda -->
         <a href="" class="previousEvents hidden agendaNav">${msg("agenda.previous")}</a>
         <h2 id="calTitle">&nbsp;</h2>
         <div id="${el}Container" class="alf-calendar agendaview">
             <div id="${el}View">
                <div id="${el}View-noEvent" class="noEvent">
                   <p id="${el}View-defaultText" class="instructionTitle">${msg("agenda.initial-text")}</p>
                </div>
             </div>
         </div>
         <div class="nextEventsContainer">&nbsp;<a href="" class="nextEvents hidden agendaNav">${msg("agenda.next")}</a></div>
      
      <#else>
         <!--[if IE]>
         <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
         <![endif]-->
         <input id="yui-history-field" type="hidden" />
      
         <div id="${el}Container" class="alf-calendar fullCalendar">
             <div id="${el}View">
             </div>
         </div>
      </#if>
   </@>
</@>