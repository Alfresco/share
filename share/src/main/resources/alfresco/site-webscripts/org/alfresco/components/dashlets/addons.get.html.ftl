<#import "/org/alfresco/utils/feed.utils.ftl" as feedLib/>
<#assign el=args.htmlid?html>

<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
  <@script type="text/javascript" src="${url.context}/res/components/dashlets/rssfeed.js" group="dashlets"/>
  <@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="dashlets"/>
</@>

<@markup id="widgets">
   <#assign id=el?replace("-", "_")>
   <@inlineScript group="dashlets">
      var addOnsRssFeedDashletEvent${id} = new YAHOO.util.CustomEvent("openFeedClick");
   </@>
   <@createWidgets group="dashlets"/>
   <@inlineScript group="dashlets">
      addOnsRssFeedDashletEvent${id}.subscribe(addOnsRssFeed.onConfigFeedClick, addOnsRssFeed, true);
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="dashlet rssfeed">
         <div class="title" id="${el}-title">${title!msg("label.header")}</div>
         <div class="toolbar">
            <div>${msg("label.body")} <a href="${msg("label.addonsLink")}" target="${target}">${msg("label.addonsText")}</a></div>
         </div>
         <div class="body scrollableList" <#if args.height??>style="height: ${args.height?html}px;"</#if>>
            <div class="dashlet-padding" id="${el}-scrollableList">
               <h3>${msg("label.loading")}</h3>
            </div>
         </div>
      </div>
   </@>
</@>