<#if jsonModel??>
   <@processJsonModel group="share"/>
<#else>
   ${msg(jsonModelError, jsonModelErrorArgs!"")?html}
</#if>
