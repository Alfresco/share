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
    <div id="${args.htmlid}-added-users-list" class="added-users-list">
      <div id="${args.htmlid}-added-users-list-bar" class="added-users-list-bar alf-invite-panel-header">
        <div class="alf-label">
          ${msg("added-users-list.title")}
        </div>
        <div id="${args.htmlid}-add-users-button" class="alf-colored-button alf-primary-button">
          <#-- The Add button is inserted here from the invitationlist component. -->
        </div>
      </div>
      <div id="${args.htmlid}-added-users-list-body" class="body added-users-list-body">
        <div class="added-users-list-tally yui-dt-liner hidden"></div>
        <div id="${args.htmlid}-added-users-list-content"></div>
      </div>
    </div>
  </@>
</@>

<@markup id="widgets">
  <@createWidgets group="invite"/>
</@>