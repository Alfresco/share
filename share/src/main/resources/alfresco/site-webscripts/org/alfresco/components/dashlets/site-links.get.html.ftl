<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/dashlets/site-links.css" group="dashlets"/>
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<#function checkHttpPrefix userUrl>
   <#if userUrl?matches("((.*)://(.*))")><#return ""></#if>
   <#if userUrl?matches("^((.*):(\\d{1,})((/.*){0,}))")><#return "http://"></#if>
   <#if userUrl?index_of(":") == -1><#return "http://"><#else><#return ""></#if>
   <#return "http://"> 
</#function>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign site=page.url.templateArgs.site>
      <div class="dashlet site-links">
         <div class="title">${msg("header.links")}</div>
         <#if userIsNotSiteConsumer>
            <div class="toolbar flat-button">
               <div>
                  <span class="align-right yui-button-align">
                     <span class="first-child">
                        <a href="links-linkedit" class="theme-color-1">
                           <img src="${url.context}/res/components/images/link-16.png" style="vertical-align: text-bottom" width="16" />
                           ${msg("link.createLink")}</a>
                     </span>
                  </span>
               </div>
            </div>
         </#if>
         <div class="body scrollableList" <#if args.height??>style="height: ${args.height?html}px;"</#if>>
         <#if numLinks?? && numLinks!=0>
            <#list links as link>
               <div class="detail-list-item <#if link_index = 0>first-item<#elseif !link_has_next>last-item</#if>">
                  <div>
                     <div class="link">
                        <#if !link.url?? || link.url?string?length<1>
                           ${link.name?html} - ${msg("link.noUrl")}
                        <#else>
                           <a <#if !link.internal>target="_blank"</#if> href="${checkHttpPrefix(link.url)}${link.url?html}" class="theme-color-1">${link.title?html}</a>
                        </#if>
                     </div>
                     <div class="actions">
                        <a id="${args.htmlid}-details-span-${link_index}" href="${url.context}/page/site/${site}/links-view?linkId=${link.name}" class="details" title="${msg("link.details")}">&nbsp;</a>
                     </div>
                  </div>
               </div>
            </#list>
         <#else>
            <div class="detail-list-item first-item last-item">
               <span>${msg("label.noLinks")}</span>
            </div>
         </#if>
         </div>
      </div>
   </@>
</@>
