<#assign el=args.htmlid?html>
    <div id="${el}-controls" class="controls flat-button">
    
        <div class="sidebarBtn">
            <button id="${el}-sidebarBtn" disabled="disabled" title="${msg("button.sidebar")}">
                <img src="${url.context}/res/components/preview/images/sidebar-show-flat-16.png" align="top" height="16" />
            </button>
        </div>
      
        <#list toolbarItems as item>
        <#if item.useWrapper?? && item.useWrapper == true>
        <span<#if item.wrapperClassName??> class="${item.wrapperClassName}"</#if>>
        </#if>
        <#if item.type == "button">
            <button<#if item.id??> id="${el}-${item.id}"</#if><#if item.disabled?? && item.disabled> disabled="disabled"</#if><#if item.title??> title="${item.title}"</#if><#if item.className??> class="${item.className}"</#if>>
                <#if item.icon??>
                <img src="${url.context}/res/${item.icon}" align="top" height="16"/>
                </#if>
                <#if item.label??>${item.label}</#if>
            </button>
        <#elseif item.type == "select">
            <button<#if item.id??> id="${el}-${item.id}Btn"</#if><#if item.disabled?? && item.disabled> disabled="disabled"</#if>></button>
            <select<#if item.id??> id="${el}-${item.id}"</#if>>
            <#list item.options as option>
                <option value="${option.value}">${option.label}</option>
            </#list>
            </select>
        <#elseif item.type == "number">
            <input style="height:1em" type="number"<#if item.id??> id="${el}-${item.id}"</#if> value="1" size="4" min="1"<#if item.disabled?? && item.disabled> disabled="disabled"</#if> />
        <#elseif item.type == "span">
            <span<#if item.id??> id="${el}-${item.id}"</#if><#if item.className??> class="${item.className}"</#if>><#if item.text??>${item.text}</#if></span>
        <#elseif item.type == "separator">
            <div<#if item.id??> id="${el}-${item.id}"</#if><#if item.className??> class="${item.className}"<#else> class="separator"</#if>></div>
        </#if>
        <#if item.useWrapper?? && item.useWrapper == true>
        </span>
        </#if>
        </#list>
    </div>
    
    <div id="${el}-searchDialog" class="searchDialog">
       <div class="hd"></div>
       <div class="bd">
          <div id="${el}-searchControls" class="controlssearch flat-button">
          <#-- Search bar -->
            <span class="yui-button">
               <label for="${el}-findInput">${msg("button.search")}:</label>
               <input id="${el}-findInput" type="search" size="30">
            </span>
            <button id="${el}-findPrevious">
              <img src="${url.context}/res/components/images/back-arrow.png" align="top" height="16" title="${msg("button.previoushit")}"/>           
            </button>
      
            <button id="${el}-findNext">
              <img src="${url.context}/res/components/images/forward-arrow-16.png" align="top" height="16" title="${msg("button.nexthit")}"/>
            </button>
            <span class="buttonHighLightAll">
                <button id="${el}-findHighlightAll" title="${msg("button.highlightall")}">&nbsp;&nbsp;&nbsp;</button>
             </span>
             <span class="buttonMatchCase">
                <button id="${el}-findMatchCase" title="${msg("button.matchcase")}">&nbsp;&nbsp;&nbsp;</button>
             </span>
          </div>
       </div>
       <div class="ft"></div>
    </div>
    
    <div id="${el}-linkDialog" class="linkDialog">
        <div class="hd"></div>
        <div class="bd flat-button">
            <div id="${el}-linkDialog-bg" class="yui-buttongroup">
                <input type="radio" name="target" id="${el}-doc" value="${msg("link.document")}" />
                <input type="radio" name="target" id="${el}-page" value="${msg("link.page")}" checked="checked" />
            </div>
            <div>
                <input type="text" id="${el}-linkDialog-input" value="" />
            </div>
            <div>${msg("link.info")}</div>
        </div>
       <div class="ft"></div>
    </div>

    <div id="${el}-sidebar" class="sidebar">
        <div id="${el}-sidebarTabView" class="yui-navset">
            <ul class="yui-nav">
                <li class="selected"><a href="#${el}-thumbnailView"><em><img src="${url.context}/res/components/preview/images/thumbnail-view-16.png" height="16" /></em></a></li>
                <li><a href="#${el}-outlineView"><em><img src="${url.context}/res/components/preview/images/outline-view-16.png" height="16" /></em></a></li>
            </ul>
            <div class="yui-content">
                <div id="${el}-thumbnailView" class="thumbnailView documentView"></div>
                <div id="${el}-outlineView" class="outlineView"></div>
            </div>
        </div>
    </div>

    <div id="${el}-viewer" class="viewer documentView">
        <a name="${el}"></a>
    </div>