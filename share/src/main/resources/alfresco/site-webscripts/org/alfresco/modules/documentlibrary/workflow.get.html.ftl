<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.ComponentManager.get("${el}").setMessages(${messages});
//]]></script>
<div id="${el}-dialog" class="workflow">
   <div id="${el}-title" class="hd"></div>
   <div class="bd">
      <form id="${el}-form" action="" method="post">
         <input type="hidden" name="date" id="${el}-date" value="" />
         <div class="yui-g">
            <h2>${msg("header.type")}</h2>
         </div>
         <div class="field">
            <select id="${el}-type" name="type" tabindex="0">
            <#list workflows as w>
               <option value="${w}"<#if w_index == 0> selected="selected"</#if>>${msg("workflow." + w?replace(":", "_"))}</option>
            </#list>
            </select>
         </div>
         <div class="yui-g">
            <h2>${msg("header.people")}</h2>
         </div>
         <div class="yui-ge field">
            <div class="yui-u first">
               <div id="${el}-peoplefinder"></div>
            </div>
            <div class="yui-u">
               <div id="${el}-peopleselected" class="people-selected"></div>
            </div>
         </div>
         <div class="yui-g">
            <h2>${msg("header.date")}</h2>
         </div>
         <div class="field">    
            <input id="${el}-dueDate-checkbox" name="-" type="checkbox" value="${msg("label.due-date.none")}" tabindex="0"/>&nbsp;
            <span id="${el}-dueDate"><label for="${el}-dueDate-checkbox">${msg("label.due-date.none")}</label></span>
         </div>
         <div id="${el}-calendarOverlay" class="calendar-overlay">
            <div class="bd">
               <div id="${el}-calendar" class="calendar"></div>
            </div>
         </div>
         <div class="yui-g">
            <h2>${msg("header.comment")}</h2>
         </div>
         <div class="field">
            <textarea id="${el}-comment" name="description" rows="3" tabindex="0"></textarea>
            <span>${msg("label.comment.max-length")}</span>
            </div>
         <div class="bdft">
            <input type="button" id="${el}-ok" value="${msg("button.assign")}" tabindex="0" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>