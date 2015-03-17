<#escape x as jsonUtils.encodeJSONString(x)>
{
   <#assign mimes=mimetypes.displaysByMimetype>
   "mimetypes":
   {
      <#list mimes?keys as mime>
      "${mime}": "${mimes[mime]}"<#if mime_has_next>,</#if>
      </#list>
   }
}
</#escape>