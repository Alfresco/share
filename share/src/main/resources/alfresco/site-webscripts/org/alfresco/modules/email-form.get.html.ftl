<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="email-form">
   <div class="hd">${msg("header")}</div>
   <div class="bd">
      <!-- Email form -->
      <form id="${el}-form" method="POST" action="" enctype="application/json">
         <input id="${el}-template" type="hidden" name="template" value=""/>
         <div class="yui-gf">
            <div class="yui-u first">${msg("label.to")}:</div>
            <div class="yui-u">
               <ul id="${el}-recipients" class="recipients suppress-validation"></ul>
               <button id="${el}-selectRecipients-button" tabindex="0">${msg("button.select")}</button>
            </div>
         </div>
         <div class="yui-gf">
            <div class="yui-u first"><label for="${el}-subject">${msg("label.subject")}:</label></div>
            <div class="yui-u">
               <input id="${el}-subject" type="text" name="subject" tabindex="0" value=""/>
            </div>
         </div>
         <div class="yui-gf">
            <div class="yui-u first"><label for="${el}-message">${msg("label.message")}:</label></div>
            <div class="yui-u">
               <textarea id="${el}-message" name="message" rows="3" tabindex="0"></textarea>
               <button class="use-template" id="${el}-useTemplate-menu" tabindex="0">${msg("button.useTemplate")}</button>
               <select class="use-template" id="${el}-useTemplate-options">
                  <#list templates as template>
                  <option value="${template.value}">${template.displayLabel}</option>
                  </#list>
               </select>
               <br/>
               <button id="${el}-discardTemplate-button" tabindex="0">${msg("button.discardTemplate")}</button>
            </div>
         </div>
         <div class="bdft">
            <input type="button" id="${el}-ok-button" value="${msg("button.ok")}" tabindex="0"/>
            <input type="button" id="${el}-cancel-button" value="${msg("button.cancel")}" tabindex="0"/>
         </div>

         <!-- Authority Picker -->
         <div class="hidden">
            <div id="${el}-authority-picker" class="email-form authority-picker">
               <div class="hd">${msg("header.recipients")}</div>
               <div class="bd">
                  <div id="${el}-authority-finder" class="authority-finder"></div>
               </div>
            </div>
         </div>

      </form>
   </div>
</div>
<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.EmailForm");
//]]></script>