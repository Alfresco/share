<@markup id="widgets">
  <@processJsonModel group="invite"/>
</@>

<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/invite/added-users-list.css" group="invite"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/invite/added-users-list.js" group="invite"/>
</@>

<@markup id="html">
  <@uniqueIdDiv>
    <div id="${args.htmlid}-added-users-list-wrapper" class="added-users-list-wrapper">
      <div id="${args.htmlid}-added-users-list" class="added-users-list">
        <div id="${args.htmlid}-added-users-list-bar" class="added-users-list-bar">
          <span id="${args.htmlid}-add-users-button" class="yui-button yui-push-button">
            <span class="first-child">
              <button>${msg("added-users-list.add-button-text")}</button>
            </span>
          </span>
        </div>
        <div id="${args.htmlid}-added-users-list-body" class="body added-users-list-body">
          <div class="added-users-list-tally yui-dt-liner">${msg("added-users-list.tally")}</div>
          <div class="added-users-list-message yui-dt-liner">${msg("added-users-list.empty")}</div>
          <div data-dojo-attach-point="containerNode" id="content"></div>
        </div>
      </div>
    </div>
  </@>
</@>