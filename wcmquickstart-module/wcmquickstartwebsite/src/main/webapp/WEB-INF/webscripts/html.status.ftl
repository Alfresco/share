"'></a></script>
<!-- above dodgy HTML is required to end any open tags, script etc. before further script injection -->

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
          <div style="padding: 2px">${msg('error.component')}: ${url.service?html}.</div>
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
    <div class="theme-color-2" style="padding: 16px; margin: 8px; border: 1px dashed #D7D7D7;">
       <div class="theme-color-4" >
            ${msg('error.friendly')}
       </div>
    </div>
</#if>

