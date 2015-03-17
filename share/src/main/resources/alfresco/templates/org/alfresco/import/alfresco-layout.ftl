<#macro grid columns class bindPrefix>
   <#if (columns?size &lt; 4)>
      <@_normalGrid columns class bindPrefix/>
   <#else>
      <@_nestedGrid columns class bindPrefix/>
   </#if>
</#macro>

<#macro _normalGrid columns class bindPrefix>
   <div class="${class} grid columnSize${columns?size}">
      <#list columns as column>
         <div class="yui-u<#if column_index == 0> first</#if> column${column_index + 1} dcolumn">
            <#list 1..column.components as component>
               <@region id="${bindPrefix + '-' + (column_index + 1) + '-' + (component_index + 1)}" scope="page" />
            </#list>
         </div>
      </#list>
   </div>
</#macro>

<#macro _nestedGrid columns class bindPrefix>
   <div class="${class} grid columnSize${columns?size}">
      <#list columns as column>
         <#if (column_index % 2 == 0)>
            <div class="yui-g<#if column_index == 0> first</#if> column${column_index + 1}">
         </#if>
         <div class="yui-u<#if column_index%2 == 0> first</#if> column${column_index + 1} dcolumn">
            <#list 1..column.components as component>
               <@region id="${bindPrefix + '-' + (column_index + 1) + '-' + (component_index + 1)}" scope="page" />
            </#list>
         </div>
         <#if (column_index % 2 == 1 || !column_has_next)>
            </div>
         </#if>
      </#list>
   </div>
</#macro>