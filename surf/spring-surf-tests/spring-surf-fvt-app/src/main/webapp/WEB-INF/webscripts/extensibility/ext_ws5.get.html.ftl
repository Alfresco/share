<#-- The "msgKeys" is set in the model by the JS controller which uses the Component properties to set the keys -->
<#list msgKeys as key>
   <content>${msg(key)}</content>
</#list>