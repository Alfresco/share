<#assign el=args.htmlid?html>
<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/dashlets/activities.css" group="dashlets"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/dashlets/activities.js" group="dashlets"/>
</@>

<@markup id="widgets">
   <#assign id=el?replace("-", "_")>
   <@inlineScript group="dashlets">
      var activitiesFeedDashletEvent${id} = new YAHOO.util.CustomEvent("openFeedClick");
   </@>
   <@createWidgets group="dashlets"/>
   <@inlineScript group="dashlets">
      activitiesFeedDashletEvent${id}.subscribe(activities.openFeedLink, activities, true);
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign id = args.htmlid>
      <div class="dashlet activities">
         <div class="title">${msg("header")}</div>
         <div class="toolbar flat-button">
            <div class="hidden">
               <span class="align-left yui-button yui-menu-button" id="${id}-user">
                  <span class="first-child">
                     <button type="button" tabindex="0"></button>
                  </span>
               </span>
               <select id="${id}-user-menu">
               <#list filterTypes as filter>
                  <option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
               </#list>
               </select>
               <span class="align-left yui-button yui-menu-button" id="${id}-activities">
                  <span class="first-child">
                     <button type="button" tabindex="0"></button>
                  </span>
               </span>
               <select id="${id}-activities-menu">
               <#list filterActivities as filter>
                  <option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
               </#list>
               </select>
               <span class="align-left yui-button yui-menu-button" id="${id}-range">
                  <span class="first-child">
                     <button type="button" tabindex="0"></button>
                  </span>
               </span>
               <select id="${id}-range-menu">
               <#list filterRanges as filter>
                  <option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
               </#list>
               </select>
               <div class="clear"></div>
            </div>
         </div>
         <div id="${id}-activityList" class="body scrollableList" <#if args.height??>style="height: ${args.height?html}px;"</#if>></div>
      </div>
      
      <#-- Empty results list template -->
      <div id="${id}-empty" style="display: none">
         <div class="empty"><h3>${msg("empty.title")}</h3><span>${msg("empty.description")}</span></div>
      </div>
   </@>
</@>