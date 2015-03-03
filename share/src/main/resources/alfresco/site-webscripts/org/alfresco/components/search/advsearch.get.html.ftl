<@markup id="css" >
   <#-- CSS Dependencies -->
   <#include "../form/form.css.ftl"/>
   <@link href="${url.context}/res/components/search/search.css" group="search"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <#include "../form/form.js.ftl"/>
   <@script src="${url.context}/res/components/form/date-range.js" group="search"/>
   <@script src="${url.context}/res/components/form/number-range.js" group="search"/>
   <@script src="${url.context}/res/components/search/advsearch.js" group="search"/>
   
</@>

<@markup id="widgets">
   <@createWidgets group="search"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="search">
         <div class="yui-gc form-row">
            <div class="yui-u first">
               <span class="lookfor">${msg("label.lookfor")}:</span>
               
               <#-- component to show list of forms, displays current form -->
               <span class="selected-form-button">
                  <span id="${el}-selected-form-button" class="yui-button yui-menu-button">
                     <span class="first-child">
                        <button type="button" tabindex="0"></button>
                     </span>
                  </span>
               </span>
               <#-- menu list of available forms -->
               <div id="${el}-selected-form-list" class="yuimenu" style="visibility:hidden">
                  <div class="bd">
                     <ul>
                        <#list searchForms as f>
                        <li>
                           <span class="form-type-name" tabindex="0">${f.label?html}</span>
                           <span class="form-type-description">${f.description?html}</span>
                        </li>
                        </#list>
                     </ul>
                  </div>
               </div>
            </div>
            
            <#-- search button -->
            <div class="yui-u align-right">
               <span id="${el}-search-button-1" class="yui-button yui-push-button search-icon">
                  <span class="first-child">
                     <button type="button">${msg('button.search')}</button>
                  </span>
               </span>
            </div>
         </div>
         
         <#-- keywords entry box - DIV structure mirrors a generated Form to collect the correct styles -->
         <div class="forms-container keywords-box">
            <div class="share-form">
               <div class="form-container">
                  <div class="form-fields">
                     <div class="set">
                        <div>${msg("label.keywords")}:</div>
                        <input type="text" class="terms" name="${el}-search-text" id="${el}-search-text" value="${(page.url.args["st"]!"")?html}" maxlength="1024" />
                     </div>
                  </div>
               </div>
            </div>
         </div>
         
         <#-- container for forms retrieved via ajax -->
         <div id="${el}-forms" class="forms-container form-fields"></div>
         
         <div class="yui-gc form-row">
            <div class="yui-u first"></div>
            <#-- search button -->
            <div class="yui-u align-right">
               <span id="${el}-search-button-2" class="yui-button yui-push-button search-icon">
                  <span class="first-child">
                     <button type="button">${msg('button.search')}</button>
                  </span>
               </span>
            </div>
         </div>
      </div>
   </@>
</@>