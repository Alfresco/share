<#import "rule-config.lib.ftl" as configLib/>
<#assign el=args.htmlid?html>
<#assign mode=args.mode!"if">
<#assign msgKey=(ruleConfigType + "." + mode)>
<#if mode == "if">
   <#-- Turn on relations when that is supported by settings last parameter to true instead of false -->
   <@configLib.printRuleConfig el component ruleConfigType msgKey "checked" false/>
<#elseif mode == "unless">
   <@configLib.printRuleConfig el component ruleConfigType msgKey "unchecked" false/>
</#if>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.ComponentManager.get("${el}").setOptions(
   {
      <#if (comparePropertyValueDefinition?exists)>comparePropertyValueDefinition: ${comparePropertyValueDefinition},</#if>
      <#if (compareMimeTypeDefinition?exists)>compareMimeTypeDefinition: ${compareMimeTypeDefinition},</#if>
      properties: <#if (properties?exists)>${properties}<#else>[]</#if>
   });
//]]></script>