<#macro template header jsClass="Alfresco.component.BaseFilter" filterName="">
   <#local filterIds = "">
   <div class="filter">
      <h2>${header}</h2>
      <ul class="filterLink">
      <#list filters as filter>
         <#local filterIds>${filterIds}"${filter.id}"<#if filter_has_next>,</#if></#local>
         <li><span class="${filter.id}"><a class="filter-link" rel="${filter.data?js_string}" href="#">${msg(filter.label?html)}</a></span></li>
      </#list>
      </ul>
   </div>
   <#nested>
   <script type="text/javascript">//<![CDATA[
      new ${jsClass}("${filterName!jsClass}", "${args.htmlid?js_string}").setFilterIds([${filterIds}]);
   //]]></script>
</#macro>

<#macro jsonParameterFilter filterParameters>
[<#list filterParameters as filterParameter>
   {
      id: "${filterParameter.id?js_string}",
      "data": "${filterParameter.data?js_string}",
      "parameters": "${filterParameter.parameters?js_string}"
   }<#if filterParameter_has_next>,</#if>
</#list>]
</#macro>