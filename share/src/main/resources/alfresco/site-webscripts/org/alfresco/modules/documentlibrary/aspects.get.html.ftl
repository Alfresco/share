<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.ComponentManager.get("${el}").setOptions(
   {
      visible: [<#list aspects.visible as a>"${a}"<#if a_has_next>,</#if></#list>],
      addable: [<#list aspects.addable as a>"${a}"<#if a_has_next>,</#if></#list>],
      removeable: [<#list aspects.removeable as a>"${a}"<#if a_has_next>,</#if></#list>],
      labels: {
         <#list aspects.labels?keys as a>"${a}": "${aspects.labels[a]?js_string?html}"<#if a_has_next>,</#if></#list>
      }
   }).setMessages(${messages});
//]]></script>
<div id="${el}-dialog" class="aspects">
   <div id="${el}-title" class="hd"></div>
   <div class="bd">
      <form id="${el}-form" action="" method="post">
         <input type="hidden" name="added" id="${el}-added" value="" />
         <input type="hidden" name="removed" id="${el}-removed" value="" />
         <div class="yui-g">
            <div class="yui-u first">
               <div class="title-left">${msg("title.addable")}</div>
               <div id="${el}-left" class="list-left"></div>
            </div>
            <div class="yui-u">
               <div class="title-right">${msg("title.current")}</div>
               <div id="${el}-right" class="list-right"></div>
            </div>
         </div>
         <div class="bdft">
            <input type="button" id="${el}-ok" value="${msg("button.apply")}" tabindex="0" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>