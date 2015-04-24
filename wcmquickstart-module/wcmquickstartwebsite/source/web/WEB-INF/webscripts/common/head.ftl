<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="${url.context}/css/index.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" href="${url.context}/css/slimbox2.css" type="text/css" media="screen" />
<link rel="icon" type="image/vnd.microsoft.icon" href="${url.context}/favicon.ico" />
<script type="text/javascript" src="${url.context}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${url.context}/js/jqueryslidemenu.js"></script>
<script type="text/javascript" src="${url.context}/js/jquery-ui-1.7.2.custom.min.js"></script>
<script type="text/javascript" src="${url.context}/js/slimbox2.js"></script>
<#if webSite.logo??>
    <style>
    #logo a {
        background:url(<@makeurl asset=webSite.logo/>) no-repeat;
    }
    </style>
</#if>

${head}
