<#if surfBugEnabled = "true">
    <div id="${htmlid!""}">
        <@componentInclude/>
        <div id="${htmlid}-SurfBug-Popup" class="surfbug_popup">
            <div class="surfbug_popup_close" onClick="SurfBug.hidePopup(event, this);">X</div>
            <div class="surfbug_data">
                <table>
                    <tr>
                        <td><span>Page ID: </span></td>
                        <td>${surfBugPageId!""}</td>
                    </tr>
                    <tr>
                        <td><span>Template ID: </span></td>
                        <td>${surfBugTemplateId!""}</td>
                    </tr>
                    <tr>
                        <td><span>Template Type: </span></td>
                        <td>${surfBugTemplatePath!""}</td>
                    </tr>
                    <tr>
                        <td><span>Component ID: </span></td>
                        <td>${subComponent_parentId!""}</td>
                    </tr>
                    <tr>
                        <td><span>Component Definition Location:</span></td>
                        <td>${storagePath}</td>
                     </tr>
                     <tr><td colspan="2">&nbsp;</td></tr>
                     <tr><td colspan="2"><span>Component Details:</span></td></tr>
                     <#list componentDebug?keys as key>
                        <tr>
                            <td class="surfbug_property_key">${key}: </td>
                            <td>${componentDebug[key]}</td>
                        </tr>
                     </#list>
                     <tr><td class="surfbug_property_key" colspan="2">Custom Properties:</td></tr>
                     <#list componentCustomPropsDebug?keys as key>
                         <tr>
                             <td class="surfbug_property_key">${key}: </td>
                             <td>${componentCustomPropsDebug[key]}</td>
                         </tr>
                     </#list>
                     <tr><td colspan="2">&nbsp;</td></tr>
                     <tr><td colspan="2"><span>Sub-Component Details:</span></td></tr>
                     <tr>
                         <td class="surfbug_property_key">ID:</td>
                         <td>${subComponent_id!""}</td>
                     </tr>
                     <tr>
                         <td class="surfbug_property_key">Contributing Paths:</td>
                         <td>${subComponent_paths!""}</td>
                     </tr>
                     <tr>
                         <td class="surfbug_property_key">Index:</td>
                         <td>${subComponent_index!""}</td>
                     </tr>
                     <tr>
                         <td class="surfbug_property_key">Processor:</td>
                         <td>${subComponent_processor!""}</td>
                     </tr>
                     <#if subCompRenderData??>
                         <#list subCompRenderData?keys as key>
                             <tr>
                                <td class="surfbug_property_key">${key}:</td>
                                <td>${subCompRenderData[key]}</td>
                             </tr>
                         </#list>
                     </#if>
                     <#if subComponent_resolvedWSStorePath??>
                        <tr> 
                           <td class="surfbug_property_key"><span>WebScript Location: </span></td>
                           <td>${subComponent_resolvedWSStorePath}/${subComponent_resolvedWSDescPath}</td>
                        </tr>
                        <tr>
                           <td class="surfbug_property_key"><span>WebScript Details: </span></td> 
                           <td><a target="_blank" href="${subComponent_resolvedWSBasePath}">Click here to open in new window/tab</a></td>
                        </tr>
                     </#if>
                     <#if subCompEvaluatedProps??>
                         <tr><td class="surfbug_property_key" colspan="2">Evaluated Properties:</td></tr>
                         <#list subCompEvaluatedProps?keys as key>
                             <tr>
                                <td class="surfbug_property_key">${key}:</td>
                                <td>${subCompEvaluatedProps[key]}</td>
                             </tr>
                         </#list>
                     </#if>
                     <#if renderedExtensibilityArtefacts??>
                         <tr><td colspan="2">&nbsp;</td></tr>
                         <tr><td colspan="2"><span>Extensibility Directives:</span></td></tr>
                         <#list renderedExtensibilityArtefacts?keys as directive>
                             <tr>
                                 <td colspan="2">${directive}</td>
                             </tr>
                             <#list renderedExtensibilityArtefacts[directive]?keys as id>
                                <tr>
                                   <td class="surfbug_property_key">"${renderedExtensibilityArtefacts[directive][id]["id"]}"</td>
                                   <td>${renderedExtensibilityArtefacts[directive][id]["sources"]}</td>
                                </tr>
                             </#list>
                         </#list>
                     </#if>
                </table>
            </div>
        </div>
        <script type="text/javascript">
            SurfBug.createHighlight("${htmlid!""}");
        </script>
    </div>
<#else>
    <div id="${htmlid!""}">
        <@componentInclude/>
    </div>
</#if>

