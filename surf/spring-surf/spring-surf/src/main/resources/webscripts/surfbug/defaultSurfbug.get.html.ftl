<div>
   <div id="${htmlid}-SurfBug" class="surfbug_highlight" onClick="SurfBug.showPopup(event, this);">
      <div id="${htmlid}-SurfBug-Popup" class="surfbug_popup">
          <div class="surfbug_popup_close" onClick="SurfBug.hidePopup(event, this);">X</div>
          <div class="surfbug_data">
              <table>
                 <tr>
                    <td><span>Component Definition Location: </span></td>
                    <td>${storagePath}</td>
                 </tr>
                 <#if resolvedWSStorePath??>
                    <tr> 
                       <td><span>WebScript Location: </span></td>
                       <td>${resolvedWSStorePath}/${resolvedWSDescPath}</td>
                    </tr>
                    <tr>
                       <td><span>WebScript Details: </span></td> 
                       <td><a target="_blank" href="${resolvedWSBasePath}">Click here to open in new window/tab</a></td>
                    </tr>
                 </#if>
                 <tr><td colspan="2">&nbsp;</td></tr>
                 <tr><td colspan="2"><span>Component Details:</span></td></tr>
                 <#list componentDebug?keys as key>
                    <tr>
                        <td>${key}: </td>
                        <td>${componentDebug[key]}</td>
                    </tr>
                 </#list>
                 <tr><td colspan="2">&nbsp;</td></tr>
                 <tr><td colspan="2"><span>Component Custom Properties:</span></td></tr>
                 <#list componentCustomPropsDebug?keys as key>
                    <tr>
                        <td>${key}: </td>
                        <td>${componentCustomPropsDebug[key]}</td>
                    </tr>
                 </#list>
              </table>
          </div>
       </div>
       <@surfbugInclude/>
   </div>
</div>