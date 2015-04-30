<#if result>
Success
isMultiPart = ${isMultiPart?string}
arg.name = ${args_name}
name = ${form_name}
title = ${form_title}
filename = ${form_filename}
number of form fields = ${formFieldLength}
<#else>
Failed
</#if>