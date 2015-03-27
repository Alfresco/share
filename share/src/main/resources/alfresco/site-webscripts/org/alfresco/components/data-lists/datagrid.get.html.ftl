<@markup id="css" >
   <#-- CSS Dependencies -->
   <#include "../form/form.css.ftl"/>
   <@link href="${url.context}/res/components/data-lists/datagrid.css" group="datalists" />
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <#include "../form/form.js.ftl"/>
   <@script src="${url.context}/res/components/data-lists/datagrid.js" group="datalists" />
</@>

<@markup id="widgets">
   <@createWidgets group="datalists"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <!--[if IE]>
         <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe> 
      <![endif]-->
      <input id="yui-history-field" type="hidden" />
      <#assign id = args.htmlid>
      <div id="${id}-body" class="datagrid">
         <div class="datagrid-meta">
            <h2 id="${id}-title"></h2>
            <div id="${id}-description" class="datagrid-description"></div>
         </div>
         <div id="${id}-datagridBar" class="yui-ge datagrid-bar flat-button">
            <div class="yui-u first align-center">
               <div class="item-select">
                  <button id="${args.htmlid}-itemSelect-button" name="datagrid-itemSelect-button">${msg("menu.select")}</button>
                  <div id="${args.htmlid}-itemSelect-menu" class="yuimenu">
                     <div class="bd">
                        <ul>
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
               <div class="items-per-page" style="visibility: hidden;">
                  <button id="${id}-itemsPerPage-button">${msg("menu.items-per-page")}</button>
               </div>
            </div>
         </div>
         <div id="${id}-grid" class="grid"></div>
         <div id="${id}-selectListMessage" class="hidden select-list-message">${msg("message.select-list")}</div>
         <div id="${id}-datagridBarBottom" class="yui-ge datagrid-bar datagrid-bar-bottom flat-button">
            <div class="yui-u first align-center">
               <div class="item-select">&nbsp;</div>
               <div id="${id}-paginatorBottom" class="paginator"></div>
            </div>
         </div>
         <!-- Action Sets -->
         <div style="display:none">
            <!-- Action Set "More..." container -->
            <div id="${args.htmlid}-moreActions">
               <div class="onActionShowMore"><a href="#" class="show-more" title="${msg("actions.more")}"><span>${msg("actions.more")}</span></a></div>
               <div class="more-actions hidden"></div>
            </div>
            <!-- Action Set Templates -->
            <div id="${args.htmlid}-actionSet" class="action-set simple">
            <#list actionSet as action>
               <div class="${action.id}"><a rel="${action.permission!""}" href="${action.href}" class="${action.type}" title="${msg(action.label)}"><span>${msg(action.label)}</span></a></div>
            </#list>
            </div>
         </div>
      </div>
   </@>
</@>