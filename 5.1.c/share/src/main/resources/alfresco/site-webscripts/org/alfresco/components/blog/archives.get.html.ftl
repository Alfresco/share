<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/blog/archive.js" group="blog"/>
</@>

<@markup id="widgets">
   <@createWidgets group="blog"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-body" class="filter blog-filter">
         <h2>${msg("header.title")}</h2>
         <ul class="filterLink" id="${args.htmlid}-archive"><li>&nbsp;</li></ul>
      </div>
   </@>
</@>