<#function globalConfig key default>
   <#if config.global.flags??>
      <#assign values = config.global.flags.childrenMap[key]>
      <#if values?? && values?is_sequence>
         <#return values[0].value>
      </#if>
   </#if>
   <#return default>
</#function>

<#-- Global flags retrieved from web-framework-config-application -->
<#assign DEBUG=(globalConfig("client-debug", "false") = "true")>
<#assign AUTOLOGGING=(globalConfig("client-debug-autologging", "false") = "true")>


<#-- allow theme to be specified in url args - helps debugging themes -->
<#if page?? && page.url.templateArgs.theme?? && page.url.templateArgs.theme?length != 0>
   <#assign theme = page.url.templateArgs.theme?html />
<#elseif args?? && args.theme?? && args.theme?length != 0>
   <#assign theme = args.theme?html/>
</#if>

<#-- Portlet container detection -->
<#assign PORTLET=(context.attributes.portletHost!false)>
