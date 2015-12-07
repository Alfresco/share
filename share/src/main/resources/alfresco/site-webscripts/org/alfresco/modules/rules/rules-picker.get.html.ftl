<#assign el=args.htmlid?html>
<div id="${el}-rulesContainer" class="rules">
   <h3>${msg("header.rule-picker")}</h3>
   <div id="${el}-rulePicker" class="rule-picker"></div>
</div>
<#assign treeConfig = config.scoped["DocumentLibrary"]["tree"]!>
<#if treeConfig.getChildValue??><#assign evaluateChildFolders = treeConfig.getChildValue("evaluate-child-folders")!"true"></#if>
<#assign commonComponentConfig = config.scoped["CommonComponentStyle"]["component-style"]!>
<#if commonComponentConfig.value??>
        <#assign tmp = commonComponentConfig.value>
        <#assign customFolderStyleConfig = tmp!"">
</#if>
<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.RulesPicker");
Alfresco.util.ComponentManager.get("${el}").options.evaluateChildFolders = ${evaluateChildFolders!"true"};
Alfresco.util.ComponentManager.get("${el}").options.customFolderStyleConfig= <#if customFolderStyleConfig??>${(customFolderStyleConfig!"")}<#else>null</#if>;
//]]></script>