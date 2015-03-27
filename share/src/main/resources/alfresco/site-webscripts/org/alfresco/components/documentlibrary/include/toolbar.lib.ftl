<#include "../../../include/alfresco-macros.lib.ftl" />
<#macro toolbarTemplate>
<#nested>
<#assign id=args.htmlid?html>
<div id="${id}-tb-body" class="toolbar no-check-bg">

   <div id="${id}-headerBar" class="header-bar flat-button theme-bg-2">
      <div class="left">
         <div class="hideable toolbar-hidden DocListTree">

            <#-- CREATE CONTENT -->
            <@markup id="createContent">
            <div class="create-content">
               <#if createContent?size != 0 || createContentByTemplateEnabled>
                  <span id="${id}-createContent-button" class="yui-button yui-push-button">
                     <span class="first-child">
                        <button name="createContent">${msg("button.create-content")}</button>
                     </span>
                  </span>
                  <div id="${id}-createContent-menu" class="yuimenu">
                     <div class="bd"></div>
                  </div>
               </#if>
            </div>
            </@markup>

         </div>
         <div class="hideable toolbar-hidden DocListTree">
            <div class="new-folder">
               <span id="${id}-newFolder-button" class="yui-button yui-push-button">
                  <span class="first-child">
                     <button name="newFolder">${msg("button.new-folder")}</button>
                  </span>
               </span>
            </div>
         </div>
         <#if uploadable>
         <div class="hideable toolbar-hidden DocListTree">
            <div class="file-upload">
               <span id="${id}-fileUpload-button" class="yui-button yui-push-button">
                  <span class="first-child">
                     <button name="fileUpload">${msg("button.upload")}</button>
                  </span>
               </span>
            </div>
         </div>
         </#if>
         <div class="hideable toolbar-hidden DocListTree">
            <div class="sync-to-cloud">
               <span id="${id}-syncToCloud-button" class="yui-button yui-push-button hidden">
                  <span class="first-child">
                     <button name="syncToCloud">${msg("button.sync-to-cloud")}</button>
                  </span>
               </span>
            </div>
         </div>
         <div class="hideable toolbar-hidden DocListTree">
            <div class="unsync-from-cloud">
               <span id="${id}-unsyncFromCloud-button" class="yui-button yui-push-button hidden">
                  <span class="first-child">
                     <button name="unsyncFromCloud">${msg("button.unsync-from-cloud")}</button>
                  </span>
               </span>
            </div>
         </div>
         <div class="selected-items hideable toolbar-hidden DocListTree DocListFilter TagFilter DocListCategories">
            <button class="no-access-check" id="${id}-selectedItems-button" name="doclist-selectedItems-button">${msg("menu.selected-items")}</button>
            <div id="${id}-selectedItems-menu" class="yuimenu">
               <div class="bd">
                  <ul>
                  <#list actionSet as action>
                     <li><a type="${action.asset!""}" rel="${action.permission!""}" href="${action.href}" data-has-aspects="${action.hasAspect}" data-not-aspects="${action.notAspect}"><span class="${action.id}">${msg(action.label)}</span></a></li>
                  </#list>
                     <li><a href="#"><hr /></a></li>
                     <li><a href="#"><span class="onActionDeselectAll">${msg("menu.selected-items.deselect-all")}</span></a></li>
                  </ul>
               </div>
            </div>
         </div>
      </div>
      <div class="right">
         <div class="hide-navbar">
            <span id="${id}-hideNavBar-button" class="yui-button yui-checkbox-button">
               <span class="first-child">
                  <button name="hideNavBar"></button>
               </span>
            </span>
         </div>
         <div class="separator">&nbsp;</div>
         <div class="rss-feed">
            <span id="${id}-rssFeed-button" class="yui-button yui-link-button">
               <span class="first-child">
                  <a tabindex="0" href="#"></a>
               </span>
            </span>
         </div>
      </div>
   </div>

   <div id="${id}-navBar" class="nav-bar flat-button theme-bg-2">
      <div class="hideable toolbar-hidden DocListTree DocListCategories">
         <div class="folder-up">
            <span id="${id}-folderUp-button" class="yui-button yui-push-button">
               <span class="first-child">
                  <button class="no-access-check" name="folderUp"></button>
               </span>
            </span>
         </div>
         <div class="separator">&nbsp;</div>
      </div>
      <div id="${id}-breadcrumb" class="breadcrumb hideable toolbar-hidden DocListTree DocListCategories"></div>
      <div id="${id}-description" class="description hideable toolbar-hidden DocListFilter TagFilter"></div>
   </div>

</div>
</#macro>