<#--
   Deprecated: These files are now brought in for every page from the extendable components/head/resources.get.html webscript.
-->
<#macro uriTemplates></#macro>
<#macro helpPages></#macro>
<#macro htmlEditor htmlEditor="YAHOO.widget.SimpleEditor"></#macro>

<#function globalConfig key default>
   <#if config.global.flags??>
      <#assign values = config.global.flags.childrenMap[key]>
      <#if values?? && values?is_sequence>
         <#return values[0].value>
      </#if>
   </#if>
   <#return default>
</#function>