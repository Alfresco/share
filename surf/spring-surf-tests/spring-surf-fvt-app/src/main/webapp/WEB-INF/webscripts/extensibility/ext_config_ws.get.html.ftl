<#assign template_values = []>
<#assign template_config = config.scoped["TestConfiguration3"]["ws-template-config"]!>
<#if template_config.getChildren??>
   <#assign template_values = template_config.getChildren()>
</#if>
<#list template_values as template_value>
      <content>${template_value.getValue()}</content>
</#list>
<#list controller_values as controller_value>
   <content>${controller_value.getValue()}</content>
</#list>