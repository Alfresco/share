<#include "/org/alfresco/components/form/controls/selectone.ftl" />
<script type="text/javascript">//<![CDATA[
   (function()
   {
     // Bind selection-change to firing of global 'multipleSelectModeChanged' event
     var fieldId = "${fieldHtmlId}";
     
     YAHOO.util.Event.addListener(fieldId, 'change', function(e) {
         var val = YAHOO.util.Dom.get(fieldId).value;
         
         if(val == 'task')
         {
            // Task can have only 'one' assignee
            YAHOO.Bubbling.fire('multipleSelectModeChanged', false);
         }
         else if (val == 'review')
         {
            // Review can have only 'one' assignee
            YAHOO.Bubbling.fire('multipleSelectModeChanged', true);
         }
     });
   })();
   //]]></script>