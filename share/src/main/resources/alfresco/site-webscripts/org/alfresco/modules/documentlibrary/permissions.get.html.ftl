<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.ComponentManager.get("${el}").setOptions(
   {
      roles:
      {
         <#list siteRoles as siteRole>"${siteRole}": true<#if siteRole_has_next>,</#if></#list>
      },
      isSitePublic: ${isSitePublic}
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-dialog" class="permissions">
   <div id="${el}-title" class="hd"></div>
   <div class="bd">
      <p/>
      <div class="yui-g">
         <h2>${msg("header.manage")}</h2>
      </div>
      <div class="groups">
<#list groupNames as group>
         <div class="yui-gd">
            <div class="yui-u first right"><label>${msg("group." + group)} ${msg("label.have")}</label></div>
            <div class="yui-u flat-button">
               <button id="${el}-${group?lower_case}" value="${permissionGroups[group_index]}" class="site-group"></button>
               <select id="${el}-${group?lower_case}-select">
   <#list siteRoles as siteRole>
                  <option value="${siteRole}">${msg("role." + siteRole)}</option>
   </#list>
               </select>
            </div>
         </div>
</#list>
      </div>
      <div class="actions">
         <div class="yui-gd">
            <div class="yui-u first reset-btn">
               <button id="${el}-reset-all">${msg("label.reset-all")}</button>
            </div>
            <div class="yui-u">
               <label>${msg("label.mangerdefaults")}</label>
            </div>
         </div>
      </div>
      <p/>
      <div class="bdft">
         <input type="button" id="${el}-ok" value="${msg("button.save")}" tabindex="0" />
         <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
      </div>
   </div>
</div>