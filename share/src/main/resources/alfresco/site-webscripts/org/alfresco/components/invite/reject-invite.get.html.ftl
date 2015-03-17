<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/invite/reject-invite.css" group="invite"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/invite/reject-invite.js" group="invite"/>
</@>

<@markup id="widgets">
   <@createWidgets group="invite"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if (error?? && error)>
         <div class="reject-invite-body">
            <h1>${msg("error.noinvitation.title")}</h1>
            <p>${msg("error.noinvitation.text")}</p>
         </div>
      <#else>
         <#assign inviter = invite.inviter.userName>
         <#if (invite.inviter.firstName?? || invite.inviter.lastName??)>
            <#assign inviter = (invite.inviter.firstName!'' + ' ' + invite.inviter.lastName!'')?html>
         </#if>
         <#assign siteName><#if (invite.site.title?? && invite.site.title?length > 0)>${invite.site.title}<#else>${invite.site.shortName}</#if></#assign>
         <#assign siteMarkup><span class="site-name">${siteName?html}</span></#assign>
         <div class="reject-invite-body">
            <div id="${args.htmlid}-confirm" class="main-content">
               <div class="question">${msg("reject.question", inviter, siteMarkup)}</div>
               <div class="actions">
                  <span id="${args.htmlid}-decline-button" class="yui-button yui-push-button"> 
                     <span class="first-child"> 
                        <input type="button" name="decline-button" value="${msg("action.reject")}"> 
                     </span> 
                  </span> 
                  <span id="${args.htmlid}-accept-button" class="yui-button yui-push-button"> 
                     <span class="first-child"> 
                        <input type="button" name="accept-button" value="${msg("action.accept")}">
                     </span>
                  </span>
               </div>
            </div>
            <div id="${args.htmlid}-declined" class="main-content hidden">
               <p>${msg("message.rejected", inviter, siteMarkup)}<p>
            </div>
            <div id="${args.htmlid}-learn-more" class="learn-more">
               <p>${msg("learn.more")} <a href="http://www.alfresco.com">www.alfresco.com</a></p>
            </div>
         </div>
      </#if>
   </@>
</@>

