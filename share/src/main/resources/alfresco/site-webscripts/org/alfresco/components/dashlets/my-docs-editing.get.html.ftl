<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/dashlets/my-docs-editing.css" group="dashlets"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/dashlets/my-docs-editing.js" group="dashlets"/>
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div class="dashlet" id="${el}-my-docs-dashlet">
         <div class="title">${msg("header")}</div>
         <div class="body scrollableList" <#if args.height??>style="height: ${args.height?html}px;"</#if>>

            <div id="${el}-message" class="my-docs-editing-message hidden"></div>
            <div id="${el}-my-docs" class="my-docs-editing">
               <@markup id="documents">
               <div class="hdr">
                  <h3>${msg('text.documents')}</h3>
               </div>
               <div id="${el}-documents" class="hidden"></div>
               <div id="${el}-documents-wait" class="my-docs-editing-wait"></div>
               </@markup>
               <@markup id="content">
               <div class="hdr">
                  <h3>${msg('text.blogposts')}</h3>
               </div>
               <div id="${el}-blogposts" class="hidden"></div>
               <div class="hdr">
                  <h3>${msg('text.wikipages')}</h3>
               </div>
               <div id="${el}-wikipages" class="hidden"></div>
               <div class="hdr">
                  <h3>${msg('text.forumposts')}</h3>
               </div>
               <div id="${el}-forumposts" class="hidden"></div>
               <div id="${el}-content-wait" class="my-docs-editing-wait"></div>
               </@markup>
            </div>

         </div>
      </div>
      <div class="hidden">

         <#-- HTML template for a document item -->
         <@markup id="documentTemplate">
         <div id="${el}-document-template" class="detail-list-item">
            <div class="icon">
               <img title="{name}" width="32" src="${url.context}/res/components/images/filetypes/{fileExt}-file-32.png" {onerror} />
            </div>
            <div class="details">
               <h4><a href="${url.context}/page{site}/documentlibrary?file={filename}&amp;filter=editingMe" class="theme-color-1">{name}</a></h4>
               <div>{editingMessage}</div>
            </div>
         </div>
         </@>

         <@markup id="itemTemplate">
         <#-- HTML template for a blog, wiki or forum item -->
         <div id="${el}-item-template" class="detail-list-item">
            <div class="icon">
               <img src="${url.context}/res/{icon}" alt="{name}" />
            </div>
            <div class="details">
               <h4><a href="{browseURL}" class="theme-color-1">{name}</a></h4>
               <div>{editingMessage}</div>
            </div>
         </div>
         </@>

      </div>
   </@>
</@>