<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/wcmqs/css/index.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" href="/wcmqs/css/slimbox2.css" type="text/css" media="screen" />
<script type="text/javascript" src="/wcmqs/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="/wcmqs/js/jqueryslidemenu.js"></script>
<script type="text/javascript" src="/wcmqs/js/jquery-ui-1.7.2.custom.min.js"></script>
<script type="text/javascript" src="/wcmqs/js/slimbox2.js"></script>

<style>
#logo a {
   background:url("${url.context}/images/AlfrescoLogo200.jpg") no-repeat;
}
</style>

<title>Error</title>
</head>
<body>
<div id="body-wrapper">
    <div class="clearfix">
        <div id="logo"> 
            <a href="${url.context}/">This is a demonstration Alfresco WCM backed Internet Web Site</a>
            <p class="logo-desc">Alfresco WCM Quick Start</p>
        </div>
        <div class="link-menu">
            <ul>                
            </ul>
        </div>
    </div>
  
    <div class="main-menu-wrapper">

        <div id="myslidemenu" class="jqueryslidemenu">
            <ul class="primary-menu">
                <li><a href="${url.context}/" accesskey="1">Home</a></li>
            <ul>
        </div>    
    </div>

    <#if editorialSite!'false' == "true">
        <script type="text/javascript">
        var _errorhidden = true;
        function _toggleErrorDetails()
        {
           if (_errorhidden)
           {
              document.getElementById('_errorDetails').style.display = 'block';
              _errorhidden = false;
           }
           else
           {
              document.getElementById('_errorDetails').style.display = 'none';
              _errorhidden = true;
           }
        }
        </script>
        
        <div class="theme-color-2" style="padding: 8px; margin: 8px; border: 1px dashed #D7D7D7;">
           <div style="font-weight: bold; font-size: 116%">
              <div style="padding: 2px">${msg('error.component')}: ${url.service}.</div>
              <div style="padding: 2px">${msg('error.status')} ${status.code} - ${status.codeName}.</div>
           </div>
           <div class="theme-color-4" style="padding-top:8px;">
              <div style="padding: 2px"><b>${msg('error.code')}:</b> ${status.code} - ${status.codeDescription}</div>
              <div style="padding: 2px"><b>${msg('error.message')}:</b> <#if status.message??>${status.message?html}<#else><i>&lt;${msg('error.message.none')}&gt;</i></#if></div>
              <div style="padding: 2px"><b>${msg('error.server')}:</b> Alfresco ${server.edition?html} v${server.version?html} schema ${server.schema?html}</div>
              <div style="padding: 2px"><b>${msg('error.time')}:</b> ${date?datetime}</div>
              <#if status.exception?exists>
              <div style="padding: 2px; cursor: pointer" onclick="_toggleErrorDetails();"><b><i>${msg('error.expand')}</i></b></div>
              <div id="_errorDetails" style="display: none">
                 <div style="padding: 2px"><@recursestack status.exception/></div>
              </div>
              </#if>
           </div>
        </div>
        
        <#macro recursestack exception>
           <#if exception.cause?exists>
              <@recursestack exception=exception.cause/>
           </#if>
           <#if exception.message?? && exception.message?is_string>
              <div style="padding: 2px"><b>${msg('error.exception')}:</b> ${exception.class.name} - ${exception.message?html}</div>
              <#if exception.cause?exists == false>
                 <#list exception.stackTrace as element>
                    <div>${element?html}</div>
                 </#list>
              <#else>
                 <div>${exception.stackTrace[0]?html}</div>
              </#if>
           </#if>
        </#macro>
    <#else>
        <div class="error">
            <h2>${msg('title.error')}</h2>
            <br/>
            ${msg('error.friendly')}
        </div>
    </#if>
     
    <div id="footer">
        <div class="copyright"><a href="http://www.alfresco.com">Alfresco.com</a></div>
    </div>
</div>
</body>
</html>
