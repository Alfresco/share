<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.PropertyPicker");
<#if transientContentProperties??>
Alfresco.util.ComponentManager.get("${el}").setOptions(
{
   transientProperties: {
      "d:content" : ${transientContentProperties}
   },
   <#if (classFilter??)>classFilter: ${classFilter}</#if>         
});
</#if>
//]]></script>