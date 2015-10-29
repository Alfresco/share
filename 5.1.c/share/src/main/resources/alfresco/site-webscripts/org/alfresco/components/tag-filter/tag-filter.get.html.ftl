<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/tag-filter/tag-filter.js" group="tag-filter"/>
</@>

<@markup id="widgets">
   <@createWidgets group="tag-filter"/>
   <@inlineScript group="tag-filter">
      tagFilter.setFilterIds(["tag"]);
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="filter">
         <h2 class="alfresco-twister">${msg("header.title")}</h2>
         <ul class="filterLink" id="${args.htmlid?html}-tags"><li>&nbsp;</li></ul>
      </div>
   </@>
</@>
