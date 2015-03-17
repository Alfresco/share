<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@script type="text/javascript" src="${url.context}/res/templates/rules/folder-rules.js"></@script>
</@>

<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
   </div>
   </@>
   <@markup id="bd">
   <div id="bd">
      <@region id="path" scope="template" />
      <@region id="rules-header" scope="template" />
      <div class="clear"></div>

      <#if ruleset.linkedToRuleSet??>
         <@region id="rules-linked" scope="template" />
      <#elseif ruleset.rules??>
         <div class="yui-g">
            <div class="yui-g first">
               <div id="inherited-rules-container" class="hidden">
               <@region id="inherited-rules" scope="template" />
               </div>
               <@region id="folder-rules" scope="template" />
            </div>
            <div class="yui-g">
               <@region id="rule-details" scope="template" />
            </div>
         </div>
      <#else>
         <@region id="rules-none" scope="template" />
      </#if>
   </div>
   </@>

   <@markup id="folder-rules">
   <script type="text/javascript">//<![CDATA[
   new Alfresco.FolderRules().setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${url.args.nodeRef?js_string}"),
      siteId: "${page.url.templateArgs.site!""}",
      folder:
      {
         nodeRef: "${folder.nodeRef}",
         site: "${folder.site?js_string}",
         name: "${folder.name?js_string}",
         path: "${folder.path?js_string}"
      },
      ruleset:
      {
         rules: <#if ruleset.rules??>[<#list ruleset.rules as rule>
            {
               id: "${rule.id}",
               title: "${(rule.title!"")?js_string}",
               description: "${(rule.description!"")?replace("\\n", "\\\\n")?js_string}",
               url: "${rule.url?js_string}",
               disabled: ${rule.disabled?string},
               owningNode:
               {
                  nodeRef : "${rule.owningNode.nodeRef}",
                  name : "${rule.owningNode.name?js_string}"
               }
            }<#if rule_has_next>,</#if></#list>
         ]<#else>null</#if>,
         inheritedRules: <#if ruleset.inheritedRules??>[<#list ruleset.inheritedRules as rule>
            {
               id: "${rule.id}",
               title: "${(rule.title!"")?js_string}",
               description: "${(rule.description!"")?replace("\\n", "\\\\n")?js_string}",
               url: "${rule.url?js_string}",
               disabled: ${rule.disabled?string},
               owningNode:
               {
                  nodeRef : "${rule.owningNode.nodeRef}",
                  name : "${rule.owningNode.name?js_string}"
               }
            }<#if rule_has_next>,</#if></#list>
         ]<#else>null</#if>,
         linkedFromRuleSets: <#if ruleset.linkedFromRuleSets??>[<#list ruleset.linkedFromRuleSets as link>
            "${link}"<#if link_has_next>,</#if></#list>
         ]<#else>null</#if>,
         linkedToRuleSet: <#if ruleset.linkedToRuleSet??>"${ruleset.linkedToRuleSet}"<#else>null</#if>
      },
      linkedToFolder: <#if linkedToFolder??>
      {
         nodeRef: "${linkedToFolder.nodeRef}",
         site: "${linkedToFolder.site?js_string}",
         name: "${linkedToFolder.name?js_string}",
         path: "${linkedToFolder.path?js_string}"
      }<#else>null</#if>
   });
   //]]></script>
   </@>
</@>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
   </@>
</@>
