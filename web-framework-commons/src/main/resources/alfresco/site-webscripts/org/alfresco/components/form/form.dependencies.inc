<#-- This file should be included by all WebScripts that make use of the forms runtime. It ensures that all the
     required JavaScript and CSS dependencies are available. -->

<#include "../component.head.inc">

<!-- Form Assets -->
<@link href="${page.url.context}/res/yui/calendar/assets/calendar.css" group="forms"/>
<@link href="${page.url.context}/res/components/object-finder/object-finder.css" group="forms"/>
<@link href="${page.url.context}/res/components/form/form.css" group="forms"/>

<#if config.global.forms?exists && config.global.forms.dependencies?exists && config.global.forms.dependencies.css?exists>
   <#list config.global.forms.dependencies.css as cssFile>
      <#-- DD: Refactor notes - there could be a potential problem here, as cssFile may start with a slash. -->
      <@link href="${page.url.context}/res${cssFile}" group="forms"/>
   </#list>
</#if>

<@script src="${page.url.context}/res/components/form/form.js" group="forms"/>
<@script src="${page.url.context}/res/components/form/date.js" group="forms"/>
<@script src="${page.url.context}/res/components/form/date-picker.js" group="forms"/>
<@script src="${page.url.context}/res/components/form/period.js" group="forms"/>
<@script src="${page.url.context}/res/components/object-finder/object-finder.js" group="forms"/>
<@script src="${url.context}/res/components/common/common-component-style-filter-chain.js" group="form"/>
<@script src="${page.url.context}/res/yui/calendar/calendar.js" group="forms"/>
<!-- NOTE: Use preminified version of tinymce/tinymce.js -->
<script type="text/javascript" src="${page.url.context}/res/modules/editors/tinymce/tinymce.min.js"></script>
<@script src="${page.url.context}/res/modules/editors/tiny_mce.js" group="forms"/>
<@script src="${page.url.context}/res/components/form/rich-text.js" group="forms"/>
<@script src="${page.url.context}/res/components/form/content.js" group="forms"/>
<@script src="${page.url.context}/res/components/form/workflow/transitions.js" group="forms"/>
<@script src="${page.url.context}/res/components/form/workflow/activiti-transitions.js" group="forms"/>
<@script src="${page.url.context}/res/components/form/jmx/operations.js" group="forms"/>

<#if config.global.forms?exists && config.global.forms.dependencies?exists && config.global.forms.dependencies.js?exists>
   <#list config.global.forms.dependencies.js as jsFile>
      <#-- DD: Refactor notes - there could be a potential problem here, as jsFile may start with a slash. -->
      <@script src="${page.url.context}/res${jsFile}" group="forms"/>
   </#list>
</#if>
