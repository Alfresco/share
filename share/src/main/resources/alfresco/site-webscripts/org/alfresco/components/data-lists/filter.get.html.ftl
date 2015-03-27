<@markup id="css">
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="datalists"/>
   <@inlineScript group="datalists">
      <#-- The following section of code is less than ideal. We should be setting the 
           filterIds directly within the widget instantiation rather than post instantiation
           to avoid iterating over the list twice -->
      <#assign filterIds = "">
      <#list filters as filter>
         <#assign filterIds>${filterIds}"${filter.id}"<#if filter_has_next>,</#if></#assign>
      </#list>
      filter.setFilterIds([${filterIds}]);
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="filter datalist-filter">
         <h2>${msg("header.items")}</h2>
         <ul class="filterLink">
         <#list filters as filter>
            <li><span class="${filter.id}"><a class="filter-link" rel="${filter.data?html}" href="#">${msg(filter.label)}</a></span></li>
         </#list>
         </ul>
      </div>
   </@>
</@>