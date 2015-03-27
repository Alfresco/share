<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="discussions"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="filter topiclist-filter">
         <h2>${msg("header.browsetopics")}</h2>
         <ul class="filterLink">
         <#list filters as filter>
            <li><span class="${filter.id}"><a class="filter-link" href="#">${msg(filter.label)}</a></span></li>
         </#list>
         </ul>
      </div>
   </@>
</@>