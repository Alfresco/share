<#assign el=args.htmlid?html>
<div id="${el}-body" class="theme-overlay guest-message hidden">
   <#if (args.logo!"true") == "true">
      <#assign aboutConfig=config.scoped["Edition"]["login"]>
      <div class="theme-company-logo ${aboutConfig.getChildValue("css-class")!logo-com}"></div>
   </#if>
   <#if args.header??>
      <#assign header = msg(args.header)/>
      <#if header != args.header>
         <h3 class="thin ${(args.headerClass!"")?html}">${msg(args.header?html)}</h3>
      </#if>
   </#if>
   <#if args.header?? && args.text??>
      <hr/>
   </#if>
   <#if args.text??>
      <#assign text = msg(args.text)/>
      <#if text != args.text>
      <p class="${(args.textClass!"")?html}">${text}</p>
      </#if>
   </#if>
</div>
<script type="text/javascript">//<![CDATA[
new Alfresco.component.GuestMessage("${args.htmlid?js_string}").setOptions({}).setMessages(${messages});
//]]></script>