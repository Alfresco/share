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
        <div id="${args.htmlid}-added-users-list-bar" class="added-users-list-bar alf-invite-panel-header">&nbsp;</div>
        <div id="${args.htmlid}-added-users-list-body" class="body added-users-list-body">
          <div class="added-users-list-tally yui-dt-liner">${msg("added-users-list.tally", "0")}</div>
          <div class="added-users-list-message yui-dt-liner">${msg("added-users-list.empty")}</div>
          <div data-dojo-attach-point="containerNode" id="${args.htmlid}-added-users-list-content"></div>
        </div>
      </div>
    </div>
  </@>
</@>

<@markup id="widgets">
  <@createWidgets group="invite"/>
  <@processJsonModel group="invite"/>
</@>