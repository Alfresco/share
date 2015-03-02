<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/data-lists/data-lists-list.css" group="datalists"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/data-lists/data-lists-list.js" group="datalists"/>
</@>

<@markup id="widgets">
   <@createWidgets group="datalists"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-body" class="lists">
         <span id="datalist-newListBtn" class="yui-button yui-push-button">
             <em class="first-child">
                 <button type="button" id="newListBtn-button">${msg('label.new-list')}</button>
             </em>
         </span>
         <h2>${msg("header.lists")}</h2>
         <ul class="filterLink">
            <#list lists as list>
            <li><span><a href="${url.context}/page/site/${page.url.templateArgs.site!""}/data-lists?list=${list}" class="filter-link">${list}</a></span></li>
            </#list>
         </ul>
      </div>
   </@>
</@>