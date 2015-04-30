<#list set.children as item>
   <#if item.kind != "set">
      <#if (item_index % 3) == 0>
      <div class="yui-gb"><div class="yui-u first">
      <#else>
      <div class="yui-u">
      </#if>
      <@formLib.renderField field=form.fields[item.id] />
      </div>
      <#if ((item_index % 3) == 2) || !item_has_next></div></#if>
   </#if>
</#list>


