<#include "/org/alfresco/components/form/controls/association.ftl" />

<#macro setSelectableMimeTypeOption field>

   <script type="text/javascript">//<![CDATA[
   (function()
   {
      <#-- Set the selectableMimeType property on the object finder created by association control-->
      var picker = Alfresco.util.ComponentManager.get("${controlId}");
      picker.options.selectableMimeType="application/json";
   })();
   //]]></script>

</#macro>

<@setSelectableMimeTypeOption field />