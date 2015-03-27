<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/wiki/parser.js" group="dashlets"/>
   <@script type="text/javascript" src="${url.context}/res/components/dashlets/wiki.js" group="dashlets"/>
   <@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="dashlets"/>
</@>

<@markup id="widgets">
   <@inlineScript group="dashlets">
      var editWikiDashletEvent_${args.htmlid?replace("-", "_")} = new YAHOO.util.CustomEvent("onDashletConfigure");
   </@>
   <@createWidgets group="dashlets"/>
   <@inlineScript group="dashlets">
      editWikiDashletEvent_${args.htmlid?replace("-", "_")}.subscribe(wiki_${args.htmlid?replace("-", "_")}.onConfigFeedClick, wiki_${args.htmlid?replace("-", "_")}, true);
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="dashlet wiki">
         <div class="title" id="${args.htmlid}-title">${msg("label.header-prefix")}<#if wikiLink??> - <a href="wiki-page?title=${wikiLink?url}">${pageTitle!msg("label.header")}</a></#if></div>
         <div class="body scrollablePanel" <#if args.height??>style="height: ${args.height?html}px;"</#if>>
            <div id="${args.htmlid}-scrollableList" class="rich-content dashlet-padding">
            <#if wikipage??>${wikipage}
            <#else><h3>${msg("label.noConfig")}</h3>
            </#if>
            </div>
         </div>
      </div>
   </@>
</@>