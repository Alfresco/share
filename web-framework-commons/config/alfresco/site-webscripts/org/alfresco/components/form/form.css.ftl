<#-- CSS Dependencies -->
<@link href="${url.context}/res/yui/calendar/assets/calendar.css" group="form"/>
<@link href="${url.context}/res/components/object-finder/object-finder.css" group="form"/>
<@link href="${url.context}/res/components/form/form.css" group="form"/> 

<#if config.global.forms?exists && config.global.forms.dependencies?exists && config.global.forms.dependencies.css?exists>
   <#list config.global.forms.dependencies.css as cssFile>
      <@link href="${url.context}/res${cssFile}" group="form"/>
   </#list>
</#if>