<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="workflow">
   <div id="${el}-title" class="hd">&nbsp;</div>
   <div class="bd">
      <form id="${el}-form" method="POST" action="" enctype="application/json">
         <input id="${el}-nodeRef" type="hidden" name="nodeRef" value=""/>
         <input id="${el}-path" type="hidden" name="path" value=""/>
         <div class="row">
            <label for="${el}-label">${msg("label.actionLabel")}:</label>
            <input id="${el}-label" type="text" name="label" tabindex="0" value="" maxlength="100"/>
         </div>
         <div class="row">
            ${msg("label.action.prefix")}
            <select name="action" class="workflow-action" id="${el}-action">
               <#list actions as action>
               <option value="${action.value}">${action.label}</option>
               </#list>
            </select>
            ${msg("label.action.suffix")}:        
            <span id="${el}-destinationLabel">&nbsp;</span>
            <button id="${el}-selectDestination-button" tabindex="0">${msg("button.select")}</button>
         </div>
         <div class="bdft">
            <input type="submit" id="${el}-ok-button" value="${msg("button.ok")}" tabindex="0"/>
            <input type="button" id="${el}-cancel-button" value="${msg("button.cancel")}" tabindex="0"/>
         </div>
      </form>
   </div>
</div>
<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.RulesWorkflowAction");
//]]></script>