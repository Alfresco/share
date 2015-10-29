<#include "/org/alfresco/components/form/controls/textfield.ftl" />

<script type="text/javascript">//<![CDATA[
(function()
{
      var fieldId = "${fieldHtmlId}";
       
      YAHOO.util.Event.onContentReady(fieldId, function() { 
         var parent = Dom.getAncestorByTagName(fieldId, "div");
         YAHOO.util.Dom.addClass(parent, 'hidden');
      });
      
      
      // Hide component by default
      YAHOO.util.Dom.addClass(fieldId, 'hidden');
            
      // Register handler when multipleSelectMode should be altered
      YAHOO.Bubbling.on("multipleSelectModeChanged", 
      
      function(layer, args){
          var newMultipleSelectMode = args[1];
          var parent = Dom.getAncestorByTagName(fieldId, "div");
          
          if(newMultipleSelectMode == true)
          {
            // Show this field
            YAHOO.util.Dom.removeClass(parent, 'hidden');
          }
          else
          {
            // Hide this field
            YAHOO.util.Dom.addClass(parent, 'hidden');
          }
      }
      , this);
})();

//]]></script>