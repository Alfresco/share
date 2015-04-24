<#assign template_values = []>
<#assign template_config = config.scoped["TestConfiguration1"]["template-config"]!>
<#if template_config.getChildren??>
   <#assign template_values = template_config.getChildren()>
</#if>
<?xml version="1.0" encoding="UTF-8"?>
<root>
   <#list template_values as template_value>
      <content>${template_value.getValue()}</content>
   </#list>
   <#list controller_values as controller_value>
      <content>${controller_value.getValue()}</content>
   </#list>
   <@region id="config-ext-region" scope="global" chromeless="true"/>
</root>