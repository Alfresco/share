<#macro documentlistTemplate>
<!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />
<#nested>
<#assign id = args.htmlid?html>
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
   <div id="${id}-doclistBar" class="yui-gc doclist-bar flat-button no-check-bg">
      <div class="yui-u first">
         <div class="file-select">
            <button id="${id}-fileSelect-button" name="doclist-fileSelect-button">${msg("menu.select")}</button>
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
         <div id="${id}-paginator" class="paginator"></div>
      </div>
      <div class="yui-u align-right">
         <@markup id="documentListViewRendererSelect">
           <div id="${id}-simpleDetailed" class="simple-detailed yui-buttongroup inline">
              <#-- Don't insert linefeeds between these <input> tags -->
              <#if viewRendererNames??><#list viewRendererNames as viewRendererName><input id="${id}-${viewRendererName}View" type="radio" name="simpleDetailed" title="${msg("button.view." + viewRendererName)}" value="" /></#list></#if>
           </div>
         </@>
         <@markup id="documentListShowFolders">
           <div class="show-folders">
              <span id="${id}-showFolders-button" class="yui-button yui-checkbox-button">
                 <span class="first-child">
                    <button name="doclist-showFolders-button"></button>
                 </span>
              </span>
              <span class="separator">&nbsp;</span>
           </div>
         </@>
         <@markup id="documentListSortSelect">
           <div class="sort-field">
              <span id="${id}-sortField-button" class="yui-button yui-push-button">
                 <span class="first-child">
                    <button name="doclist-sortField-button"></button>
                 </span>
              </span>
              <span class="separator">&nbsp;</span>
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
   </div>

   <#-- Main Panel: Document List -->
   <@markup id="documentListContainer">
   <div id="${id}-documents" class="documents"></div>
   <div id="${id}-gallery" class="alf-gallery documents"></div>
   <div id="${id}-gallery-empty" class="hidden">
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
         <div class="internal-show-more" id="onActionShowMore"><a href="#" class="show-more" alt="${msg("actions.more")}"><span>${msg("actions.more")}</span></a></div>
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
