<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/data-lists/toolbar.css" group="datalists"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/data-lists/toolbar.js" group="datalists"/>
</@>

<@markup id="widgets">
   <@createWidgets group="datalists"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign id = args.htmlid>
      <div id="${args.htmlid}-body" class="datalist-toolbar toolbar">
         <div id="${args.htmlid}-headerBar" class="header-bar flat-button theme-bg-2">
            <div class="left">
               <div class="new-row">
                  <span id="${id}-newRowButton" class="yui-button yui-push-button">
                     <span class="first-child">
                        <button type="button">${msg('button.new-row')}</button>
                     </span>
                  </span>
               </div>
               <div class="selected-items">
                  <button class="no-access-check" id="${args.htmlid}-selectedItems-button" name="doclist-selectedItems-button">${msg("menu.selected-items")}</button>
                  <div id="${args.htmlid}-selectedItems-menu" class="yuimenu">
                     <div class="bd">
                        <ul>
                        <#list actionSet as action>
                           <li><a type="${action.asset!""}" rel="${action.permission!""}" href="${action.href}"><span class="${action.id}">${msg(action.label)}</span></a></li>
                        </#list>
                           <li><a href="#"><hr /></a></li>
                           <li><a href="#"><span class="onActionDeselectAll">${msg("menu.selected-items.deselect-all")}</span></a></li>
                        </ul>
                     </div>
                  </div>
               </div>
            </div>
            <div class="right" style="display: none;">
               <span id="${id}-printButton" class="yui-button yui-push-button print">
                   <span class="first-child">
                       <button type="button">${msg('button.print')}</button>
                   </span>
               </span>
               <span id="${id}-rssFeedButton" class="yui-button yui-push-button rss-feed">
                   <span class="first-child">
                       <button type="button">${msg('button.rss-feed')}</button>
                   </span>
               </span>
            </div>
         </div>
      </div>
   </@>
</@>