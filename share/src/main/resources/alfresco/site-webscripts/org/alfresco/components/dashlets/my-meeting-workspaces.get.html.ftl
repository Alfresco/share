<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/dashlets/my-meeting-workspaces.css" group="dashlets"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/delete-site.css" group="dashlets"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/dashlets/my-meeting-workspaces.js" group="dashlets"/>
   <@script type="text/javascript" src="${url.context}/res/modules/delete-site.js" group="dashlets"/>
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="dashlet my-meeting-workspaces">
         <div class="title">${msg("header.myMeetingWorkspaces")}</div>
         <#if (sites?? && sites?size > 0)>
            <div id="${args.htmlid}-meeting-workspaces" class="body scrollableList" <#if args.height??>style="height: ${args.height?html}px;"</#if>>
         <#else>
            <div class="body scrollableList" <#if args.height??>style="height: ${args.height?html}px;"</#if>>
            <div class="dashlet-padding">
               <h3>${msg("label.noMeetingWorkspaces")}</h3>
            </div>
         </#if>
         </div>
      </div>
   </@>
</@>