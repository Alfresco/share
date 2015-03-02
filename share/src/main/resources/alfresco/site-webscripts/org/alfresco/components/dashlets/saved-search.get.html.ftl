<#assign el=args.htmlid?html>

<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/dashlets/saved-search.css" group="dashlets" />
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/search/search-lib.js" group="dashlets"/>
   <@script type="text/javascript" src="${url.context}/res/components/dashlets/saved-search.js" group="dashlets"/>
   <@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="dashlets"/>
</@>

<@markup id="widgets">
   <#assign id=el?replace("-", "_")>
   <@inlineScript group="dashlets">
      var savedSearchDashletEvent${id} = new YAHOO.util.CustomEvent("onConfigSearchClick");
   </@>
   <@createWidgets group="dashlets"/>
   <@inlineScript group="dashlets">
      savedSearchDashletEvent${id}.subscribe(savedSearch.onConfigSearchClick, savedSearch, true);
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="dashlet savedsearch">
         <div class="title" id="${el}-title">${msg("header.title")}</div>
         <div id="${el}-list" class="body scrollableList" <#if args.height??>style="height: ${args.height?html}px;"</#if>>
            <div id="${el}-search-results"></div>
         </div>
      </div>
   </@>
</@>