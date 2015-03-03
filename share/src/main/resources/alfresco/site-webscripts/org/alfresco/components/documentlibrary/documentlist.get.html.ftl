<#include "include/documentlist.lib.ftl" />
<#include "../form/form.dependencies.inc">

<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/documentlibrary/documentlist.css" group="documentlibrary"/>
   <#include "../preview/include/web-preview-css-dependencies.lib.ftl" />
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/documentlist.js" group="documentlibrary"/>
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/documentlist-view-detailed.js" group="documentlibrary"/>
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/documentlist-view-simple.js" group="documentlibrary"/>
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/documentlist-view-gallery.js" group="documentlibrary"/>
   <@script type="text/javascript" src="${url.context}/res/components/documentlibrary/documentlist-view-filmstrip.js" group="documentlibrary"/>
   <@script type="text/javascript" src="${url.context}/res/yui/slider/slider.js" group="documentlibrary"/>
   <@script type="text/javascript" src="${url.context}/res/yui/carousel/carousel.js" group="documentlibrary"/>
   <#assign dependencyGroup="documentlibrary" />
   <#include "../preview/include/web-preview-js-dependencies.lib.ftl" />
</@>

<@markup id="widgets">
   <@createWidgets group="documentlibrary"/>
</@>

<@uniqueIdDiv>
   <@markup id="html">
      <@documentlistTemplate/>
   </@>
</@>
