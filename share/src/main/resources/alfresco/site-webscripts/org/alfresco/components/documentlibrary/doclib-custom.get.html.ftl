<#--
   Configured dependencies.
   TODO: Temporary code to be removed when config reader implemented.
-->
<#if dependencies??>
   <#if dependencies.css??>
      <#list dependencies.css as cssFile>
         <@link rel="stylesheet" type="text/css" href="${url.context}/res/${cssFile}" group="documentlibrary"/>
      </#list>
   </#if>
   <#if dependencies.js??>
      <#list dependencies.js as jsFile>
         <@script type="text/javascript" src="${url.context}/res/${jsFile}" group="documentlibrary"/>
      </#list>
   </#if>
</#if>
