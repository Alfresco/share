<#-- This is an example of how to request a CSS dependency. The resource request will
     be made in the <head> element of the page (because this is where the <@outputCSS>
     directive has been placed. A checksum suffix generated from the file contents 
     will be appended to the request so that the browser can cache it indefinitely.
     The group attribute is used when dependencies are aggregated together into a single
     request. -->
<@link href="${url.context}/res/css/body.css" group="default"/>

<div class="body">
    <!-- The body class defined in the "css/body.css" file sets a background image to 
         provide an example of how images referenced from within CSS files are encoded
         when the "generate-css-data-images" configuration option is enabled (see the
         "WEB-INF/surf.xml" file). -->
</div>