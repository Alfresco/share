<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/tags/tags.css" group="tags"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/tags/tags.js" group="tags"/>
</@>

<@markup id="widgets">
   <@createWidgets group="tags"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="filter tags">
         <h2 id="${args.htmlid?html}-h2">${msg("header.title")}</h2>
         <ul id="${args.htmlid?html}-ul" class="filterLink">
      <#if tags?size &gt; 0>
            <li><span class="tag"><a href="#" class="tag-link" rel="-all-">${msg("label.all-tags")}</a></span></li>
         <#list tags as tag>
            <li><span class="tag"><a href="#" class="tag-link" rel="${tag.name?html}">${tag.name?html}</a>&nbsp;(${tag.count})</span></li>
         </#list>
      <#else>
            <li>${msg("label.no-tags")}</li>
      </#if>
         </ul>
      </div>
   </@>
</@>
