<#-- This is an example of how to request a CSS dependency. The resource request will
     be made in the <head> element of the page (because this is where the <@outputCSS>
     directive has been placed. A checksum suffix generated from the file contents 
     will be appended to the request so that the browser can cache it indefinitely.
     The group attribute is used when dependencies are aggregated together into a single
     request. -->
<@link href="${url.context}/res/css/header.css" group="default"/>

<div class="header">
    <div class="logo">
        <#-- This shows an example of using the /res/ path to access resources from within
             the web-application -->
        <img src="${url.context}/res/images/AlfrescoLogo200.jpg"/>
    </div>
    <div class="welcome">
        <#if displayWelcome == true>
            <#-- This is an example of accessing an i18n label from the associated
                 header.get.properties file. -->
            <span>${msg("welcome.message", name)}</span>
        <#else>
            <span>${msg("welcome.instructions")}</span>
        </#if>
    </div>
</div>