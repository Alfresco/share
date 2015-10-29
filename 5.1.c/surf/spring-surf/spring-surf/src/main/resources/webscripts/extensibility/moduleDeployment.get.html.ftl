<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>Module Deployment Status</title> 
      <link rel="stylesheet" href="${resourceurl('/css/webscripts.css', true)}" type="text/css" />
      <link rel="stylesheet" href="${resourceurl('/css/moduleDeployment.css', true)}" type="text/css" />
      <script src="${resourceurl('/js/yui/yahoo/yahoo-min.js', true)}"></script>
      <script src="${resourceurl('/js/yui/json/json-min.js', true)}"></script>
      <script src="${resourceurl('/js/moduleDeployment.js', true)}" type="text/javascript"></script>
   </head>
   <body>
       <div>
          <table>
             <tr>
                <td><img src="${resourceurl('/images/logo/AlfrescoLogo32.png', true)}" alt="Alfresco" /></td>
                <td><span class="title">Module Deployment</span></td>
             </tr>
          </table>
          
          <form action="${url.serviceContext}${url.match}" method="post" onSubmit="selectAll(this.deployedModules);"> 
              <table>
                 <tr align="left">
                    <td colspan="4"><b>Current Status (Last update: ${lastCacheUpdate})</b></td>
                 </tr>
                 <#if errors??>
                     <tr align="left">
                        <td colspan="4">
                           <p class="errors">
                              <#list errors as error>
                                 ${error}<br>
                              </#list>
                           </p>
                        </td>
                     </tr>
                 </#if>
                 <tr>
                    <td colspan="2">Available Modules</td>
                    <td colspan="2">Deployed Modules (in order processed)</td>
                    <td rowspan="3" id="moduleInfo" class="hidden alignTop">
                       <div>
                          <span class="title">Selected module: </span><span id="selectedModule"></span>
                       </div>
                       <div>
                          <span>Evaluator:</span>
                            <div class="scrollableContainer">
                              <select id="evaluator" name="evaluator" onChange="showRequiredEvaluatorProps(this.value);">
                                 <#list evaluators as ev>
                                    <option value='${ev}'><script type="text/javascript">getEvaluatorId(${ev});</script></option>
                                 </#list>
                              </select>
                            </div>
                       </div>
                       <div>
                          <span>Evaluator Properties: </span>
                       </div>
                       <div id="evaluatorPropertyOverrides">
                       </div>
                       <div>
                          <input class="addPropertyButton" type="button" value="Add" onClick="addProperty('', '');"/>
                       </div>
                       <div class="moduleToolbar">
                          <input type="button" value="Defaults" onClick="resetProperties();"/>
                          <input type="button" value="Update" onClick="saveModule();"/>
                       </div>
                    </td>
                 </tr>
                 <tr>
                    <td>
                      <div class="scrollableContainer">
                         <select name="undeployedModules" size="10">
                            <#list undeployedModules as mod>
                               <option value='${mod}' onClick="showSelectedEvaluator(this, false);"><script type="text/javascript">getModuleId(${mod});</script></option>
                            </#list>
                         </select>
                      </div>
                    </td>
                    <td>
                       <input type="button" value="Add" onClick="deploymentAction(this.form.undeployedModules, this.form.deployedModules);"/><br/>
                       <input type="button" value="Remove" onClick="deploymentAction(this.form.deployedModules, this.form.undeployedModules);"/>
                    </td>
                    <td>
                      <div class="scrollableContainer">
                         <select name="deployedModules" size="10">
                            <#list deployedModules as mod>
                               <option value='${mod}' onClick="showSelectedEvaluator(this, false);"><script type="text/javascript">getModuleId(${mod});</script></option>
                            </#list>
                         </select>
                      </div>
                    </td>
                    <td>
                       <input type="button" value="Up" onClick="moveUp(this.form.deployedModules);"><br/>
                       <input type="button" value="Down" onClick="moveDown(this.form.deployedModules);"/>
                    </td>
                 </tr>
                 <tr>
                    <td class="alignBottom" colspan="4">
                       <input type="submit" value="Apply Changes"/>
                    </td>
                </tr>
             </table>
         </form>
      </div>
   </body>
</html>