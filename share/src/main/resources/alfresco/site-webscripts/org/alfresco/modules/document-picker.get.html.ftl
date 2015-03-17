<#assign controlId = args.htmlid?html + "-cntrl">
<#assign pickerId = controlId + "-picker">

<div id="${pickerId}" class="picker document-picker">
   <div id="${pickerId}-head" class="hd">${msg("label.document-picker-header")}</div>

   <div id="${pickerId}-body" class="bd">
      <div class="picker-header">
         <span class="folder-up">
            <button id="${pickerId}-folderUp">&nbsp;</button>
         </span>
         <span class="navigator">
            <button id="${pickerId}-navigator" class="navigator"></button>
         </span>
         <div id="${pickerId}-navigatorMenu" class="yuimenu">
            <div class="bd">
               <ul id="${pickerId}-navigatorItems" class="navigator-items-list">
                  <li>&nbsp;</li>
               </ul>
            </div>
         </div>
      </div>
      <div class="yui-g">
         <div id="${pickerId}-left" class="yui-u first panel-left">
            <div id="${pickerId}-results" class="picker-items"></div>
         </div>
         <div id="${pickerId}-right" class="yui-u panel-right">
            <div id="${pickerId}-selectedItems" class="picker-items"></div>
         </div>
      </div>
      <div class="bdft">
         <button id="${controlId}-ok" tabindex="0" disabled>${msg("button.ok")}</button>
         <button id="${controlId}-cancel" tabindex="0">${msg("button.cancel")}</button>
      </div>
   </div>
</div>
<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.DocumentPicker");
//]]></script>
