<#macro printRuleConfig el class configType msgKey enableButton relationMenu>
<script type="text/javascript">//<![CDATA[
   new ${component}("${el}").setOptions(
   {
      <#if (menuMap??)>menuMap: ${menuMap},</#if>
      <#if (ruleConfigDefinitions??)>ruleConfigDefinitions: ${ruleConfigDefinitions},</#if>
      <#if (customisationsMap??)>customisationsMap: ${customisationsMap},</#if>
      <#if (constraints??)>constraints: ${constraints},</#if>
      <#if (constraintsFilter??)>constraintsFilter: ${constraintsFilter},</#if>      
      <#if (rootNode??)>rootNode: "${rootNode}",</#if>
      repositoryBrowsing: ${(rootNode??)?string},
      ruleConfigType: "${ruleConfigType}",
      siteId: "${(args.site!"")?js_string}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-body" class="rule-config ${configType}">
   <div class="rule-config-header">
      <#if enableButton?length &gt; 0>
      <div class="rule-config-title">
         <input type="checkbox" id="${el}-${configType}-checkbox" name="-" class="hidden" <#if enableButton == "checked">checked</#if>>
         <label for="${el}-${configType}-checkbox">${msg("header." + msgKey)}</label>
      </div>
      <#else>
      <div class="rule-config-title">${msg("header." + msgKey)}</div>
      </#if>
      <#if relationMenu>
      <input class="rule-config-relation" type="button" id="${el}-${configType}-menubutton" name="${el}-${configType}-menubutton_button" value="${msg("label.and")}">
      <select id="${el}-${configType}-menubuttonselect" name="${el}-${configType}-menubuttonselect">
          <option value="and">${msg("label.and")}</option>
          <option value="or">${msg("label.or")}</option>
      </select>
      <div class="clear"></div>
      </#if>
   </div>
   <ul id="${el}-configs" class="rule-config-body <#if enableButton == "unchecked">hidden</#if>">
   </ul>
</div>
<div class="hidden">
   <li id="${el}-configTemplate" class="config suppress-validation">
      <input type="hidden" name="id" value=""/>
      <div class="actions">
         <span class="yui-button yui-push-button add-config">
            <span class="first-child"><button type="button">+</button></span>
         </span>
         <span class="yui-button yui-push-button remove-config">
            <span class="first-child"><button type="button">-</button></span>
         </span>
      </div>
      <div class="name"><!-- select element will be placed in here --></div>
      <div class="parameters"><!-- parameter controls will be placed here--></div>
      <div class="clear"></div>
   </li>
</div>
</#macro>
