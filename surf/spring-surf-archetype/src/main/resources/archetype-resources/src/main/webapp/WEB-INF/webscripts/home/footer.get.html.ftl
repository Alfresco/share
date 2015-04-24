<#-- This is an example of how to request a CSS dependency. The resource request will
     be made in the <head> element of the page (because this is where the <@outputCSS>
     directive has been placed. A checksum suffix generated from the file contents 
     will be appended to the request so that the browser can cache it indefinitely.
     The group attribute is used when dependencies are aggregated together into a single
     request. -->
<@link href="${url.context}/res/css/footer.css" group="default"/>

<div class="footer">
    <#-- Here we're using a Component scoped property that has been set in the "WEB-INF/surf-config/pages/home.xml" configuration
         file to specify which footer image to use -->
    <img src="${url.context}/res/images/${args["footerImage"]}"/>
</div>