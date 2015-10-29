<#assign templateId = context.properties["error-templateId"]>
<#assign regionId = context.properties["error-regionId"]>
<#assign regionScopeId = context.properties["error-regionScopeId"]>
<#assign regionSourceId = context.properties["error-regionSourceId"]>

<div width="100%">

<font color="#cc0000">

A problem has occurred.
<br/>
This region could not be rendered:
<br/>
<br/>
templateId: ${templateId}
<br/>
regionId: ${regionId}
<br/>
regionScopeId: ${regionScopeId}
<br/>
regionSourceId: ${regionSourceId}
<br/>
<br/>
Please notify your system administrator.
</b>
</font>

</div>