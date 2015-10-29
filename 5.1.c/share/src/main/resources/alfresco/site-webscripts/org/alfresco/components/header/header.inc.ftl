<#include "../../include/alfresco-macros.lib.ftl" />
<#assign id = "">
<#assign js = "">

<#--
   Pre-configured conditions
-->
<#assign conditionRepositoryRootNode = ((config.scoped["RepositoryLibrary"]["root-node"].getValue())!"") != "">
<#assign conditionvalueEdition = (context.properties["editionInfo"].edition)!"UNKNOWN">
<#assign conditionEditionCommunity = conditionvalueEdition == "UNKNOWN">
<#assign conditionEditionEnterprise = conditionvalueEdition == "ENTERPRISE">
<#assign conditionEditionTeam = conditionvalueEdition == "TEAM">

<#--
   Application and User Items entrypoint
-->
<#macro renderItems p_root p_id p_type>
   <#assign id = p_id>
   <#assign js = "">

   <#-- Render an application item -->
   <#list p_root.items as item>
      <@menuItem item p_type />
      <#if p_type = "user" && item_has_next><span class="separator">&nbsp;</span></#if>
   </#list>
   <#-- Remove first comma -->
   <#if js?starts_with(",")><#assign js = js?substring(1)></#if>
</#macro>

<#--
   Render a top-level item
-->
<#macro menuItem item prefix="id">
<#-- Permission check -->
<#if (item.permission?length > 0)><#if !(permissions[item.permission]!false)><#return></#if></#if>
<#-- Condition check -->
<#if (item.condition?length > 0)><#if !(item.condition?eval)><#return></#if></#if>
<#-- Attributes -->
<#assign itemId = id + "-${prefix}_" + item.generatedId>
<#assign itemMenuId = id + "-${prefix}menu_" + item.generatedId>
<#assign attrStyle><#if (item.icon?length > 0)>style="background-image: url(${url.context}/res/components/images/header/${item.icon});"</#if></#assign>
<#assign attrTitle><#if (item.description?length > 0)>title="${msg(item.description)}"</#if></#assign>
<#assign attrHref = "href=\"\"">
   <#if (item.value?starts_with("http"))><#assign attrHref>href="${item.value}"</#assign>
   <#elseif (item.value?length > 0)><#assign attrHref>templateUri="${item.value}" href=""</#assign>
   </#if>
<#assign label = msg(item.label)><#if label?contains("{")><#assign label = msgArgs(item.label, labelTokens)?html></#if>
<span id="${itemId}" class="yui-button">
   <span class="first-child" ${attrStyle!""}>
   <#if item.type = "container">
      <#assign js>${js}, (function(){ var btn = new YAHOO.widget.Button("${itemId}", { type: "menu", menu: "${itemMenuId}", lazyloadmenu: false }); btn.getMenu().cfg.setProperty("keepopen", true); return btn; })()</#assign>
      <button ${attrTitle!""} tabindex="0">${label}</button>
   <#elseif item.type = "js">
      <#assign js>${js}, (function(){ var module = new ${item.value}("${itemId}"); module.setOptions({ siteId: "${(page.url.templateArgs.site!"")?url}" }); return module;})()</#assign>
      <button ${attrTitle!""} tabindex="0">${label}</button>
   <#else>
      <#assign js>${js}, new YAHOO.widget.Button("${itemId}")</#assign>
      <#assign attrTarget><#if item.type = "external-link">target="_blank"</#if></#assign>
      <a ${attrTitle!""} ${attrHref} tabindex="0" ${attrTarget!""}>${label}</a>
   </#if>
   </span>
</span>
<#if item.type = "container"><@subMenu prefix item /></#if>
</#macro>

<#--
   Render a sub menu
-->
<#macro subMenu prefix item>
<div id="${id}-${prefix}menu_${item.generatedId}" class="yuimenu">
   <div class="bd">
   <#list item.containers as container>
      <#if (container.permission?length > 0)>
         <#if !(permissions[container.permission]!false)><#break></#if>
      </#if>
      <#if (container.condition?length > 0)>
         <#if !(container.condition?eval)><#return></#if>
      </#if>
      <#if (container.label?length > 0)><h6 <#if (container_index = 0)>class="first-of-type"</#if>>${msg(container.label)}</h6></#if>
      <ul <#if (container_index = 0)>class="first-of-type"</#if>>
      <#list container.items as i><@submenuItem i /></#list>
      </ul>
   </#list>
   </div>
</div>
</#macro>

<#--
   Render a submenu item.
   Knows how to render the user status menuitem.
-->
<#macro submenuItem item>
<#-- Permission check -->
<#if (item.permission?length > 0)><#if !(permissions[item.permission]!false)><#return></#if></#if>
<#-- Condition check -->
<#if (item.condition?length > 0)><#if !(item.condition?eval)><#return></#if></#if>
<#-- Attributes -->
<#assign attrStyle><#if (item.icon?length > 0)>style="background-image: url(${url.context}/res/components/images/header/${item.icon});"</#if></#assign>
<#assign attrTitle><#if (item.description?length > 0)>title="${msg(item.description)}"</#if></#assign>
<#assign attrHref = "href=\"\"">
   <#if (item.type != "js")>
      <#if (item.value?starts_with("http"))><#assign attrHref>href="${item.value}"</#assign>
      <#elseif (item.value?length > 0)><#assign attrHref>templateUri="${item.value}" href=""</#assign>
      </#if>
   </#if>
<#if item.type = "user">
<li class="user-menuitem HEADER-MARKER">
   <#assign avatarNodeRef>${user.properties["avatar"]!"avatar"}</#assign>
   <a class="avatar" ${attrHref} tabindex="0"><img src="${url.context}/proxy/alfresco/slingshot/profile/avatar/${avatarNodeRef?string?replace('://','/')}" alt="avatar" /></a>
   <span class="user-status">
      <textarea id="${id}-statusText" tabindex="0">${userStatus?html}</textarea>
      <div id="${id}-statusTime" class="user-status-time" title="${userStatusTime?html}"></div>
      <div>
         <span id="${id}-submitStatus" class="yui-button yui-push-button">
            <span class="first-child">
               <button type="button" tabindex="0" title="${msg("header.submit-status.description")}"><span>${msg("header.submit-status.label")}</span></button>
            </span>
         </span>
      </div>
   </span>
</li>
<#elseif item.type = "post">
   <#assign post>${id}_post_${item.generatedId}</#assign>
   <script type="text/javascript">//<![CDATA[
   function ${post}() {Alfresco.util.Ajax.jsonRequest({method:"POST",url:Alfresco.constants.URL_PAGECONTEXT+"${item.value}"});}
   //]]></script>
   <li><a ${attrStyle} ${attrTitle} onclick="${post}()" tabindex="0" ${attrTarget!""}>${msg(item.label!"")}</a>
<#else>
   <#assign attrTarget><#if item.type = "external-link">target="_blank"</#if></#assign>
<li><a ${attrStyle} ${attrTitle} ${attrHref} id="${id}-${item.id}" tabindex="0" ${attrTarget!""}>${msg(item.label!"")}</a>
   <#if item.type = "container"><@subMenu item /></#if>
</li>
</#if>
</#macro>