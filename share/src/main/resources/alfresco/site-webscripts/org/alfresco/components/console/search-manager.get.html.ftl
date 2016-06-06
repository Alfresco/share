<@markup id="widgets">
   <@createWidgets group="console"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
    <br />
    <h2 style="margin-left: 15px;">
      <b>Search Manager</b>
    </h2>
    <br />
    <h3 style="margin-left: 20px;">
      <span style="font-weight: normal;">${msg("text.search-manager.description")}
      </span>
    </h3>
    <br />
    <h3 style="margin-left: 20px;">
      <span style="font-weight: normal;">${msg("text.search-manager.link-description")}
        <a style="font-weight: normal;"
           title="Search Manager"
           target="_blank"
           href="${url.context}/page/dp/ws/faceted-search-config"><u>${msg("text.search-manager.here")}</u>
        </a>.
      </span>
    </h3>
   </@>
</@>