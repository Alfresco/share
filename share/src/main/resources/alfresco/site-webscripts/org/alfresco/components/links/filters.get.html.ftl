<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="links"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div class="filter links-filter">
         <h2 id="${el}-h2">${msg("header.links")}</h2>
         <ul class="filterLink">
         <#list filters as filter>
            <li><span class="${filter.id}"><a class="filter-link" href="#">${msg(filter.label)}</a></span></li>
         </#list>
         </ul>
      </div>
   </@>
</@>

