<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/download/archive-and-download.css"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/download/archive-and-download.js"/>
</@>

<@markup id="widgets">
   <@createWidgets/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div id="${el}-dialog" class="archive-and-download hidden">
         <div class="hd">
            <span id="${el}-title-span">${msg("header.label")}</span>
         </div>
         <div class="bd">
            <div class="status-wrapper">
               <span id="${el}-status-span" class="status">&nbsp;</span>
            </div>
            <div id="${el}-aggregate-data-wrapper">
              <div class="status-wrapper">
                 <span id="${el}-aggregate-status-span" class="status">${msg("status.label")}</span>
                 <span id="${el}-file-count-span" class="status">&nbsp;</span>
              </div>
              <div id="${el}-aggregate-progress-div" class="aggregate-progress-div">
                 <span id="${el}-aggregate-progress-span" class="aggregate-progressSuccess-span">&nbsp;</span>
              </div>
            </div>
            <div class="bdft">
               <input id="${el}-cancelOk-button" type="button" value="${msg("button.cancel")}" tabindex="0"/>
            </div>
         </div>
      </div>
   </@>
</@>