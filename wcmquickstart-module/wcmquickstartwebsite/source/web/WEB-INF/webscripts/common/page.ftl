<#macro templateBody>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <#include "/common/head.ftl"/> 
    <#include "/common/title.ftl"/>
</head>
<body>
<div id="body-wrapper">
    <div class="clearfix">
        <div id="logo"> 
            <a href="${url.context}/">${(webSite.description!'')?html}</a>
            <#if webSite.title??><p class="logo-desc">${webSite.title?html}</p></#if>
        </div>
        <div class="link-menu">
            <ul>
                <!--<li><a href="#">${msg('links.accessibility')}</a></li>-->
                <li><a href="${url.context}${rootSection.path}contact/contact.html">${msg('links.contact')}</a></li>
            </ul>
        </div>
        <@region id="top-right" scope="template"/>  
    </div>
  
    <div class="main-menu-wrapper">
        <@region id="menu" scope="global"/>
    </div>
  
    <#nested>
     
    <div id="footer">
        <div class="copyright"><a href="http://www.alfresco.com">Alfresco.com</a></div>
    </div>
</div>

<#include "/common/init.ftl"/> 
</body>
</html>
</#macro>
