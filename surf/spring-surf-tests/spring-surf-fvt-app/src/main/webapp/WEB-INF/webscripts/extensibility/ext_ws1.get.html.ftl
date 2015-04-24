<#-- The "msgKeys" is set in the model by the JS controller which uses the Component properties to set the keys -->
<@markup id="base-content">
   <#list msgKeys as key>
      <content>${msg(key)}</content>
   </#list>
</@>