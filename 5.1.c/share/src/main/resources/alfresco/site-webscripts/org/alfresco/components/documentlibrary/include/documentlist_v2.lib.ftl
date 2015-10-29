<#macro viewRenderererJsDeps>
   <#list viewJsDeps as dep>
      <@script type="text/javascript" src="${url.context}/res/${dep}" group="documentlibrary"/>
   </#list>
</#macro>

<#macro viewRenderererCssDeps>
   <#list viewCssDeps as dep>
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/${dep}" group="documentlibrary"/>
   </#list>
</#macro>

<#macro documentlistTemplate>
   <#nested>
   <#assign id=args.htmlid?html>
   <div id="${id}-tb-body" class="toolbar no-check-bg">
      <@markup id="documentListToolbar">
         <div id="${id}-headerBar" class="header-bar flat-button theme-bg-2">
            <@markup id="toolbarLeft">
               <div class="left">
                  <div class="hideable toolbar-hidden DocListTree">
                     <#-- FILE SELECT -->
                     <@markup id="fileSelect">
                        <div class="file-select">
                           <button id="${id}-fileSelect-button" name="doclist-fileSelect-button">${msg("menu.select")}&nbsp;&#9662;</button>
                           <div id="${id}-fileSelect-menu" class="yuimenu">
                              <div class="bd">
                                 <ul>
                                    <li><a href="#"><span class="selectDocuments">${msg("menu.select.documents")}</span></a></li>
                                    <li><a href="#"><span class="selectFolders">${msg("menu.select.folders")}</span></a></li>
                                    <li><a href="#"><span class="selectAll">${msg("menu.select.all")}</span></a></li>
                                    <li><a href="#"><span class="selectInvert">${msg("menu.select.invert")}</span></a></li>
                                    <li><a href="#"><span class="selectNone">${msg("menu.select.none")}</span></a></li>
                                 </ul>
                              </div>
                           </div>
                        </div>
                     </@>
                     <#-- CREATE CONTENT -->
                     <@markup id="createContent">
                     <div class="create-content">
                        <#if createContent?size != 0 || createContentByTemplateEnabled>
                           <span id="${id}-createContent-button" class="yui-button yui-push-button">
                              <span class="first-child">
                                 <button name="createContent">${msg("button.create-content")}&nbsp;&#9662;</button>
                              </span>
                           </span>
                           <div id="${id}-createContent-menu" class="yuimenu">
                              <div class="bd"></div>
                           </div>
                        </#if>
                     </div>
                     </@markup>
         
                  </div>
                  
                  <#-- UPLOAD BUTTON -->
                  <@markup id="uploadButton">
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
                  </@>
                  
                  <#-- CLOUD SYNC BUTTONS -->
                  <@markup id="cloudSyncButtons">
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
                  </@>
                  
                  <#-- SELECTED ITEMS MENU -->
                  <@markup id="selectedItems">
                     <div class="selected-items hideable toolbar-hidden DocListTree DocListFilter TagFilter DocListCategories">
                        <button class="no-access-check" id="${id}-selectedItems-button" name="doclist-selectedItems-button">${msg("menu.selected-items")}&nbsp;&#9662;</button>
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
                  </@>
                  <!-- <div id="${id}-paginator" class="paginator"></div> -->
               </div>
            </@>
            <@markup id="toolbarRight">
               <div class="right">
                  <div class="options-select">
                     <button id="${id}-options-button" name="doclist-options-button">${msg("button.options")}&nbsp;&#9662;</button>
                     <div id="${id}-options-menu" class="yuimenu" style="display:none;">
                        <div class="bd">
                           <ul>
                              <@markup id="documentListViewFolderAction">
                                 <#if preferences.showFolders!true>
                                    <li><a href="#"><span class="hideFolders">${msg("button.folders.hide")}</span></a></li>
                                  <#else>
                                    <li><a href="#"><span class="showFolders">${msg("button.folders.show")}</span></a></li>
                                 </#if>
                              </@>
                              <@markup id="documentListViewNavBarAction">
                                 <#if preferences.hideNavBar!false>
                                    <li><a href="#"><span class="showPath">${msg("button.navbar.show")}</span></a></li>
                                 <#else>
                                    <li><a href="#"><span class="hidePath">${msg("button.navbar.hide")}</span></a></li>
                                 </#if>
                              </@>
                              <@markup id="documentListViewRssAction">
                                 <li class="drop-down-list-break-below"><a href="#"><span class="rss">${msg("link.rss-feed")}</span></a></li>
                              </@>
                              <@markup id="documentListViewFullWindowAction">
                                 <li><a href="#"><span class="fullWindow">${msg("button.fullwindow.enter")}</span></a></li>
                              </@>
                              <@markup id="documentListViewFullScreenAction">
                                 <li class="drop-down-list-break-below"><a href="#"><span class="fullScreen">${msg("button.fullscreen.enter")}</span></a></li>
                              </@>
                              <@markup id="documentListViewRendererSelect">
                                <#if viewRenderers??>
                                   <#list viewRenderers as viewRenderer>
                                      <li class="${viewRenderer.iconClass}<#if !viewRenderer_has_next> drop-down-list-break-below</#if>"><a href="#"><span class="view ${viewRenderer.id}">${msg(viewRenderer.label)}</span></a></li>
                                   </#list>
                                </#if>
                              </@>
                              <@markup id="documentListViewDefaultViewActions">
                                 <li><a href="#"><span class="removeDefaultView">${msg("button.removeDefaultView")}</span></a></li>
                                 <li><a href="#"><span class="setDefaultView">${msg("button.setDefaultView")}</span></a></li>
                              </@>
                           </ul>
                        </div>
                     </div>
                  </div>
                  <@markup id="documentListSortSelect">
                    <div class="sort-field">
                       <span id="${id}-sortField-button" class="yui-button yui-push-button">
                          <span class="first-child">
                             <button name="doclist-sortField-button"></button>
                          </span>
                       </span>
                       <!-- <span class="separator">&nbsp;</span> -->
                       <select id="${id}-sortField-menu">
                       <#list sortOptions as sort>
                          <option value="${(sort.value!"")?html}" <#if sort.direction??>title="${sort.direction?string}"</#if>>${msg(sort.label)}</option>
                       </#list>
                       </select>
                    </div>
                    <div class="sort-direction">
                       <span id="${id}-sortAscending-button" class="yui-button yui-push-button">
                          <span class="first-child">
                             <button name="doclist-sortAscending-button"></button>
                          </span>
                       </span>
                    </div>
                  </@>
                  <@markup id="galleryViewSlider">
                    <div id="${id}-gallery-slider" class="alf-gallery-slider hidden">
                       <div class="alf-gallery-slider-small"><img src="${url.context}/res/components/documentlibrary/images/gallery-size-small-16.png"></div>
                       <div id="${id}-gallery-slider-bg" class="yui-h-slider alf-gallery-slider-bg"> 
                       <div id="${id}-gallery-slider-thumb" class="yui-slider-thumb alf-gallery-slider-thumb"><img src="${url.context}/res/components/documentlibrary/images/thumb-n.png"></div> 
                    </div>
                    <div class="alf-gallery-slider-large"><img src="${url.context}/res/components/documentlibrary/images/gallery-size-large-16.png"></div>
                    </div>
                  </@>
               </div>
            </@>
         </div>
      </@>
      
      <@markup id="navigationBar">
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
      </@>
   
   </div>
   <!--[if IE]>
      <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
   <![endif]-->
   <input id="yui-history-field" type="hidden" />
   <div id="${id}-dl-body" class="doclist no-check-bg">

      <#--
         INFORMATION TEMPLATES
      -->
      <div id="${id}-main-template" class="hidden">
         <div>
         </div>
      </div>
   
      <#-- No items message -->
      <div id="${id}-no-items-template" class="hidden">
         <div class="docListInstructionTitle">${msg("no.items.title")}</div>
      </div>
   
      <#-- Hidden sub-folders message -->
      <div id="${id}-hidden-subfolders-template" class="hidden">
         <div class="docListInstructionTitle">${msg("no.items.title")}</div>
         <div id="${id}-show-folders-template" class="docListInstructionColumn">
            <img class="docListInstructionImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-folder-48.png">
            <a class="docListInstructionTextSmall docListLinkedInstruction"><#-- We don't know the number of hidden subfolders at this point so this needs to be inserted --></a>
         </div>
      </div>
   
      <#-- HTML 5 drag and drop instructions -->
      <div id="${id}-dnd-instructions-template" class="hidden">
         <div id="${id}-dnd-instructions">
            <span class="docListInstructionTitle">${msg("dnd.drop.title")}</span>
            <div>
               <div class="docListInstructionColumn docListInstructionColumnRightBorder">
                  <img class="docListInstructionImage" src="${url.context}/res/components/documentlibrary/images/help-drop-list-target-96.png">
                  <span class="docListInstructionText">${msg("dnd.drop.doclist.description")}</span>
               </div>
               <div class="docListInstructionColumn">
                  <img class="docListInstructionImage" src="${url.context}/res/components/documentlibrary/images/help-drop-folder-target-96.png">
                  <span class="docListInstructionText">${msg("dnd.drop.folder.description")}</span>
               </div>
               <div style="clear:both"></div>
            </div>
         </div>
      </div>
   
      <#-- Standard upload instructions -->
      <div id="${id}-upload-instructions-template" class="hidden">
         <div class="docListInstructionTitle">${msg("standard.upload.title")}</div>
         <div id="${id}-standard-upload-link-template" class="docListInstructionColumn">
            <img class="docListInstructionImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-upload-96.png">
            <span class="docListInstructionText"><a class="docListLinkedInstruction">${msg("standard.upload.description")}</a></span>
         </div>
      </div>
   
      <#-- Other options? -->
      <div id="${id}-other-options-template" class="hidden">
         <div class="docListOtherOptions">${msg("other.options")}</div>
      </div>
   
      <#-- The following DOM structures should be editing with respect to documentlist.js function
           fired by the Doclists "tableMsgShowEvent" as it uses this structure to associate the
           image and anchor with the appropriate actions. NOTE: This is only a template that will
           be cloned, during the cloning the id will be appended with "-instance" to ensure uniqueness
           within the page, this allows us to locate each DOM node individually. -->
   
      <#-- Standard upload (when user has create access) -->
      <div id="${id}-standard-upload-template" class="hidden">
        <div id="${id}-standard-upload-link-template">
           <img class="docListOtherOptionsImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-upload-48.png">
           <span class="docListOtherOptionsText"><a class="docListLinkedInstruction">${msg("dnd.upload.description")}</a></span>
        </div>
      </div>
   
      <#-- New Folder (when user has create access) -->
      <div id="${id}-new-folder-template" class="hidden">
        <div id="${id}-new-folder-link-template">
           <img class="docListOtherOptionsImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-new-folder-48.png">
           <span class="docListOtherOptionsText"><a class="docListLinkedInstruction">${msg("dnd.newfolder.description")}</a></span>
        </div>
      </div>
   
      <#-- Hidden sub-folders message -->
      <div id="${id}-show-folders-template" class="hidden">
         <img class="docListOtherOptionsImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-folder-48.png">
         <span class="docListOtherOptionsText"><a class="docListLinkedInstruction"><#-- We don't know the number of hidden subfolders at this point so this needs to be inserted --></a></span>
      </div>
      <#--
         END OF INFORMATION TEMPLATES
      -->
   
      <#-- Top Bar: Select, Pagination, Sorting & View controls -->
      <div id="${id}-doclistBar" class="yui-gc doclist-bar flat-button no-check-bg"></div>
      <div class="alf-fullscreen-exit-button" class="hidden">
        <span class="yui-button">
            <span class="first-child">
                <button type="button" title="${msg("button.fullscreen.exit")}" id="${id}-fullscreen-exit-button"></button>
            </span>
         </span>
      </div>
   
      <#-- Main Panel: Document List -->
      <@markup id="documentListContainer">
      <div id="${id}-documents" class="documents"></div>
      <div id="${id}-gallery" class="alf-gallery documents"></div>
      <div id="${id}-gallery-empty" class="hidden documents">
         <div class="yui-dt-liner"></div>
      </div>
      <div id="${id}-filmstrip" class="alf-filmstrip alf-gallery documents">
            <div id="${id}-filmstrip-main-content" class="alf-filmstrip-main-content">
                <div id="${id}-filmstrip-carousel"></div>
                <div id="${id}-filmstrip-nav-main-previous" class="alf-filmstrip-nav-button alf-filmstrip-main-nav-button alf-filmstrip-nav-prev">
                    <img src="${page.url.context}/res/components/documentlibrary/images/filmstrip-main-nav-prev.png" />
                </div>
                <div id="${id}-filmstrip-nav-main-next" class="alf-filmstrip-nav-button alf-filmstrip-main-nav-button alf-filmstrip-nav-next">
                    <img src="${page.url.context}/res/components/documentlibrary/images/filmstrip-main-nav-next.png" />
                </div>
            </div>
            <div id="${id}-filmstrip-nav" class="alf-filmstrip-nav">
                <div id="${id}-filmstrip-nav-handle" class="alf-filmstrip-nav-handle"></div>
                <div id="${id}-filmstrip-nav-carousel"></div>
                <div id="${id}-filmstrip-nav-buttons" class="alf-filmstrip-nav-buttons">
                    <div id="${id}-filmstrip-nav-previous" class="alf-filmstrip-nav-button alf-filmstrip-nav-prev">
                        <img src="${page.url.context}/res/components/documentlibrary/images/filmstrip-content-nav-prev.png" />
                    </div>
                    <div id="${id}-filmstrip-nav-next" class="alf-filmstrip-nav-button alf-filmstrip-nav-next">
                        <img src="${page.url.context}/res/components/documentlibrary/images/filmstrip-content-nav-next.png" />
                    </div>
                </div>
            </div>
       </div>
      </@>
   
      <#-- Bottom Bar: Paginator -->
      <div id="${id}-doclistBarBottom" class="yui-gc doclist-bar doclist-bar-bottom flat-button">
         <div class="yui-u first">
            <div class="file-select">&nbsp;</div>
            <div id="${id}-paginatorBottom" class="paginator"></div>
         </div>
      </div>
   
      <#--
         RENDERING TEMPLATES
      -->
      <div style="display: none">
   
         <#-- Action Set "More" template -->
         <div id="${id}-moreActions">
            <div class="internal-show-more" id="onActionShowMore"><a href="#" class="show-more" alt="${msg("actions.more")}" aria-haspopup="true"><span>${msg("actions.more")}</span></a></div>
            <div class="more-actions hidden"></div>
         </div>
   
         <#-- Document List Gallery View Templates-->
         <div id="${id}-gallery-item-template" class="alf-gallery-item hidden">
            <div class="alf-gallery-item-thumbnail">
               <div class="alf-header">
                  <div class="alf-select"></div>
                     <a href="javascript:void(0)" class="alf-show-detail">&nbsp;</a>
               </div>
               <div class="alf-label"></div>
            </div>
            <div class="alf-detail" style="display: none;">
               <div class="bd">
                  <div class="alf-detail-thumbnail"></div>
                  <div class="alf-status"></div>
                  <div class="alf-actions"></div>
                  <div style="clear: both;"></div>
                  <div class="alf-description"></div>
               </div>
            </div>
         </div>
         
         <#-- Document List Filmstrip View Templates -->
           <div id="${id}-filmstrip-nav-item-template" class="alf-filmstrip-nav-item hidden">
              <div class="alf-filmstrip-nav-item-thumbnail">
                 <div class="alf-label"></div>
              </div>
           </div>
           <div id="${id}-filmstrip-item-template" class="alf-gallery-item hidden">
              <div class="alf-gallery-item-thumbnail">
                 <div class="alf-header">
                    <div class="alf-select"></div>
                    <a href="javascript:void(0)" class="alf-show-detail">&nbsp;</a>
                    <div class="alf-label"></div>
                 </div>
              </div>
              <div class="alf-detail">
                  <div class="bd">
                      <div class="alf-status"></div>
                      <div class="alf-actions"></div>
                      <div style="clear: both;"></div>
                      <div class="alf-description"></div>
                  </div>
              </div>
           </div>
   
      </div>
   
   </div>
</#macro>