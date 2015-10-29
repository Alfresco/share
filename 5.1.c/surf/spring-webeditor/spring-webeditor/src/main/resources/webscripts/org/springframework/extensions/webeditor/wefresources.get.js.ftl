<#macro renderResource resource>
<#assign path = resource.path?interpret>
<#escape x as jsonUtils.encodeJSONString(x)>
{
   name: "${resource.name?html}", 
   type: "${resource.type?html}",
   path: "${url.context}<@path />"<#if resource.dependencies?size &gt; 0>,
   requires: [<#list resource.dependencies as dependency>"${dependency.name?html}"<#if dependency_has_next>,</#if></#list>]</#if><#if resource.variableName??>,
   varName: "${resource.variableName?html}"</#if><#if resource.userAgent??>,
   userAgent: "${resource.userAgent?html}"</#if>
}
</#escape>
</#macro>

<#list resources as resource>
<#if resource.path??>
WEF.addResource(<@renderResource resource=resource />);
</#if>
</#list>

if (window.attachEvent)
{
   window.attachEvent("onload",function() {WEF.run("${appName?html}");});
}
else if (window.addEventListener)
{
   window.addEventListener("load",function() {WEF.run("${appName?html}");}, false);
}