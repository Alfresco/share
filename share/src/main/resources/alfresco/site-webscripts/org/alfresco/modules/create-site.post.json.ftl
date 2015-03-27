{
   "success": ${success?string}
<#if code?exists>, "code": ${code}</#if>
<#if error?exists>, "error": "${error}"</#if>
}