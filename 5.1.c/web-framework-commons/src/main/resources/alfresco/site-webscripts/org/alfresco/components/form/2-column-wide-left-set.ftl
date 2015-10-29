<#list set.children as item>
   <#if item.kind != "set">
      <#if (item_index % 2) == 0>
      <div class="alf-gc"><div class="alf-u first">
      <#else>
      <div class="alf-u">
      </#if>
      <@formLib.renderField field=form.fields[item.id] />
      </div>
      <#if ((item_index % 2) == 1) || !item_has_next></div></#if>
   </#if>
</#list>